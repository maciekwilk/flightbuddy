package com.flightbuddy;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightbuddy.resources.Messages;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchService;
import com.flightbuddy.user.RegistrationFormData;
import com.flightbuddy.user.User;
import com.flightbuddy.user.UserService;

@RestController
public class MainController {

	Logger log = LoggerFactory.getLogger(MainController.class);
	
	@Autowired UserService userService;
	@Autowired ScheduledSearchService scheduledSearchService;
	
	@RequestMapping("/user/authenticate")
	public Principal user(@AuthenticationPrincipal Principal user) {
		return user;
	}
	
	@RequestMapping("/user/register")
	public Map<String, String> register(@RequestBody RegistrationFormData formData) {
		try {
			userService.createUser(formData.getUsername(), formData.getPassword());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Collections.singletonMap("error", e.getMessage());
		}
		return Collections.singletonMap("message", Messages.get("user.registered"));
	}
	
	@RequestMapping("/search/schedule/save")
	public Map<String, String> saveScheduledSearch(@RequestBody ScheduledSearch scheduledSearch, @AuthenticationPrincipal Principal principal) {
		User currentUser = userService.findByUsername(principal.getName());
		try {
			scheduledSearchService.save(scheduledSearch, currentUser);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Collections.singletonMap("error", e.getMessage());
		}
		return Collections.singletonMap("message", Messages.get("search.scheduled"));

	}
}