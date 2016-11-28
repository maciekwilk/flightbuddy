package com.flightbuddy.google.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Slice {

	private String kind = "qpxexpress#sliceInput";
	private String origin;
	private String destination;
	//YYYY-MM-DD format
	private String date; 
	private int maxStops = 2;
	private int maxConnectionDuration = 180;
	private Cabin preferredCabin = Cabin.COACH; 
	private PermittedDepartureTime permittedDepartureTime = new PermittedDepartureTime();
	@JsonIgnore
	private String[] permittedCarrier;
	@JsonIgnore
	private Alliance alliance;
	@JsonIgnore
	private String[] prohibitedCarrier;

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getMaxStops() {
		return maxStops;
	}

	public void setMaxStops(int maxStops) {
		this.maxStops = maxStops;
	}

	public int getMaxConnectionDuration() {
		return maxConnectionDuration;
	}

	public void setMaxConnectionDuration(int maxConnectionDuration) {
		this.maxConnectionDuration = maxConnectionDuration;
	}

	public Cabin getPreferredCabin() {
		return preferredCabin;
	}

	public void setPreferredCabin(Cabin preferredCabin) {
		this.preferredCabin = preferredCabin;
	}

	public PermittedDepartureTime getPermittedDepartureTime() {
		return permittedDepartureTime;
	}

	public void setPermittedDepartureTime(PermittedDepartureTime permittedDepartureTime) {
		this.permittedDepartureTime = permittedDepartureTime;
	}

	public String[] getPermittedCarrier() {
		return permittedCarrier;
	}

	public void setPermittedCarrier(String[] permittedCarrier) {
		this.permittedCarrier = permittedCarrier;
	}

	public Alliance getAlliance() {
		return alliance;
	}

	public void setAlliance(Alliance alliance) {
		this.alliance = alliance;
	}

	public String[] getProhibitedCarrier() {
		return prohibitedCarrier;
	}

	public void setProhibitedCarrier(String[] prohibitedCarrier) {
		this.prohibitedCarrier = prohibitedCarrier;
	}
	
	private enum Cabin {
		COACH, PREMIUM_COACH, BUSINESS, FIRST;
	}
	
	private enum Alliance {
		ONEWORLD, SKYTEAM, STAR;
	}
}
