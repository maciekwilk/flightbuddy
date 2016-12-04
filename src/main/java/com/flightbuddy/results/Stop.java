package com.flightbuddy.results;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Stop {

	@Id
	@GeneratedValue(generator="system-uuid")
	private String id;

	private String code;

	@JsonIgnore
	@ManyToOne
	private Flight flight;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}
}
