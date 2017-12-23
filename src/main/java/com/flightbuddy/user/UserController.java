package com.flightbuddy.user;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.flightbuddy.resources.Messages;
import com.flightbuddy.user.authentication.TokenDTO;
import com.flightbuddy.user.authentication.UserDTO;

@RestController
public class UserController {

	Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired UserService userService;
	
	@RequestMapping(value = "/user/authenticate", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserDTO request) {
        Map<String, Object> tokenMap = userService.authenticate(request.getUsername(), request.getPassword());
		if (tokenMap.containsKey("user")) {
            return new ResponseEntity<Map<String, Object>>(tokenMap, HttpStatus.OK);
		} 
		return new ResponseEntity<Map<String, Object>>(tokenMap, HttpStatus.UNAUTHORIZED);
	}
	
	@RequestMapping(value = "/user/authenticate/token", method = RequestMethod.POST)
	public Map<String, Object> getUserForToken(@RequestBody TokenDTO token) {
        UserDTO user = userService.getUser(token);
		if (user.getUsername() == null) {
            return Collections.emptyMap();
		} 
		return Collections.singletonMap("user", user);
	}
	
	@RequestMapping(value = "/user/register", method = RequestMethod.POST)
	public Map<String, String> register(@RequestBody RegistrationFormData formData) {
		try {
			userService.createUser(formData.getUsername(), formData.getPassword());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Collections.singletonMap("error", e.getMessage());
		}
		return Collections.singletonMap("message", Messages.get("user.registered"));
	}
}