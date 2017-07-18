package com.flightbuddy.search;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.flightbuddy.results.Flight;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.schedule.search.ScheduledSearch;

public class SearchDataConverter {

	public static ImmutableSearchInputData convertToImmutable(ScheduledSearch scheduledSearch) {
		String from = scheduledSearch.getFrom();
		String to = scheduledSearch.getTo();
		String price = String.valueOf(scheduledSearch.getPrice());
		boolean withReturn = scheduledSearch.isWithReturn();
		LocalDate[] dates = scheduledSearch.getDates().toArray(new LocalDate[]{});
		return new ImmutableSearchInputData(from, to, price, dates, withReturn);
	}
	
	public static ImmutableSearchInputData convertToImmutable(SearchInputData searchInputData) {
		String from = searchInputData.getFrom();
		String to = searchInputData.getTo();
		String price = searchInputData.getPrice();
		boolean withReturn = searchInputData.isWithReturn();
		LocalDate[] dates = searchInputData.getDates();
		return new ImmutableSearchInputData(from, to, price, dates, withReturn);
	}
	
	public static SearchResult convertToSearchResult(FoundTrip foundTrip) {
		String price = foundTrip.getPrice().toString();
		List<Flight> flights = foundTrip.getFlights();
		List<LocalDateTime> dates = getDates(flights);
		List<String> hours = getHours(dates);
		List<String> durations = getDurations(flights);
		List<String> trips = getTrips(flights);
		List<Integer> stops = countStops(flights);
		return new SearchResult(price, hours, durations, trips, stops);
	}

	private static List<LocalDateTime> getDates(List<Flight> flights) {
		List<LocalDateTime> dates = flights.stream()
											.map(flight -> flight.getDate())
											.collect(Collectors.toList());
		return dates;
	}

	private static List<String> getHours(List<LocalDateTime> dates) {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	private static List<String> getDurations(List<Flight> flights) {
		List<Integer> durationsInMins = flights.stream()
											.map(flight -> flight.getDuration())
											.collect(Collectors.toList());
		return calculateDurations(durationsInMins);
	}

	private static List<String> calculateDurations(List<Integer> durationsInMins) {
		List<String> durationStrings = durationsInMins.stream()
														.map(duration -> convertMinsToHours(duration))
														.collect(Collectors.toList());
		return durationStrings;
	}

	private static String convertMinsToHours(int duration) {
		int mins = duration % 60;
		String minsString = convertMins(mins);
		int hours = duration / 60;
		if (hours > 0) {
			return hours + "h " + minsString;
		} else {
			return minsString;
		}
	}

	private static String convertMins(int mins) {
		if (mins == 0) {
			return "";
		} else if (mins < 10) {
			return "0" + mins + "m";
		} else {
			return mins + "m";
		}
	}

	private static List<String> getTrips(List<Flight> flights) {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	private static List<Integer> countStops(List<Flight> flights) {
		int stops = 0;
//		flights.stream().forEach(flight -> stops += flight.getStops().size());
		return Collections.emptyList();
	}
}
