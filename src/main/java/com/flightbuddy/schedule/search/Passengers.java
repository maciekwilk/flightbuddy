package com.flightbuddy.schedule.search;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.flightbuddy.db.MutableEntity;

@Entity
@Table(name="passengers")
public class Passengers extends MutableEntity {

	@Id
	private String id;
	
	private int adultCount;
	
    private int childCount;
    
    private int infantInLapCount;
    
    private int infantInSeatCount;
    
    private int seniorCount;
    
    @JoinColumn(name = "scheduledSearch", nullable = false)
	@OneToOne
    private ScheduledSearch scheduledSearch;
    
    public Passengers() {
		String uuid = UUID.randomUUID().toString();
		id = uuid.replaceAll("-", "");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getAdultCount() {
		return adultCount;
	}

	public void setAdultCount(int adultCount) {
		this.adultCount = adultCount;
	}

	public int getChildCount() {
		return childCount;
	}

	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	public int getInfantInLapCount() {
		return infantInLapCount;
	}

	public void setInfantInLapCount(int infantInLapCount) {
		this.infantInLapCount = infantInLapCount;
	}

	public int getInfantInSeatCount() {
		return infantInSeatCount;
	}

	public void setInfantInSeatCount(int infantInSeatCount) {
		this.infantInSeatCount = infantInSeatCount;
	}

	public int getSeniorCount() {
		return seniorCount;
	}

	public void setSeniorCount(int seniorCount) {
		this.seniorCount = seniorCount;
	}

	public ScheduledSearch getScheduledSearch() {
		return scheduledSearch;
	}

	public void setScheduledSearch(ScheduledSearch scheduledSearch) {
		this.scheduledSearch = scheduledSearch;
	}
}
