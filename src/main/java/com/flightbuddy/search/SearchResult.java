package com.flightbuddy.search;

public final class SearchResult {

	private final String price;
	private final String[] hours;
	private final String duration;
	private final String trip;
	private final int stops;
	
	public SearchResult(String price, String[] hours, String duration, String trip, int stops) {
		this.price = price;
		this.hours = hours;
		this.duration = duration;
		this.trip = trip;
		this.stops = stops;
	}

	public String getPrice() {
		return price;
	}

	public String[] getHours() {
		return hours;
	}

	public String getDuration() {
		return duration;
	}

	public String getTrip() {
		return trip;
	}

	public int getStops() {
		return stops;
	}
}
