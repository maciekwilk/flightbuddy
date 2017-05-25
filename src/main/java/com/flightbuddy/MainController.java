package com.flightbuddy;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
	public Map<String, String> user(Principal user) {
		if (user == null)
			return Collections.emptyMap();
		return Collections.singletonMap("username", user.getName());
	}
	
	@RequestMapping("/user/register")
	public Map<String, String> register(@RequestBody RegistrationFormData formData) {
		try {
			userService.createUser(formData.getUsername(), formData.getPassword());
		} catch (Exception e) {
			return Collections.singletonMap("message", e.getMessage());
		}
		return Collections.singletonMap("message", Messages.get("user.registered"));
	}
}