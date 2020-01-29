package com.flightbuddy.db;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;
	
@MappedSuperclass
public abstract class ImmutableEntity {

	@NotNull
	@Column(updatable = false)
	private LocalDateTime created;
	
	@PrePersist
	private void onPersist() {
		final LocalDateTime now = LocalDateTime.now();
		if (created == null) { // Tests could have modified it
			created = now;
		}
	}
	
	/** 
	 * This should only be used inside Tests
	 */
	@Deprecated
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getCreated() {
		return created;
	}
}