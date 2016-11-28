package com.flightbuddy.exceptions;

@SuppressWarnings("serial")
public class RequestException extends RuntimeException {

	public RequestException(String message) {
		super("Bad request exception. " + message);
	}
}
