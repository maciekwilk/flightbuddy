package com.flightbuddy.google.response;

import com.flightbuddy.google.response.error.Error;

public class GoogleResponse {

	private String kind;
	private Trips trips;
	private Error error;

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}  
	
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public Trips getTrips() {
		return trips;
	}
	public void setTrips(Trips trips) {
		this.trips = trips;
	}  	    
}
