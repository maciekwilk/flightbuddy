package com.flightbuddy;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightbuddy.resources.Messages;
import com.flightbuddy.user.RegistrationFormData;
import com.flightbuddy.user.UserService;

@RestController
public class MainController {
	
	@Autowired UserService userService;
	
	@RequestMapping("/user/authenticate")
	public Principal user(@AuthenticationPrincipal Principal user) {
		return user;
	}
	
	@RequestMapping("/user/register")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Map<String, String> register(@RequestBody RegistrationFormData formData) {
		try {
			userService.createUser(formData.getUsername(), formData.getPassword());
		} catch (Exception e) {
			return Collections.singletonMap("error", e.getMessage());
		}
		return Collections.singletonMap("message", Messages.get("user.registered"));
	}
}