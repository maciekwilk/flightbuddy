package com.flightbuddy.user.authentication;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.flightbuddy.user.User;
import com.flightbuddy.user.UserRole;

public class UserAuthenticationDetails implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	private Set<UserRole> roles;
	private String username;
	private String password;
	private boolean enabled;
	
	public UserAuthenticationDetails(){
		
	}
	
	public UserAuthenticationDetails(User user) {
		this.id = user.getId();
		this.roles = user.getRoles();
		this.username = user.getUsername();
		this.enabled = user.isEnabled();
		this.password = user.getPassword();
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
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

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setStatus(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for(UserRole role : roles) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.name());
            authorities.add(grantedAuthority);
        }
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}