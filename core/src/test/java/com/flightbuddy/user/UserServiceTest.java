package com.flightbuddy.user;

import com.flightbuddy.Application;
import com.flightbuddy.user.authentication.TokenTO;
import com.flightbuddy.user.authentication.UserTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static com.flightbuddy.user.UserRole.ROLE_ADMIN;
import static com.flightbuddy.user.UserRole.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class UserServiceTest {

    @Value("${jwt.signingkey}")
    private String SIGNING_KEY;

	@Autowired
    private UserService userService;

    @MockBean
    private UserDao userDao;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

	@Test
	@WithMockUser(authorities = {"ROLE_ADMIN"})
	public void createUserAsAdmin() {
		User createdUser = userService.createUser(USERNAME, PASSWORD);
		verify(userDao, times(1)).persist(any(User.class));
		assertTrue(isPasswordValid(createdUser.getPassword()));
        assertTrue(createdUser.isEnabled());
		assertEquals(createdUser.getRoles(), Collections.singleton(ROLE_USER));
	}

	@WithMockUser(authorities = {"ROLE_ADMIN"})
	public void createUserAsAdminWhenUserExists() {
		given(userDao.findByUsername(eq(USERNAME))).willReturn(new User());
		assertThrows(RuntimeException.class, () -> userService.createUser(USERNAME, PASSWORD));
	}

	@WithMockUser(authorities = {"ROLE_USER"})
	public void createUserAsUser() {
        assertThrows(AccessDeniedException.class, () -> userService.createUser(USERNAME, PASSWORD));
	}

    public void createUserAsAnonymous() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> userService.createUser(USERNAME, PASSWORD));
    }

    @Test
    public void authenticateWhenNoUser() {
        given(userDao.findByUsername(eq(USERNAME))).willReturn(null);
        Map<String, Object> returnedMap = userService.authenticate(USERNAME, PASSWORD);
        assertFalse(returnedMap.containsKey("user"));
        assertNull(returnedMap.get("token"));
    }

    @Test
    public void authenticateWithWrongPassword() {
        given(userDao.findByUsername(eq(USERNAME))).willReturn(null);
        Map<String, Object> returnedMap = userService.authenticate(USERNAME, "wrongpassword");
        assertFalse(returnedMap.containsKey("user"));
        assertNull(returnedMap.get("token"));
    }

    @Test
    public void authenticateUser() {
        UserRole role = ROLE_USER;
        User user = createUser(role);
        given(userDao.findByUsername(eq(USERNAME))).willReturn(user);
        Map<String, Object> returnedMap = userService.authenticate(USERNAME, PASSWORD);
        assertAuthenticationWasSuccessful(role, returnedMap);
    }

    @Test
    public void authenticateAdmin() {
        UserRole role = ROLE_ADMIN;
        User user = createUser(role);
        given(userDao.findByUsername(eq(USERNAME))).willReturn(user);
        Map<String, Object> returnedMap = userService.authenticate(USERNAME, PASSWORD);
        assertAuthenticationWasSuccessful(role, returnedMap);
    }

    public void getUserAsAnonymous() {
	    TokenTO token = createTokenTO(false);
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> userService.getUser(token));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    public void getUserAsAdminWithNullToken() {
        TokenTO token = createTokenTO(true);
        UserTO returnedUser = userService.getUser(token);
        assertNull(returnedUser.username);
        assertNull(returnedUser.roles);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    public void getUserAsAdminWithoutUser() {
        TokenTO token = createTokenTO(true);
        given(userDao.findByUsername(eq(USERNAME))).willReturn(null);
        UserTO returnedUser = userService.getUser(token);
        assertNull(returnedUser.username);
        assertNull(returnedUser.roles);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    public void getUserAsAdminWithUser() {
        TokenTO token = createTokenTO(true);
        User user = createUser(ROLE_USER);
        given(userDao.findByUsername(eq(USERNAME))).willReturn(user);
        UserTO returnedUser = userService.getUser(token);
        assertEquals(returnedUser.username, USERNAME);
        assertTrue(returnedUser.roles.contains(ROLE_USER));
        assertFalse(returnedUser.roles.contains(ROLE_ADMIN));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_USER"})
    public void getUserAsUserWithUser() {
        TokenTO token = createTokenTO(true);
        User user = createUser(ROLE_USER);
        given(userDao.findByUsername(eq(USERNAME))).willReturn(user);
        UserTO returnedUser = userService.getUser(token);
        assertEquals(returnedUser.username, USERNAME);
        assertTrue(returnedUser.roles.contains(ROLE_USER));
        assertFalse(returnedUser.roles.contains(ROLE_ADMIN));
    }


	private boolean isPasswordValid(String validPassword) {
		return new BCryptPasswordEncoder().matches(UserServiceTest.PASSWORD, validPassword);
	}

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

	private User createUser(UserRole role) {
		String encodedPassword = new BCryptPasswordEncoder().encode(PASSWORD);
		User user = new User();
		user.setUsername(USERNAME);
		user.setPassword(encodedPassword);
		user.setRoles(Collections.singleton(role));
		user.setEnabled(true);
		return user;
	}

    private TokenTO createTokenTO(boolean withToken) {
        TokenTO tokenTO = new TokenTO();
        if (withToken) {
            tokenTO.token = Jwts.builder()
                    .setSubject(USERNAME)
                    .claim("roles", "")
                    .setIssuedAt(new Date())
                    .signWith(SignatureAlgorithm.HS512, SIGNING_KEY).compact();
        }
        return tokenTO;
    }

    @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
    private void assertAuthenticationWasSuccessful(UserRole role, Map<String, Object> returnedMap) {
        String token = (String) returnedMap.get("token");
        Claims claims = getClaims(token);
        UserTO userTO = (UserTO) returnedMap.get("user");
        ArrayList<UserRole> claimsRoles = (ArrayList<UserRole>) claims.get("roles");
        assertEquals(userTO.username, USERNAME);
        assertTrue(userTO.roles.contains(role));
        assertNull(userTO.password);
        assertEquals(claims.getSubject(), USERNAME);
        assertTrue(claimsRoles.contains(role.name()));
    }
}
