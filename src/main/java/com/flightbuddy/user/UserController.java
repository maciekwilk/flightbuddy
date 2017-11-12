package com.flightbuddy.user;

import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.flightbuddy.resources.Messages;
import com.flightbuddy.user.authentication.UserTokenDetails;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
public class UserController {

	Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired UserService userService;
	
	@RequestMapping("/user")
    public User user(Principal principal) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String loggedUsername = auth.getName();
        return userService.findByUsername(loggedUsername);
	}
	
	@RequestMapping(value = "/user/authenticate", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserTokenDetails request) {
        String token = null;
        User user = userService.findByUsername(request.getUsername());
		String encodedPassword = new ShaPasswordEncoder().encodePassword(request.getPassword(), null);
        Map<String, Object> tokenMap = new HashMap<String, Object>();
        if (user != null && user.getPassword().equals(encodedPassword)) {
            token = Jwts.builder().setSubject(user.getUsername()).claim("roles", user.getRoles()).setIssuedAt(new Date())
                    .signWith(SignatureAlgorithm.HS256, "secretkey").compact();
            tokenMap.put("token", token);
            tokenMap.put("user", new UserTokenDetails(user));
            return new ResponseEntity<Map<String, Object>>(tokenMap, HttpStatus.OK);
        } else {
            tokenMap.put("token", null);
            return new ResponseEntity<Map<String, Object>>(tokenMap, HttpStatus.UNAUTHORIZED);
        }

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
}