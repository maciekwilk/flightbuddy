package com.flightbuddy.search;

import com.flightbuddy.schedule.search.Passengers;

public final class ImmutablePassengers {
	
	private final int adultCount;
    private final int childCount;
    private final int infantInLapCount;
    private final int infantInSeatCount;
    private final int seniorCount;
    
    ImmutablePassengers(Passengers passengers) {
    	this.adultCount = passengers.getAdultCount();
		this.childCount = passengers.getChildCount();
		this.infantInLapCount = passengers.getInfantInLapCount();
		this.infantInSeatCount = passengers.getInfantInSeatCount();
		this.seniorCount = passengers.getSeniorCount();
	}
    
    public ImmutablePassengers(PassengersTO passengers) {
    	this.adultCount = passengers.adultCount;
		this.childCount = passengers.childCount;
		this.infantInLapCount = passengers.infantInLapCount;
		this.infantInSeatCount = passengers.infantInSeatCount;
		this.seniorCount = passengers.seniorCount;
    }

	public int getAdultCount() {
		return adultCount;
	}
	
	public int getChildCount() {
		return childCount;
	}
	
	public int getInfantInLapCount() {
		return infantInLapCount;
	}
	
	public int getInfantInSeatCount() {
		return infantInSeatCount;
	}
	
	public int getSeniorCount() {
		return seniorCount;
	}
}
