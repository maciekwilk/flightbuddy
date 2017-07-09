package com.flightbuddy.user;

import java.util.Set;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.flightbuddy.db.MutableEntity;
import com.flightbuddy.schedule.search.ScheduledSearch;

@Entity
public class User extends MutableEntity {

    @Id
    private String id;
    
    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    private String password;

    private boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="role", joinColumns=@JoinColumn(name="id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;
    
    @OneToOne(mappedBy = "user")
    private ScheduledSearch scheduledSearch;

	public User() {
		String uuid = UUID.randomUUID().toString();
		id = uuid.replaceAll("-", "");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<UserRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<UserRole> roles) {
		this.roles = roles;
	}

	public ScheduledSearch getScheduledSearch() {
		return scheduledSearch;
	}

	public void setScheduledSearch(ScheduledSearch scheduledSearch) {
		this.scheduledSearch = scheduledSearch;
	}
}