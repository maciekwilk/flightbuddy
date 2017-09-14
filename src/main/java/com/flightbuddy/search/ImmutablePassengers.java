package com.flightbuddy.search;

public final class ImmutablePassengers {
	
	private final int adultCount;
    private final int childCount;
    private final int infantInLapCount;
    private final int infantInSeatCount;
    private final int seniorCount;
    
    public ImmutablePassengers(int adultCount, int childCount, int infantInLapCount, int infantInSeatCount, int seniorCount) {
		this.adultCount = adultCount;
		this.childCount = childCount;
		this.infantInLapCount = infantInLapCount;
		this.infantInSeatCount = infantInSeatCount;
		this.seniorCount = seniorCount;
	}
    
    public ImmutablePassengers(Passengers passengers) {
    	this.adultCount = passengers.getAdultCount();
		this.childCount = passengers.getChildCount();
		this.infantInLapCount = passengers.getInfantInLapCount();
		this.infantInSeatCount = passengers.getInfantInSeatCount();
		this.seniorCount = passengers.getSeniorCount();
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
