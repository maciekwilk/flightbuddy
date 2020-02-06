package com.flightbuddy.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.flightbuddy.Application;
import com.flightbuddy.resources.Messages;
import com.flightbuddy.user.authentication.TokenTO;
import com.flightbuddy.user.authentication.UserTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class UserControllerTest {

	@Value("${jwt.signingkey}")
	private String SIGNING_KEY;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private static final String USERNAME = "test@example.com";
    private static final String PASSWORD = "password";

    @Test
    public void loginWithoutUser() throws Exception {
    	given(userService.authenticate(eq(USERNAME), eq(PASSWORD))).willReturn(Collections.emptyMap());
        UserTO user = createUserTO();
        String requestBody = convertToJson(user);
        String expectedBody = convertToJson(Collections.emptyMap());
    	mvc.perform(post("/user/authenticate").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
    	.andExpect(status().isUnauthorized())
    	.andExpect(content().string(expectedBody));
    }

    @Test
    public void loginWithUser() throws Exception {
        Map<String, Object> mapWithUser = Collections.singletonMap("user", USERNAME);
        given(userService.authenticate(eq(USERNAME), eq(PASSWORD))).willReturn(mapWithUser);
        UserTO user = createUserTO();
        String requestBody = convertToJson(user);
        String expectedBody = convertToJson(mapWithUser);
        mvc.perform(post("/user/authenticate").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedBody));
    }

    @Test
    public void getUserForTokenWithoutAuthentication() throws Exception {
        given(userService.getUser(any())).willReturn(new UserTO());
        String requestBody = convertToJson(new TokenTO());
        mvc.perform(post("/user/authenticate/token").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""));
    }

    @Test
    public void getUserForTokenWithoutUser() throws Exception {
        given(userService.getUser(any())).willReturn(new UserTO());
        String requestBody = convertToJson(new TokenTO());
        String expectedBody = convertToJson(Collections.emptyMap());
        String token = createToken(UserRole.ROLE_USER);
        mvc.perform(post("/user/authenticate/token").contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .header("Authorization", "Bearer " + token).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedBody));
    }

    @Test
    public void getUserForTokenWithUser() throws Exception {
        UserTO user = createUserTO();
        given(userService.getUser(any())).willReturn(user);
        String requestBody = convertToJson(new TokenTO());
        String expectedBody = convertToJson(Collections.singletonMap("user", user));
        String token = createToken(UserRole.ROLE_USER);
        mvc.perform(post("/user/authenticate/token").contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .header("Authorization", "Bearer " + token).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedBody));
    }

    @Test
    public void getUserForTokenWithUserAsAdmin() throws Exception {
        UserTO user = createUserTO();
        given(userService.getUser(any())).willReturn(user);
        String requestBody = convertToJson(new TokenTO());
        String expectedBody = convertToJson(Collections.singletonMap("user", user));
        String token = createToken(UserRole.ROLE_ADMIN);
        mvc.perform(post("/user/authenticate/token").contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .header("Authorization", "Bearer " + token).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedBody));
    }

    @Test
    public void registerUserAsAdmin() throws Exception {
    	RegistrationRequest formData = createRegistrationFormDataTO();
    	String requestBody = convertToJson(formData);
    	Map<String, String> returnMessage = Collections.singletonMap("message", Messages.get("user.registered"));
		String expectedResponse = convertToJson(returnMessage);
        String token = createToken(UserRole.ROLE_ADMIN);
    	mvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .header("Authorization", "Bearer " + token).with(csrf()))
    	.andExpect(status().isOk())
    	.andExpect(content().string(expectedResponse));
		verify(userService, times(1)).createUser(eq(USERNAME), any());
    }

	@Test
    public void registerUserAsUser() throws Exception {
    	RegistrationRequest formData = createRegistrationFormDataTO();
    	String requestBody = convertToJson(formData);
        String token = createToken(UserRole.ROLE_USER);
    	mvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .header("Authorization", "Bearer " + token).with(csrf()))
    	.andExpect(status().isForbidden());
		verify(userService, times(0)).createUser(eq(USERNAME), any());
    }

	@Test
    public void registerUserNoUser() throws Exception {
    	RegistrationRequest formData = createRegistrationFormDataTO();
    	String requestBody = convertToJson(formData);
    	mvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
    	.andExpect(status().isUnauthorized());
		verify(userService, times(0)).createUser(eq(USERNAME), any());
    }



	private String convertToJson(Object object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper.writeValueAsString(object);
	}

	private RegistrationRequest createRegistrationFormDataTO() {
		return new RegistrationRequest(USERNAME, PASSWORD);
	}

    private UserTO createUserTO() {
        UserTO user = new UserTO();
        user.username = USERNAME;
        user.password = PASSWORD;
        return user;
    }

    private String createToken(UserRole role) {
        Set userRoles = Collections.singleton(role);
        return Jwts.builder()
                .setSubject("username")
                .claim("roles", userRoles)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, SIGNING_KEY).compact();
    }
}