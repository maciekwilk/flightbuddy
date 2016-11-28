package com.flightbuddy.results;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Flight {

	@Id
	@GeneratedValue(generator="system-uuid")
	private String id;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "flight")
	private List<Stop> stops;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "flight")
	private List<Airline> airlines;

	@Temporal(value = TemporalType.TIMESTAMP)
	private LocalDate date;
	
	@ManyToOne
	private FoundTrip foundTrip;

	public List<Stop> getStops() {
		return stops;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setStops(List<Stop> stops) {
		this.stops = stops;
	}

	public List<Airline> getAirlines() {
		return airlines;
	}

	public void setAirlines(List<Airline> airlines) {
		this.airlines = airlines;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public FoundTrip getFoundTrip() {
		return foundTrip;
	}

	public void setFoundTrip(FoundTrip foundTrip) {
		this.foundTrip = foundTrip;
	}
}
