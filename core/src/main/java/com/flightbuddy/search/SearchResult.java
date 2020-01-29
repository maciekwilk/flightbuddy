package com.flightbuddy.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class SearchResult {

	private final String id;
	private final int price;
	private final List<String> hours;
	private final List<String> dates;
	private final List<String> durations;
	private final List<String> trips;
	private final List<Integer> stops;
	private final List<SearchResultDetails> searchResultDetails;
	
	SearchResult(int price, List<String> hours, List<String> durations, List<String> trips, List<Integer> stops,
				 List<SearchResultDetails> searchResultDetails, List<String> dates) {
		this.id = UUID.randomUUID().toString();
		this.price = price;
		this.hours = Collections.unmodifiableList(new ArrayList<>(hours));
		this.dates = Collections.unmodifiableList(new ArrayList<>(dates));
		this.durations = Collections.unmodifiableList(new ArrayList<>(durations));
		this.trips = Collections.unmodifiableList(new ArrayList<>(trips));
		this.stops = Collections.unmodifiableList(new ArrayList<>(stops));
		this.searchResultDetails = Collections.unmodifiableList(new ArrayList<>(searchResultDetails));
	}

	public int getPrice() {
		return price;
	}

	public List<String> getHours() {
		return hours;
	}

	public List<String> getDurations() {
		return durations;
	}

	public List<String> getTrips() {
		return trips;
	}

	public List<Integer> getStops() {
		return stops;
	}
	
	public String getId() {
		return id;
	}

	public List<SearchResultDetails> getSearchResultDetails() {
		return searchResultDetails;
	}

	public List<String> getDates() {
		return dates;
	}
}
