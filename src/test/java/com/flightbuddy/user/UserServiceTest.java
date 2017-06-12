package com.flightbuddy.user;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.flightbuddy.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class UserServiceTest {

	@Autowired UserService userService;

    @MockBean UserDao userDao;
    
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
     
    @Test(expected = RuntimeException.class)
	public void createUserWhenUserExists() {
    	given(userDao.findByUsername(eq(USERNAME))).willReturn(new User());
		userService.createUser(USERNAME, PASSWORD);
		verify(userDao, times(1)).persist(any(User.class));
	}
    
	@Test
	public void createUser() {
		User createdUser = userService.createUser(USERNAME, PASSWORD);
		verify(userDao, times(1)).persist(any(User.class));
		String encodedPassword = new ShaPasswordEncoder().encodePassword(PASSWORD, null);
		assertEquals(createdUser.getPassword(), encodedPassword);
		assertEquals(createdUser.isEnabled(), true);
		assertEquals(createdUser.getRoles(), Collections.singleton(UserRole.ROLE_USER));
	}
}
