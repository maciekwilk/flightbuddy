package com.flightbuddy.search;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

public class SearchInputData {
	
	private String from;
	private String to;
	private String price;
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

	public String getPrice() {
		return price;
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

	public void setPrice(String price) {
		this.price = price;
	}

	public void setDates(LocalDate[] dates) {
		this.dates = dates;
	}

	public void setWithReturn(boolean withReturn) {
		this.withReturn = withReturn;
	}
}
