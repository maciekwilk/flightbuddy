package com.flightbuddy.exceptions;

@SuppressWarnings("serial")
public class ConnectionException extends RuntimeException {

	public ConnectionException(String message) {
		super(message);
	}
}
