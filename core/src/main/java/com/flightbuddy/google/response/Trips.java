package com.flightbuddy.google.response;

import com.flightbuddy.google.response.tripdata.TripData;
import com.flightbuddy.google.response.tripoption.TripOption;

public class Trips {

	private String kind;
    private String requestId;
    private TripData data;
    private TripOption[] tripOption;
    
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public TripData getData() {
		return data;
	}
	public void setData(TripData data) {
		this.data = data;
	}
	public TripOption[] getTripOption() {
		return tripOption;
	}
	public void setTripOption(TripOption[] tripOption) {
		this.tripOption = tripOption;
	}
}
