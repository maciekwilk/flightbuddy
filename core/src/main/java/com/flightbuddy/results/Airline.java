package com.flightbuddy.results;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flightbuddy.db.ImmutableEntity;

@Entity
@Table(name="airline")
public class Airline extends ImmutableEntity {

	@Id
	private String id;

	private String name;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "flight")
	private Flight flight;

	public Airline() {
		String uuid = UUID.randomUUID().toString();
		id = uuid.replaceAll("-", "");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}
}
