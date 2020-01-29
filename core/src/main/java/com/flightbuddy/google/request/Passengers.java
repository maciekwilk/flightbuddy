package com.flightbuddy.google.request;

public class Passengers {

	private String kind = "qpxexpress#passengerCounts";
    private int adultCount = 1;
    private int childCount;
    private int infantInLapCount;
    private int infantInSeatCount;
    private int seniorCount;

	public int getAdultCount() {
		return adultCount;
	}
	public void setAdultCount(int adultCount) {
		this.adultCount = adultCount;
	}
	public int getChildCount() {
		return childCount;
	}
	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}
	public int getInfantInLapCount() {
		return infantInLapCount;
	}
	public void setInfantInLapCount(int infantInLapCount) {
		this.infantInLapCount = infantInLapCount;
	}
	public int getInfantInSeatCount() {
		return infantInSeatCount;
	}
	public void setInfantInSeatCount(int infantInSeatCount) {
		this.infantInSeatCount = infantInSeatCount;
	}
	public int getSeniorCount() {
		return seniorCount;
	}
	public void setSeniorCount(int seniorCount) {
		this.seniorCount = seniorCount;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
}
