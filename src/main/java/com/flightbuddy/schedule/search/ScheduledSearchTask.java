package com.flightbuddy.schedule.search;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.flightbuddy.db.ImmutableEntity;

@Entity
public class ScheduledSearchTask extends ImmutableEntity {
	
	@Id
	private String id;
	
	@JoinColumn(name = "scheduledSearch", nullable = false)
	@OneToOne(cascade = CascadeType.ALL)
	private ScheduledSearch scheduledSearch;
	
	@NotNull
	@Column
	private LocalDateTime executionTime;
	
    @Column
    @Enumerated(EnumType.STRING)
    private ScheduledSearchState state;
    
    @Column
    @Enumerated(EnumType.STRING)
    private RequestService service;
    
    public ScheduledSearchTask() {
		String uuid = UUID.randomUUID().toString();
		id = uuid.replaceAll("-", "");
	}
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ScheduledSearch getScheduledSearch() {
		return scheduledSearch;
	}

	public void setScheduledSearch(ScheduledSearch scheduledSearch) {
		this.scheduledSearch = scheduledSearch;
	}

	public LocalDateTime getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(LocalDateTime executionTime) {
		this.executionTime = executionTime;
	}

	public ScheduledSearchState getState() {
		return state;
	}

	public void setState(ScheduledSearchState state) {
		this.state = state;
	}

	public RequestService getService() {
		return service;
	}

	public void setService(RequestService service) {
		this.service = service;
	}

	public enum ScheduledSearchState {
    	READY, SET, STARTED, FINISHED
    }
    
    public enum RequestService {
    	GOOGLE
    }
}
