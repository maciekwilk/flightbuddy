package com.flightbuddy.google.request;

public class PermittedDepartureTime {

	private String kind = "qpxexpress#timeOfDayRange";
	//HH:MM format
    private String earliestTime = "00:01";	
    //HH:MM format
    private String latestTime = "23:59";
	
	public String getKind() {
		return kind;
	}
	
	public void setKind(String kind) {
		this.kind = kind;
	}
	
	public String getEarliestTime() {
		return earliestTime;
	}
	
	public void setEarliestTime(String earliestTime) {
		this.earliestTime = earliestTime;
	}
	
	public String getLatestTime() {
		return latestTime;
	}
	
	public void setLatestTime(String latestTime) {
		this.latestTime = latestTime;
	}
}
