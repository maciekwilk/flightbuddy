package com.flightbuddy;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
	
	@RequestMapping("/user")
	public Map<String, String> user(Principal user) {
		return Collections.singletonMap("username",user.getName());
	}
}