package com.flightbuddy.results;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.flightbuddy.db.ImmutableEntity;

@Entity
@Table(name="foundtrip")
public class FoundTrip extends ImmutableEntity {

	@Id
	private String id;

	private BigDecimal price;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "foundTrip")
	private List<Flight> flights;
	
	public FoundTrip() {
		String uuid = UUID.randomUUID().toString();
		id = uuid.replaceAll("-", "");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal getPrice() {
		return price;
	}
	
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public List<Flight> getFlights() {
		return flights;
	}
	
	public void setFlights(List<Flight> flights) {
		this.flights = flights;
	}
}
