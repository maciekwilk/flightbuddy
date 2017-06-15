package com.flightbuddy.user;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	@Autowired UserDao userDao;
	
	@Transactional
	public User createUser(String username, String password) {
		if (findByUsername(username) != null) {
			throw new RuntimeException("User with username " + username + " already exists");
		}
		String encodedPassword = new ShaPasswordEncoder().encodePassword(password, null);
		User user = new User();
		user.setUsername(username);
		user.setPassword(encodedPassword);
		user.setRoles(Collections.singleton(UserRole.ROLE_USER));
		user.setEnabled(true);
		userDao.persist(user);
		return user;
	}
	
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}
}
