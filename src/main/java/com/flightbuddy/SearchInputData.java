package com.flightbuddy;

import java.time.LocalDate;

public class SearchInputData {
	
	private String from;
	private String to;
	private String price;
	private LocalDate[] dates;
	private boolean withReturn;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public LocalDate[] getDates() {
		return dates;
	}
	public void setDates(LocalDate[] dates) {
		this.dates = dates;
	}

	public boolean isWithReturn() {
		return withReturn;
	}

	public void setWithReturn(boolean withReturn) {
		this.withReturn = withReturn;
	}
}
