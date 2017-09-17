package com.flightbuddy.search;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.flightbuddy.results.Airline;
import com.flightbuddy.results.Flight;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.Stop;
import com.flightbuddy.schedule.search.Passengers;
import com.flightbuddy.schedule.search.ScheduledSearch;

public class SearchDataConverter {

	public static ImmutableSearchInputData convertToImmutable(ScheduledSearch scheduledSearch) {
		String from = scheduledSearch.getFrom();
		String to = scheduledSearch.getTo();
		int minPrice = scheduledSearch.getMinPrice();
		int maxPrice = scheduledSearch.getMaxPrice();
		boolean withReturn = scheduledSearch.isWithReturn();
		LocalDate[] dates = scheduledSearch.getDates().toArray(new LocalDate[]{});
		ImmutablePassengers immutablePassengers = copyPassengers(scheduledSearch.getPassengers());
		return new ImmutableSearchInputData(from, to, minPrice, maxPrice, dates, withReturn, immutablePassengers);
	}

	public static ImmutableSearchInputData convertToImmutable(SearchInputData searchInputData) {
		String from = searchInputData.getFrom();
		String to = searchInputData.getTo();
		int minPrice = searchInputData.getMinPrice();
		int maxPrice = searchInputData.getMaxPrice();
		boolean withReturn = searchInputData.isWithReturn();
		LocalDate[] dates = searchInputData.getDates();
		ImmutablePassengers immutablePassengers = new ImmutablePassengers(searchInputData.getPassengers());
		return new ImmutableSearchInputData(from, to, minPrice, maxPrice, dates, withReturn, immutablePassengers);
	}
	
	public static SearchResult convertToSearchResult(FoundTrip foundTrip) {
		int price = foundTrip.getPrice().intValue();
		List<Flight> flights = foundTrip.getFlights();
		List<LocalDateTime> localDateTimes = getLocalDateTimes(flights);
		List<Integer> durationsInMins = getDurationsInMins(flights);
		List<String> durations = convertDurationsToHours(durationsInMins);
		List<String> hours = getHours(localDateTimes, durationsInMins);
		List<String> dates = getDates(localDateTimes);
		List<String> trips = getTrips(flights);
		List<Integer> stops = countStops(flights);
		List<SearchResultDetails> searchResultDetails = getSearchResultDetails(flights);
		return new SearchResult(price, hours, durations, trips, stops, searchResultDetails, dates);
	}

	private static ImmutablePassengers copyPassengers(Passengers passengers) {
		return new ImmutablePassengers(passengers.getAdultCount(), passengers.getChildCount(), passengers.getInfantInLapCount(), 
				passengers.getInfantInSeatCount(), passengers.getSeniorCount());
	}

	private static List<LocalDateTime> getLocalDateTimes(List<Flight> flights) {
		return flights.stream()
				.map(flight -> flight.getDate())
				.collect(Collectors.toList());
	}

	private static List<Integer> getDurationsInMins(List<Flight> flights) {
		return flights.stream()
				.map(flight -> flight.getDuration())
				.collect(Collectors.toList());
	}

	private static List<String> convertDurationsToHours(List<Integer> durationsInMins) {
		return durationsInMins.stream()
				.map(duration -> convertMinsToHours(duration))
				.collect(Collectors.toList());
	}

	private static String convertMinsToHours(int duration) {
		int mins = duration % 60;
		String minsString = convertMins(mins) + "m";
		int hours = duration / 60;
		if (hours > 0 && mins > 0) {
			return hours + "h " + minsString;
		} else if (hours > 0) {
			return hours + "h";
		} else {
			return minsString;
		}
	}

	private static List<String> getHours(List<LocalDateTime> dates, List<Integer> durationsInMins) {
		return IntStream.range(0, dates.size())
					.mapToObj(i -> convertToTripTimes(dates.get(i), durationsInMins.get(i)))
					.collect(Collectors.toList());
	}

	private static String convertToTripTimes(LocalDateTime tripStartDate, int tripDuration) {
		LocalDateTime tripEndDate = tripStartDate.plusMinutes(tripDuration);
		String tripStartTime = tripStartDate.getHour() + ":" + convertMins(tripStartDate.getMinute());
		String tripEndTime = tripEndDate.getHour() + ":" + convertMins(tripEndDate.getMinute());
		String tripTimes = tripStartTime + "-" + tripEndTime;
		return tripTimes;
	}

