package com.flightbuddy;

import java.time.LocalDate;

public final class SearchInputData {
	
	private final String from;
	private final String to;
	private final String price;
	private final LocalDate[] dates;
	private final boolean withReturn;
	
	public SearchInputData(String from, String to, String price, LocalDate[] dates, boolean withReturn) {
		this.from = from;
		this.to = to;
		this.price = price;
		this.dates = new LocalDate[dates.length];
		for (int i = 0; i < dates.length; i++) {
			this.dates[i] = LocalDate.of(dates[i].getYear(), dates[i].getMonth(), dates[i].getDayOfMonth());
		}
		this.withReturn = withReturn;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getPrice() {
		return price;
	}

	public LocalDate[] getDates() {
		return (LocalDate[]) dates.clone();
	}

	public boolean isWithReturn() {
		return withReturn;
	}
}
