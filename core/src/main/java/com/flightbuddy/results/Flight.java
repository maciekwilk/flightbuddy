package com.flightbuddy.results;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flightbuddy.db.ImmutableEntity;

@Entity
@Table(name="flight")
public class Flight extends ImmutableEntity {

	@Id
	private String id;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "flight")
	private List<Stop> stops;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "flight")
	private List<Airline> airlines;

	private LocalDateTime date;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "foundTrip")
	private FoundTrip foundTrip;
	
	private int duration;

	public Flight() {
		String uuid = UUID.randomUUID().toString();
		id = uuid.replaceAll("-", "");
	}
	
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

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public FoundTrip getFoundTrip() {
		return foundTrip;
	}

	public void setFoundTrip(FoundTrip foundTrip) {
		this.foundTrip = foundTrip;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
}
