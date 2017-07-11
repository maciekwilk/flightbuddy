package com.flightbuddy.user;

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
	
	public void login(UserDetails user, String password){
		Authentication request = new UsernamePasswordAuthenticationToken(user.getUsername(), password, user.getAuthorities());
		Authentication result = authenticationManager.authenticate(request);
		SecurityContextHolder.getContext().setAuthentication(result);
	}
	
	public void loginAsSystem(){
		UserAuthenticationDetails system = new SystemAuthenticationToken();
		login(system, SystemAuthenticationToken.SYSTEM_PASSWORD);
	}
	
	public void logoutUsers() {
		SecurityContextHolder.clearContext();
	}
}
