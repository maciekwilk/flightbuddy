package com.flightbuddy.user.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.user.User;
import com.flightbuddy.user.UserDao;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
    private UserDao userDao;
    
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (SystemAuthenticationToken.SYSTEM_USERNAME.equals(username)) {
        	return new SystemAuthenticationToken();
        }
		User user = userDao.findByUsername(username);
		if (user == null) {
        	throw new UsernameNotFoundException("user account is not in database");
        }
        return new UserAuthenticationDetails(user);
	}
}
