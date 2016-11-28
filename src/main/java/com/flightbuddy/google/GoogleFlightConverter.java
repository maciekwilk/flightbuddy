package com.flightbuddy.google;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

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

@Component
public class GoogleFlightConverter {

	private static final LocalDate DEFAULT_DATE = LocalDate.of(2000, 1, 1);

	public List<FoundTrip> convertResponseToTrips(GoogleResponse googleResponse) {
		Trips trips = googleResponse.getTrips();
		TripData tripData = trips.getData();
		TripOption[] tripOptions = trips.getTripOption();
		return getFoundTrips(tripOptions, tripData);
	}

	private List<FoundTrip> getFoundTrips(TripOption[] tripOptions, TripData tripData) {
		List<FoundTrip> trips = new ArrayList<>(tripOptions.length);
		for (TripOption tripOption : tripOptions) {
			FoundTrip trip = getFoundTrip(tripOption, tripData);
			trips.add(trip);
		}
		return trips;
	}

	private FoundTrip getFoundTrip(TripOption tripOption, TripData tripData) {
		FoundTrip foundTrip = new FoundTrip();
		BigDecimal price = getPrice(tripOption);
		foundTrip.setPrice(price); 
		Slice[] slices = tripOption.getSlice();
		List<Flight> flights = getFlights(foundTrip, slices, tripData);
		foundTrip.setFlights(flights);
		return foundTrip;
	}

	private BigDecimal getPrice(TripOption tripOption) {
		String priceWithCurrency = tripOption.getSaleTotal();
		if (priceWithCurrency != null && !priceWithCurrency.isEmpty()) {
			String price = priceWithCurrency.substring(3);
			if (Pattern.matches(RegularExpressions.STRING_TO_DOUBLE_CHECK, price)) {
				return new BigDecimal(price);
			}
		}
		return BigDecimal.ZERO;
	}

	private List<Flight> getFlights(FoundTrip foundTrip, Slice[] slices, TripData tripData) {
		List<Flight> flights = new ArrayList<>(2);
		for (Slice slice : slices) {
			Flight flight = createFlight(foundTrip, slice, tripData);
			flights.add(flight);
		}
		return flights;
	}
	
	private Flight createFlight(FoundTrip foundTrip, Slice slice, TripData tripData) {
		Flight flight = new Flight();
		flight.setFoundTrip(foundTrip);
		for (Segment segment : slice.getSegment()) {
			extractFlightInfoFromSegment(flight, segment, tripData);
		}
		return flight;
	}

	private void extractFlightInfoFromSegment(Flight flight, Segment segment, TripData tripData) {
		if (segment.getFlight() != null && segment.getFlight().getCarrier() != null) {
			addAirlineToFlight(flight, segment, tripData);
		}
		for (Leg leg : segment.getLeg()) {
			extractFlightInfoFromLeg(flight, leg);
		}
	}

	private void addAirlineToFlight(Flight flight, Segment segment, TripData tripData) {
		ResponseFlight responseFlight = segment.getFlight();
		String carrier = responseFlight.getCarrier();
		Airline airline = createAirline(flight, carrier, tripData);
		List<Airline> airlines = getAirlines(flight);
		airlines.add(airline);
		flight.setAirlines(airlines);
	}
	
	private Airline createAirline(Flight flight, String carrier, TripData tripData) {
		Airline airline = new Airline();
		String name = getAirlineName(carrier, tripData);
		airline.setName(name);
		airline.setFlight(flight);
		return airline;
	}

	private String getAirlineName(String airlineShort, TripData tripData) {
		if (tripData != null && tripData.getCarrier() != null) {
			for (Carrier carrier : tripData.getCarrier()) {
				if (airlineShort.equals(carrier.getCode())) {
					return carrier.getName();
				}
			}
		}
		return airlineShort;
	}

	private List<Airline> getAirlines(Flight flight) {
		List<Airline> airlines = flight.getAirlines();
		if (airlines == null) {
			airlines = new ArrayList<>();
		}
		return airlines;
	}

	private void extractFlightInfoFromLeg(Flight flight, Leg leg) {
		LocalDate date = flight.getDate();
		List<Stop> stops = getStops(flight);
		if (date == null) {
			addDateAndFirstStops(flight, leg, stops);
		} else {
			Stop destination = createStop(flight, leg.getDestination());
			stops.add(destination);
		}
		flight.setStops(stops);
	}

	private List<Stop> getStops(Flight flight) {
		List<Stop> stops = flight.getStops();
		if (stops == null) {
			stops = new ArrayList<>();
		}
		return stops;
	}

	private void addDateAndFirstStops(Flight flight, Leg leg, List<Stop> stops) {
		LocalDate date;
		date = getDate(leg);
		flight.setDate(date);
		Stop origin = createStop(flight, leg.getOrigin());
		stops.add(origin);
		Stop destination = createStop(flight, leg.getDestination());
		stops.add(destination);
	}

	private LocalDate getDate(Leg leg) {
		String departureTime = leg.getDepartureTime();
		if (departureTime != null && Pattern.matches(RegularExpressions.ISO_OFFSET_DATE_TIME, departureTime)) {
			DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
			return LocalDate.parse(departureTime, formatter);
		}
		return DEFAULT_DATE;
	}
	
	private Stop createStop(Flight flight, String stopCode) {
		Stop stop = new Stop();
		stop.setCode(stopCode);
		stop.setFlight(flight);
		return stop;
	}
}
