package com.flightbuddy.google.response.error;

public class Error {

	private Errors[] errors;
	private String code;
	private String message;
	
	public Errors[] getErrors() {
		return errors;
	}
	public void setErrors(Errors[] errors) {
		this.errors = errors;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
