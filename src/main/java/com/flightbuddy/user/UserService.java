package com.flightbuddy.user;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.user.authentication.JWTFilter;
import com.flightbuddy.user.authentication.UserTokenDetails;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class UserService {

	@Autowired UserDao userDao;
	
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public User createUser(String username, String password) {
		if (findByUsername(username) != null) {
			throw new RuntimeException("User with username " + username + " already exists");
		}
		User user = new User();
		String encodedPassword = new BCryptPasswordEncoder().encode(password);
		user.setUsername(username);
		user.setPassword(encodedPassword);
		user.setRoles(Collections.singleton(UserRole.ROLE_USER));
		user.setEnabled(true);
		userDao.persist(user);
		return user;
	}
	
	public Map<String, Object> authenticate(String username, String password) {
		User user = findByUsername(username);
        Map<String, Object> tokenMap = new HashMap<String, Object>();
        if (user != null && isPasswordValid(password, user.getPassword())) {
        	String token = Jwts.builder().setSubject(user.getUsername()).claim("roles", user.getRoles()).setIssuedAt(new Date())
                    .signWith(SignatureAlgorithm.HS256, JWTFilter.SIGNING_KEY).compact();
            tokenMap.put("token", token);
            tokenMap.put("user", new UserTokenDetails(user));
        } else {
            tokenMap.put("token", null);
        }
		return tokenMap;
	}

	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}
	
	private boolean isPasswordValid(String insertedPassword, String validPassword) {
		return new BCryptPasswordEncoder().matches(insertedPassword, validPassword);
	}
}
