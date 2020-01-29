package com.flightbuddy.user.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	private void login(UserDetails user){
		Authentication request = new UsernamePasswordAuthenticationToken(user.getUsername(), SystemAuthenticationToken.SYSTEM_PASSWORD, user.getAuthorities());
		Authentication result = authenticationManager.authenticate(request);
		SecurityContextHolder.getContext().setAuthentication(result);
	}
	
	public void loginAsSystem(){
		UserAuthenticationDetails system = new SystemAuthenticationToken();
		login(system);
	}
	
	public void logoutUsers() {
		SecurityContextHolder.clearContext();
	}
}
