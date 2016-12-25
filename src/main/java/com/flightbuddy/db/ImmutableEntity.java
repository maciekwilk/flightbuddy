package com.flightbuddy.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
	
@MappedSuperclass
public abstract class ImmutableEntity {

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date created;
	
	@PrePersist
	private void onPersist() {
		final Date now = new Date();
		if (created == null) { // Tests could have modified it
			created = now;
		}
	}
	
	/** 
	 * This should only be used inside Tests
	 */
	@Deprecated
	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}
}