	private static String convertMins(int mins) {
		if (mins < 10) {
			return "0" + mins;
		} 
		return String.valueOf(mins);
	}
	
	private static List<String> getDates(List<LocalDateTime> localDateTimes) {
		return localDateTimes.stream()
				.map(localDateTime -> localDateTime.getDayOfWeek() + ", " + localDateTime.getMonth() + " " + localDateTime.getDayOfMonth())
				.collect(Collectors.toList());				
	}

	private static List<String> getTrips(List<Flight> flights) {
		return flights.stream()
				.map(flight -> convertStops(flight.getStops()) + " (" + convertAirlines(flight.getAirlines()) + ")")
				.collect(Collectors.toList());
	}

	private static String convertStops(List<Stop> stops) {
		return stops.stream()
				.map(stop -> stop.getCode())
				.collect(Collectors.joining(" -> "));
	}
	
	private static String convertAirlines(List<Airline> airlines) {
		return airlines.stream()
				.map(airline -> airline.getName())
				.collect(Collectors.joining(" -> "));
	}

	private static List<Integer> countStops(List<Flight> flights) {
		return flights.stream()
				.map(flight -> flight.getStops().size() - 2)
				.collect(Collectors.toList());
	}
	
	private static List<SearchResultDetails> getSearchResultDetails(List<Flight> flights) {
		return flights.stream()
				.map(flight -> getSearchResultDetails(flight))
				.collect(Collectors.toList());
	}

	private static SearchResultDetails getSearchResultDetails(Flight flight) {
		List<Stop> stops = flight.getStops();
		List<Airline> airlines = flight.getAirlines();
		List<SearchResultDetailsEntry> searchResultDetails = new ArrayList<>();
		SearchResultDetailsEntry firstSearchResultDetailsEntry = createSearchResultDetailsEntry(stops.get(0), stops.get(1), airlines.get(0));
		searchResultDetails.add(firstSearchResultDetailsEntry);
		for (int i = 1; i < stops.size() - 1; i++) {
			addNextEntries(stops, airlines, searchResultDetails, i);
		}
		return new SearchResultDetails(searchResultDetails);
	}

	private static void addNextEntries(List<Stop> stops, List<Airline> airlines,
			List<SearchResultDetailsEntry> searchResultDetails, int i) {
		SearchResultDetailsEntry transferSearchResultDetailsEntry = createTransferSearchResultDetailsEntry(stops.get(i));
		SearchResultDetailsEntry searchResultDetailsEntry = createSearchResultDetailsEntry(stops.get(i), stops.get(i + 1), airlines.get(i));
		searchResultDetails.add(transferSearchResultDetailsEntry);
		searchResultDetails.add(searchResultDetailsEntry);
	}

	private static SearchResultDetailsEntry createSearchResultDetailsEntry(Stop origin, Stop destination, Airline airline) {
		LocalDateTime departureTime = origin.getDepartureTime();
		LocalDateTime arrivalTime = destination.getArrivalTime();
		int durationAsInt = getDuration(departureTime, arrivalTime);
		String hours = convertToTripTimes(departureTime, durationAsInt);
		String duration = convertMinsToHours(durationAsInt);
		String trip = getTripForDetails(origin, destination);
		return new SearchResultDetailsEntry(hours, duration, trip, airline.getName());
	}

	private static SearchResultDetailsEntry createTransferSearchResultDetailsEntry(Stop stop) {
		LocalDateTime departureTime = stop.getDepartureTime();
		LocalDateTime arrivalTime = stop.getArrivalTime();
		int durationAsInt = getDuration(arrivalTime, departureTime);
		String duration = convertMinsToHours(durationAsInt);
		return new SearchResultDetailsEntry(null, duration, null, null);
	}
	
	private static int getDuration(LocalDateTime startDate, LocalDateTime endDate) {
		Duration duration = Duration.between(startDate, endDate);
		return (int) duration.toMinutes();
	}

	private static String getTripForDetails(Stop origin, Stop destination) {
		return origin.getCode() + " -> " + destination.getCode();
	}
}
