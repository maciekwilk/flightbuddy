package com.flightbuddy.google.response.tripoption.slice;

public class Slice {

	private String kind;
    private int duration;
    private Segment[] segment;
    
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
	public Segment[] getSegment() {
		return segment;
	}
	public void setSegment(Segment[] segment) {
		this.segment = segment;
	}    
}
