package com.flightbuddy.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SearchResult {

	private final String price;
	private final List<String> hours;
	private final List<String> durations;
	private final List<String> trips;
	private final List<Integer> stops;
	
	public SearchResult(String price, List<String> hours, List<String> durations, List<String> trips, List<Integer> stops) {
		this.price = price;
		this.hours = Collections.unmodifiableList(new ArrayList<String>(hours));
		this.durations = Collections.unmodifiableList(new ArrayList<String>(durations));
		this.trips = Collections.unmodifiableList(new ArrayList<String>(trips));
		this.stops = Collections.unmodifiableList(new ArrayList<Integer>(stops));
	}

	public String getPrice() {
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
}
