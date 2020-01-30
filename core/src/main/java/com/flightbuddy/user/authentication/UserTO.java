package com.flightbuddy.user.authentication;

import java.util.Set;

import com.flightbuddy.user.User;
import com.flightbuddy.user.UserRole;

public class UserTO {
	
	public Set<UserRole> roles;
	public String username;
	public String password;
	
	public UserTO(){
	}
	
	public UserTO(User user) {
		this.roles = user.getRoles();
		this.username = user.getUsername();
	}
}