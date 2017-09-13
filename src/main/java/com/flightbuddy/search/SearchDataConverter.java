package com.flightbuddy.search;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.flightbuddy.results.Airline;
import com.flightbuddy.results.Flight;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.Stop;
import com.flightbuddy.schedule.search.ScheduledSearch;

public class SearchDataConverter {

	public static ImmutableSearchInputData convertToImmutable(ScheduledSearch scheduledSearch) {
		String from = scheduledSearch.getFrom();
		String to = scheduledSearch.getTo();
		int minPrice = scheduledSearch.getMinPrice();
		int maxPrice = scheduledSearch.getMaxPrice();
		boolean withReturn = scheduledSearch.isWithReturn();
		LocalDate[] dates = scheduledSearch.getDates().toArray(new LocalDate[]{});
		return new ImmutableSearchInputData(from, to, minPrice, maxPrice, dates, withReturn);
	}
	
	public static ImmutableSearchInputData convertToImmutable(SearchInputData searchInputData) {
		String from = searchInputData.getFrom();
		String to = searchInputData.getTo();
		int minPrice = searchInputData.getMinPrice();
		int maxPrice = searchInputData.getMaxPrice();
		boolean withReturn = searchInputData.isWithReturn();
		LocalDate[] dates = searchInputData.getDates();
		return new ImmutableSearchInputData(from, to, minPrice, maxPrice, dates, withReturn);
	}
	
	public static SearchResult convertToSearchResult(FoundTrip foundTrip) {
		String price = foundTrip.getPrice().toString();
		List<Flight> flights = foundTrip.getFlights();
		List<LocalDateTime> dates = getDates(flights);
		List<Integer> durationsInMins = getDurationsInMins(flights);
		List<String> durations = convertDurationsToHours(durationsInMins);
		List<String> hours = getHours(dates, durationsInMins);
		List<String> trips = getTrips(flights);
		List<Integer> stops = countStops(flights);
		return new SearchResult(price, hours, durations, trips, stops);
	}

	private static List<LocalDateTime> getDates(List<Flight> flights) {
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
}
