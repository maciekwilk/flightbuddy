package com.flightbuddy.google.request;

public class GoogleRequest {

	private Request request;

	public GoogleRequest() {}

	public GoogleRequest(Request request) {
		this.request = request;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}
	
}
