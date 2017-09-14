package com.flightbuddy.search;

import java.time.LocalDate;

public final class ImmutableSearchInputData {
	
	private final String from;
	private final String to;
	private final int maxPrice;
	private final int minPrice;
	private final LocalDate[] dates;
	private final boolean withReturn;
	private final ImmutablePassengers passengers;
	
	public ImmutableSearchInputData(String from, String to, int minPrice, int maxPrice, LocalDate[] dates, boolean withReturn,
			ImmutablePassengers passengers) {
		this.from = from;
		this.to = to;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.dates = new LocalDate[dates.length];
		for (int i = 0; i < dates.length; i++) {
			this.dates[i] = LocalDate.of(dates[i].getYear(), dates[i].getMonth(), dates[i].getDayOfMonth());
		}
		this.withReturn = withReturn;
		this.passengers = passengers;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public int getMaxPrice() {
		return maxPrice;
	}
	
	public int getMinPrice() {
		return minPrice;
	}

	public LocalDate[] getDates() {
		return (LocalDate[]) dates.clone();
	}

	public boolean isWithReturn() {
		return withReturn;
	}

	public ImmutablePassengers getPassengers() {
		return passengers;
	}
}
