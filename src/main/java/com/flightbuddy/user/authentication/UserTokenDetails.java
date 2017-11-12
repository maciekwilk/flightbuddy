package com.flightbuddy.user.authentication;

import java.util.Set;

import com.flightbuddy.user.User;
import com.flightbuddy.user.UserRole;

public class UserTokenDetails {
	
	private Set<UserRole> roles;
	private String username;
	private String password;
	
	public UserTokenDetails(){
	}
	
	public UserTokenDetails(User user) {
		this.roles = user.getRoles();
		this.username = user.getUsername();
	}

	public Set<UserRole> getRoles() {
		return roles;
	}
	
	public void setRoles(Set<UserRole> roles) {
		this.roles = roles;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}