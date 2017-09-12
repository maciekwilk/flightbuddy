package com.flightbuddy.search;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

public class SearchInputData {
	
	private String from;
	private String to;
	private int minPrice;
	private int maxPrice;
	@JsonSerialize(contentUsing = LocalDateSerializer.class)
	@JsonDeserialize(contentUsing = LocalDateDeserializer.class)
	private LocalDate[] dates;
	private boolean withReturn;
	
	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public int getMinPrice() {
		return minPrice;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public LocalDate[] getDates() {
		return dates;
	}

	public boolean isWithReturn() {
		return withReturn;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}

	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}

	public void setDates(LocalDate[] dates) {
		this.dates = dates;
	}

	public void setWithReturn(boolean withReturn) {
		this.withReturn = withReturn;
	}
}
