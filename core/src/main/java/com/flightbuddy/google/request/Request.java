package com.flightbuddy.google.request;

/**
 * A QPX Express search request. Required values are at least one adult or senior passenger, an origin, a destination, and a date.
 * @author mwilk
 *
 */
public class Request {

	private Passengers passengers;
	private Slice[] slice;
	//The currency is specified in ISO-4217. The format, in regex, is [A-Z]{3}\d+(\.\d+)?
	private String maxPrice;
	private String saleCountry = "DE";
	private String ticketingCountry = "DE";
	private boolean refundable = false;
	//Number of solutions to return. max 500
	private int solutions = 500;
	
	public Passengers getPassengers() {
		return passengers;
	}

	public void setPassengers(Passengers passengers) {
		this.passengers = passengers;
	}

	public Slice[] getSlice() {
		return slice;
	}

	public void setSlice(Slice[] slice) {
		this.slice = slice;
	}

	public String getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(String maxPrice) {
		this.maxPrice = maxPrice;
	}

	public String getSaleCountry() {
		return saleCountry;
	}

	public void setSaleCountry(String saleCountry) {
		this.saleCountry = saleCountry;
	}

	public String getTicketingCountry() {
		return ticketingCountry;
	}

	public void setTicketingCountry(String ticketingCountry) {
		this.ticketingCountry = ticketingCountry;
	}

	public boolean isRefundable() {
		return refundable;
	}

	public void setRefundable(boolean refundable) {
		this.refundable = refundable;
	}

	public int getSolutions() {
		return solutions;
	}

	public void setSolutions(int solutions) {
		this.solutions = solutions;
	}
}
