package com.flightbuddy.exceptions;

@SuppressWarnings("serial")
public class NoFlightsFoundException extends RuntimeException {

	public NoFlightsFoundException(String message) {
		super(message);
	}
}
