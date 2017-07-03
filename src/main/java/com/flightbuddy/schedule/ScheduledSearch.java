package com.flightbuddy.schedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.flightbuddy.db.MutableEntity;
import com.flightbuddy.user.User;

@Entity
public class ScheduledSearch extends MutableEntity {
	
	@Id
	private String id;

	@NotNull
	@Column(name="`from`")
	private String from;

	@NotNull
	@Column(name="`to`")
	private String to;

	@NotNull
	private BigDecimal price;

	@ElementCollection
	@JsonSerialize(contentUsing = LocalDateSerializer.class)
	@JsonDeserialize(contentUsing = LocalDateDeserializer.class)
	@CollectionTable(name = "scheduledsearch_dates", joinColumns = @JoinColumn(name = "id"))
	private List<LocalDate> dates;

	private boolean withReturn;
	
	@JoinColumn(name = "user", nullable = false)
	@OneToOne(cascade = CascadeType.ALL)
	private User user;

    @OneToOne(mappedBy = "scheduledSearch")
    private ScheduledSearchTask scheduledSearchTask;

	public ScheduledSearch() {
		String uuid = UUID.randomUUID().toString();
		id = uuid.replaceAll("-", "");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public List<LocalDate> getDates() {
		return dates;
	}

	public void setDates(List<LocalDate> dates) {
		this.dates = dates;
	}

	public boolean isWithReturn() {
		return withReturn;
	}

	public void setWithReturn(boolean withReturn) {
		this.withReturn = withReturn;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ScheduledSearchTask getScheduledSearchTask() {
		return scheduledSearchTask;
	}

	public void setScheduledSearchTask(ScheduledSearchTask scheduledSearchTask) {
		this.scheduledSearchTask = scheduledSearchTask;
	}
}
