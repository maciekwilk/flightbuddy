package com.flightbuddy;

import static com.flightbuddy.user.UserRole.ROLE_ADMIN;
import static com.flightbuddy.user.UserRole.ROLE_USER;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.flightbuddy.resources.Messages;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchService;
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
    @MockBean ScheduledSearchService scheduledSearchService;
	
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    
    
    @Test
    public void authenticateWithoutAuthHeader() throws Exception {
    	given(userDao.findByUsername(eq(USERNAME))).willReturn(createUser(ROLE_USER));
        HttpHeaders headers = new HttpHeaders();
    	mvc.perform(post("/user/authenticate").headers(headers).with(csrf()))
    	.andExpect(status().isOk())
    	.andExpect(content().string(""));
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
    public void authenticateUser() throws Exception {
		given(userDao.findByUsername(eq(USERNAME))).willReturn(createUser(ROLE_USER));
        HttpHeaders headers = new HttpHeaders();
        byte[] authorizationString = (USERNAME + ":" + PASSWORD).getBytes();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encodeBase64String(authorizationString));
    	mvc.perform(post("/user/authenticate").headers(headers).with(csrf()))
    	.andExpect(status().isOk())
    	.andExpect(content().string(containsUsername(USERNAME)))
    	.andExpect(content().string(containsRole(ROLE_USER)));
    }
    
    @Test
    public void authenticateAdmin() throws Exception {
    	given(userDao.findByUsername(eq(USERNAME))).willReturn(createUser(ROLE_ADMIN));
        HttpHeaders headers = new HttpHeaders();
        byte[] authorizationString = (USERNAME + ":" + PASSWORD).getBytes();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encodeBase64String(authorizationString));
    	mvc.perform(post("/user/authenticate").headers(headers).with(csrf()))
    	.andExpect(status().isOk())
    	.andExpect(content().string(containsUsername(USERNAME)))
    	.andExpect(content().string(containsRole(ROLE_ADMIN)));
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
	
	@Test
	@WithMockUser
	public void saveScheduledSearchWithoutSchedule() throws Exception {
		byte[] requestBody = new byte[]{};
		mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	@WithMockUser(authorities = {"ROLE_USER"})
	public void saveScheduledSearchAsUser() throws Exception {
		ScheduledSearch search = new ScheduledSearch();
		ObjectMapper objectMapper = new ObjectMapper();
    	String requestBody = objectMapper.writeValueAsString(search);
		mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
		.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(authorities = {"ROLE_ADMIN"})
	public void saveScheduledSearchAsAdmin() throws Exception {
		ScheduledSearch search = new ScheduledSearch();
		ObjectMapper objectMapper = new ObjectMapper();
    	String requestBody = objectMapper.writeValueAsString(search);
		mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
		.andExpect(status().isOk());
	}
	
	@Test
	@WithAnonymousUser
	public void saveScheduledSearchAsAnonymous() throws Exception {
		ScheduledSearch search = new ScheduledSearch();
		ObjectMapper objectMapper = new ObjectMapper();
    	String requestBody = objectMapper.writeValueAsString(search);
		mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithMockUser(authorities = {"ROLE_USER"})
	public void saveScheduledSearchWithSchedule() throws Exception {
		ScheduledSearch scheduledSearch = createScheduledSearch("from", "to", "10.00", true, Collections.singletonList(LocalDate.of(2017, 8, 21)));
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    	String requestBody = objectMapper.writeValueAsString(scheduledSearch);
		mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
		.andExpect(status().isOk());
		ArgumentCaptor<ScheduledSearch> argument = ArgumentCaptor.forClass(ScheduledSearch.class);
		verify(scheduledSearchService, times(1)).save(argument.capture(), any());
		assertEquals("from", argument.getValue().getFrom());
	}

	private RegistrationFormData createRegistrationFormData() {
		RegistrationFormData formData = new RegistrationFormData();
    	formData.setUsername(USERNAME);
    	formData.setPassword(PASSWORD);
		return formData;
	}
	
	private User createUser(UserRole role) {
		String encodedPassword = new ShaPasswordEncoder().encodePassword(PASSWORD, null);
		User user = new User();
		user.setUsername(USERNAME);
		user.setPassword(encodedPassword);
		user.setRoles(Collections.singleton(role));
		user.setEnabled(true);
		return user;
	}

	private Matcher<String> containsRole(UserRole role) {
		return containsString("\"authorities\":[{\"authority\":\"" + role + "\"}]");
	}

	private Matcher<String> containsUsername(String username) {
		return containsString("\"username\":\"" + username + "\"");
	}

	private ScheduledSearch createScheduledSearch(String from, String to, String price, boolean withReturn, List<LocalDate> dates) {
		ScheduledSearch scheduledSearch = new ScheduledSearch();
		scheduledSearch.setFrom(from);
		scheduledSearch.setTo(to);
		scheduledSearch.setPrice(new BigDecimal(price));
		scheduledSearch.setWithReturn(withReturn);
		scheduledSearch.setDates(dates);
		return scheduledSearch;
	}
}