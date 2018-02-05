package com.flightbuddy.google;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.flightbuddy.google.response.GoogleResponse;
import com.flightbuddy.google.response.Trips;
import com.flightbuddy.google.response.tripdata.Carrier;
import com.flightbuddy.google.response.tripdata.TripData;
import com.flightbuddy.google.response.tripoption.TripOption;
import com.flightbuddy.google.response.tripoption.slice.Leg;
import com.flightbuddy.google.response.tripoption.slice.ResponseFlight;
import com.flightbuddy.google.response.tripoption.slice.Segment;
import com.flightbuddy.google.response.tripoption.slice.Slice;
import com.flightbuddy.results.Airline;
import com.flightbuddy.results.Flight;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.Stop;
import com.flightbuddy.utils.RegularExpressions;

class GoogleFlightConverter {

	private static final LocalDateTime DEFAULT_DATE = LocalDateTime.of(2000, 1, 1, 12, 30);

	public static List<FoundTrip> convertResponseToTrips(GoogleResponse googleResponse, int minPrice) {
		Trips trips = googleResponse.getTrips();
		if (trips == null || trips.getTripOption() == null || trips.getTripOption().length == 0) {
			return Collections.emptyList();
		}
		TripData tripData = trips.getData();
		TripOption[] tripOptions = trips.getTripOption();
		return getFoundTrips(tripOptions, tripData, minPrice);
	}

	private static List<FoundTrip> getFoundTrips(TripOption[] tripOptions, TripData tripData, int minPrice) {
		List<FoundTrip> trips = new ArrayList<>(tripOptions.length);
		Arrays.stream(tripOptions).filter((tripOption) -> getPrice(tripOption).compareTo(new BigDecimal(minPrice)) >= 0)
					.forEach((tripOption) -> {
						FoundTrip trip = getFoundTrip(tripOption, tripData);
						trips.add(trip);
					});
		return trips;
	}

	private static FoundTrip getFoundTrip(TripOption tripOption, TripData tripData) {
		FoundTrip foundTrip = new FoundTrip();
		BigDecimal price = getPrice(tripOption);
		foundTrip.setPrice(price); 
		Slice[] slices = tripOption.getSlice();
		List<Flight> flights = getFlights(foundTrip, slices, tripData);
		foundTrip.setFlights(flights);
		return foundTrip;
	}

	private static BigDecimal getPrice(TripOption tripOption) {
		String priceWithCurrency = tripOption.getSaleTotal();
		if (priceWithCurrency != null && !priceWithCurrency.isEmpty()) {
			String price = priceWithCurrency.substring(3);
			if (Pattern.matches(RegularExpressions.STRING_TO_DOUBLE_CHECK, price)) {
				return new BigDecimal(price);
			}
		}
		return BigDecimal.ZERO;
	}

	private static List<Flight> getFlights(FoundTrip foundTrip, Slice[] slices, TripData tripData) {
		List<Flight> flights = new ArrayList<>(2);
		Arrays.stream(slices).forEach((slice) -> {
			Flight flight = createFlight(foundTrip, slice, tripData);
			flights.add(flight);
		});
		return flights;
	}
	
	private static Flight createFlight(FoundTrip foundTrip, Slice slice, TripData tripData) {
		Flight flight = new Flight();
		flight.setFoundTrip(foundTrip);
		flight.setDuration(slice.getDuration());
		Arrays.stream(slice.getSegment()).forEach((segment) ->
				extractFlightInfoFromSegment(flight, segment, tripData));
		return flight;
	}

	private static void extractFlightInfoFromSegment(Flight flight, Segment segment, TripData tripData) {
		if (segment.getFlight() != null && segment.getFlight().getCarrier() != null) {
			addAirlineToFlight(flight, segment, tripData);
		}
		Arrays.stream(segment.getLeg()).forEach((leg) ->
				extractFlightInfoFromLeg(flight, leg));
	}

	private static void addAirlineToFlight(Flight flight, Segment segment, TripData tripData) {
		ResponseFlight responseFlight = segment.getFlight();
		String carrier = responseFlight.getCarrier();
		Airline airline = createAirline(flight, carrier, tripData);
		List<Airline> airlines = getAirlines(flight);
		airlines.add(airline);
		flight.setAirlines(airlines);
	}
	
	private static Airline createAirline(Flight flight, String carrier, TripData tripData) {
		Airline airline = new Airline();
		String name = getAirlineName(carrier, tripData);
		airline.setName(name);
		airline.setFlight(flight);
		return airline;
	}

	private static String getAirlineName(String airlineShort, TripData tripData) {
		if (tripData != null && tripData.getCarrier() != null) {
			return Arrays.stream(tripData.getCarrier()).filter(
						(carrier) -> airlineShort.equals(carrier.getCode())
					).findFirst().map(Carrier::getName).orElse(airlineShort);
		}
		return airlineShort;
	}

	private static List<Airline> getAirlines(Flight flight) {
		List<Airline> airlines = flight.getAirlines();
		if (airlines == null) {
			airlines = new ArrayList<>();
		}
		return airlines;
	}

	private static void extractFlightInfoFromLeg(Flight flight, Leg leg) {
		LocalDateTime date = flight.getDate();
		List<Stop> stops = getStops(flight);
		if (date == null) {
			addDateAndFirstStops(flight, leg, stops);
		} else {
			addNextStops(flight, leg, stops);
		}
		flight.setStops(stops);
	}

	private static List<Stop> getStops(Flight flight) {
		List<Stop> stops = flight.getStops();
		if (stops == null) {
			stops = new ArrayList<>();
		}
		return stops;
	}

	private static void addDateAndFirstStops(Flight flight, Leg leg, List<Stop> stops) {
		LocalDateTime departureTime = getDate(leg.getDepartureTime());
		LocalDateTime arrivalTime = getDate(leg.getArrivalTime());
		flight.setDate(departureTime);
		Stop origin = createStop(flight, leg.getOrigin(), null, departureTime);
		stops.add(origin);
		Stop destination = createStop(flight, leg.getDestination(), arrivalTime, null);
		stops.add(destination);
	}

	private static void addNextStops(Flight flight, Leg leg, List<Stop> stops) {
		LocalDateTime departureTime = getDate(leg.getDepartureTime());
		LocalDateTime arrivalTime = getDate(leg.getArrivalTime());
		Stop origin = stops.get(stops.size() - 1);
		origin.setDepartureTime(departureTime);
		Stop destination = createStop(flight, leg.getDestination(), arrivalTime, null);
		stops.add(destination);
	}

	private static LocalDateTime getDate(String date) {
		if (date != null && Pattern.matches(RegularExpressions.ISO_OFFSET_DATE_TIME, date)) {
			DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
			return LocalDateTime.parse(date, formatter);
		}
		return DEFAULT_DATE;
	}
	
	private static Stop createStop(Flight flight, String stopCode, LocalDateTime arrivalTime, LocalDateTime departureTime) {
		Stop stop = new Stop();
		stop.setArrivalTime(arrivalTime);
		stop.setDepartureTime(departureTime);
		stop.setCode(stopCode);
		stop.setFlight(flight);
		return stop;
	}
}
