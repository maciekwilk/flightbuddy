package com.flightbuddy.search;

public final class SearchResultDetailsEntry {

	private final String hours;
	private final String duration;
	private final String trip;
	private final String airline;
	
	public SearchResultDetailsEntry(String hours, String duration, String trip, String airline) {
		this.hours = hours;
		this.duration = duration;
		this.trip = trip;
		this.airline = airline;
	}

	public String getHours() {
		return hours;
	}

	public String getDuration() {
		return duration;
	}

	public String getTrip() {
		return trip;
	}

	public String getAirline() {
		return airline;
	}
}
