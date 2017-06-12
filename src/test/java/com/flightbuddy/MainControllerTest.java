package com.flightbuddy;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightbuddy.resources.Messages;
import com.flightbuddy.user.RegistrationFormData;
import com.flightbuddy.user.User;
import com.flightbuddy.user.UserDao;
import com.flightbuddy.user.UserRole;
import com.flightbuddy.user.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class MainControllerTest {

    @Autowired MockMvc mvc;
    
    @MockBean UserService userService;
    @MockBean UserDao userDao;
	
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    
    @Test
    public void authenticateWithoutAuthHeader() throws Exception {
    	given(userDao.findByUsername(eq(USERNAME))).willReturn(createUser());
        HttpHeaders headers = new HttpHeaders();
    	mvc.perform(post("/user/authenticate").headers(headers).with(csrf()))
    	.andExpect(status().isOk())
    	.andExpect(content().string("{}"));
    }
    
    @Test
    public void authenticateWithoutUser() throws Exception {
    	given(userDao.findByUsername(eq(USERNAME))).willReturn(null);
        HttpHeaders headers = new HttpHeaders();
        byte[] authorizationString = (USERNAME + ":" + PASSWORD).getBytes();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encodeBase64String(authorizationString));
    	mvc.perform(post("/user/authenticate").headers(headers).with(csrf()))
    	.andExpect(status().isUnauthorized());
    }
    
    @Test
    public void authenticate() throws Exception {
    	given(userDao.findByUsername(eq(USERNAME))).willReturn(createUser());
        HttpHeaders headers = new HttpHeaders();
        byte[] authorizationString = (USERNAME + ":" + PASSWORD).getBytes();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encodeBase64String(authorizationString));
    	ObjectMapper objectMapper = new ObjectMapper();
    	Map<String, String> returnMessage = Collections.singletonMap("username", USERNAME);
		String expectedResponse = objectMapper.writeValueAsString(returnMessage);
    	mvc.perform(post("/user/authenticate").headers(headers).with(csrf()))
    	.andExpect(status().isOk())
    	.andExpect(content().string(expectedResponse));
    }
    
	@Test
	@WithMockUser(authorities = {"ROLE_ADMIN"})
    public void registerUserAsAdmin() throws Exception {
    	RegistrationFormData formData = createRegistrationFormData();
    	ObjectMapper objectMapper = new ObjectMapper();
    	String requestBody = objectMapper.writeValueAsString(formData);
    	Map<String, String> returnMessage = Collections.singletonMap("message", Messages.get("user.registered"));
		String expectedResponse = objectMapper.writeValueAsString(returnMessage);
    	mvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
    	.andExpect(status().isOk())
    	.andExpect(content().string(expectedResponse));
		verify(userService, times(1)).createUser(eq(USERNAME), any());
    }
	
	@Test
	@WithMockUser(authorities = {"ROLE_USER"})
    public void registerUserAsUser() throws Exception {
    	RegistrationFormData formData = createRegistrationFormData();
    	ObjectMapper objectMapper = new ObjectMapper();
    	String requestBody = objectMapper.writeValueAsString(formData);
    	mvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
    	.andExpect(status().isForbidden());
		verify(userService, times(0)).createUser(eq(USERNAME), any());
    }
	
	@Test
    public void registerUserNoUser() throws Exception {
    	RegistrationFormData formData = createRegistrationFormData();
    	ObjectMapper objectMapper = new ObjectMapper();
    	String requestBody = objectMapper.writeValueAsString(formData);
    	mvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
    	.andExpect(status().isUnauthorized());
		verify(userService, times(0)).createUser(eq(USERNAME), any());
    }

	private RegistrationFormData createRegistrationFormData() {
		RegistrationFormData formData = new RegistrationFormData();
    	formData.setUsername(USERNAME);
    	formData.setPassword(PASSWORD);
		return formData;
	}
	
	private User createUser() {
		String encodedPassword = new ShaPasswordEncoder().encodePassword(PASSWORD, null);
		User user = new User();
		user.setUsername(USERNAME);
		user.setPassword(encodedPassword);
		user.setRoles(Collections.singleton(UserRole.ROLE_USER));
		user.setEnabled(true);
		return user;
	}
}