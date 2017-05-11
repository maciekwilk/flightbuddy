package com.flightbuddy.user;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	@Autowired UserDao userDao;
	
	@Transactional
	@PreAuthorize("hasAny('ADMIN')")
	public void createUser(String username, String password) {
		if (findByUsername(username) != null) {
			throw new RuntimeException("User with username " + username + "already exists");
		}
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		addUserRole(user);
		user.setEnabled(true);
		userDao.persist(user);
	}
	
	@PreAuthorize("hasAny('ADMIN')")
	public User findByUsername(String username) {
		return userDao.findByUsername(username);
	}

	private void addUserRole(User user) {
		Set<UserRole> roles = new HashSet<>(1);
		roles.add(UserRole.USER);
		user.setRoles(roles);
	}
}
