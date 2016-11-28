package com.flightbuddy.google.response.tripoption.slice;

public class Segment {
	
	private String kind;
    private int duration;
    private ResponseFlight flight;
    private String id;
    private String cabin;
    private String bookingCode;
    private int bookingCodeCount;
    private String marriedSegmentGroup;
    private boolean subjectToGovernmentApproval;
    private Leg[] leg;
    private int connectionDuration;
    
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public ResponseFlight getFlight() {
		return flight;
	}
	public void setFlight(ResponseFlight flight) {
		this.flight = flight;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCabin() {
		return cabin;
	}
	public void setCabin(String cabin) {
		this.cabin = cabin;
	}
	public String getBookingCode() {
		return bookingCode;
	}
	public void setBookingCode(String bookingCode) {
		this.bookingCode = bookingCode;
	}
	public int getBookingCodeCount() {
		return bookingCodeCount;
	}
	public void setBookingCodeCount(int bookingCodeCount) {
		this.bookingCodeCount = bookingCodeCount;
	}
	public String getMarriedSegmentGroup() {
		return marriedSegmentGroup;
	}
	public void setMarriedSegmentGroup(String marriedSegmentGroup) {
		this.marriedSegmentGroup = marriedSegmentGroup;
	}
	public boolean isSubjectToGovernmentApproval() {
		return subjectToGovernmentApproval;
	}
	public void setSubjectToGovernmentApproval(boolean subjectToGovernmentApproval) {
		this.subjectToGovernmentApproval = subjectToGovernmentApproval;
	}
	public Leg[] getLeg() {
		return leg;
	}
	public void setLeg(Leg[] leg) {
		this.leg = leg;
	}
	public int getConnectionDuration() {
		return connectionDuration;
	}
	public void setConnectionDuration(int connectionDuration) {
		this.connectionDuration = connectionDuration;
	}
}
