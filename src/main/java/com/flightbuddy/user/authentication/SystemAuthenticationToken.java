package com.flightbuddy.user.authentication;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.flightbuddy.user.UserRole;

public class SystemAuthenticationToken extends UserAuthenticationDetails {

	private static final ShaPasswordEncoder ENCODER = new ShaPasswordEncoder();
	public static final String SYSTEM_USERNAME = "j2Qiqmk$DdS#pHSd_x4rA8Y";
	public static final String SYSTEM_PASSWORD = "GZw@1s21j*6PCY9YblASL(f";
	private static final List<GrantedAuthority> SYSTEM_AUTHORITIES;
	private static final long serialVersionUID = 4663607279383413936L;	
	
	static{
		SYSTEM_AUTHORITIES = Arrays.asList( (GrantedAuthority)new SimpleGrantedAuthority(UserRole.ROLE_SYSTEM.name()));
	}
	
	@Override
	public List<GrantedAuthority> getAuthorities() {
		return SYSTEM_AUTHORITIES;
	}

	@Override
	public String getPassword() {
		return ENCODER.encodePassword(SYSTEM_PASSWORD, null);
	}

	@Override
	public String getUsername() {
		return SYSTEM_USERNAME;
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
		return true;
	}	
}
