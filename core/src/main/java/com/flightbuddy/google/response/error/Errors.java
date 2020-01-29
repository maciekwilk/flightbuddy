package com.flightbuddy.google.response.error;

public class Errors {
	
	private String domain;
    private String reason;
    private String message;
    private String extendedHelp;
    
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getExtendedHelp() {
		return extendedHelp;
	}
	public void setExtendedHelp(String extendedHelp) {
		this.extendedHelp = extendedHelp;
	}
}
