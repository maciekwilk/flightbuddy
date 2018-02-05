package com.flightbuddy.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.flightbuddy.Application;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchService;
import com.flightbuddy.user.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class SearchControllerTest {

    @Value("${jwt.signingkey}")
    private String SIGNING_KEY;

	@Autowired
	private MockMvc mvc;

    @MockBean
	private ScheduledSearchService scheduledSearchService;
    @MockBean
	private SearchService searchService;
    
    @Test
	public void saveScheduledSearchWithoutSchedule() throws Exception {
		byte[] requestBody = new byte[]{};
        String token = createToken(UserRole.ROLE_USER);
		mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .header("Authorization", "Bearer " + token).with(csrf()))
		.andExpect(status().isBadRequest());
	}

    @Test
    public void saveScheduledSearchWithoutToken() throws Exception {
        ScheduledSearch search = new ScheduledSearch();
        String requestBody = convertToJson(search);
        mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void saveScheduledSearchAsAnonymous() throws Exception {
        ScheduledSearch search = new ScheduledSearch();
        String requestBody = convertToJson(search);
        String token = createToken(UserRole.ROLE_ANONYMOUS);
        mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .header("Authorization", "Bearer " + token).with(csrf()))
                .andExpect(status().isForbidden());
    }
	
	@Test
	public void saveScheduledSearchAsUser() throws Exception {
		ScheduledSearch search = new ScheduledSearch();
    	String requestBody = convertToJson(search);
        String token = createToken(UserRole.ROLE_USER);
		mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .header("Authorization", "Bearer " + token).with(csrf()))
		.andExpect(status().isOk());
	}

    @Test
	public void saveScheduledSearchAsAdmin() throws Exception {
		ScheduledSearch search = new ScheduledSearch();
    	String requestBody = convertToJson(search);
        String token = createToken(UserRole.ROLE_ADMIN);
		mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .header("Authorization", "Bearer " + token).with(csrf()))
		.andExpect(status().isOk());
	}

    @Test
	public void saveScheduledSearchWithSchedule() throws Exception {
		ScheduledSearch scheduledSearch = createScheduledSearch(Collections.singletonList(LocalDate.of(2017, 8, 21)));
		String requestBody = convertToJson(scheduledSearch);
        String token = createToken(UserRole.ROLE_USER);
		mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody)
                .header("Authorization", "Bearer " + token).with(csrf()))
		.andExpect(status().isOk());
		ArgumentCaptor<ScheduledSearch> argument = ArgumentCaptor.forClass(ScheduledSearch.class);
		verify(scheduledSearchService, times(1)).save(argument.capture(), any());
		assertEquals("from", argument.getValue().getFrom());
	}
	
	@Test
	public void performSearchWithoutSearchData() throws Exception {
		byte[] requestBody = new byte[]{};
		mvc.perform(post("/search/perform").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	public void performSearchWithSearchDataWithoutSearchResults() throws Exception {
		SearchInputData searchData = createSearchInputData(new LocalDate[] {LocalDate.of(2017, 8, 21)});
		String requestBody = convertToJson(searchData);
    	when(searchService.performSearch(any())).thenReturn(Collections.emptyList());
		MvcResult mvcResult = mvc.perform(post("/search/perform").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
		.andExpect(status().isOk()).andReturn();
		Map<String, Object> result = getResult(mvcResult);
		assertEquals(false, result.containsKey("searchResults"));
		assertEquals(true, result.containsKey("message"));
	}
	
	@Test
	public void performSearchWithSearchData() throws Exception {
		SearchInputData searchData = createSearchInputData(new LocalDate[] {LocalDate.of(2017, 8, 21)});
		String requestBody = convertToJson(searchData);
    	SearchResult searchResult = createSearchResult();
    	when(searchService.performSearch(any())).thenReturn(Collections.singletonList(searchResult));
		MvcResult mvcResult = mvc.perform(post("/search/perform").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
		.andExpect(status().isOk()).andReturn();
		Map<String, Object> result = getResult(mvcResult);
		String searchResultBody = convertToJson(result.get("searchResults"));
		String expectedSearchResultBody = convertToJson(searchResult);
		assertEquals("[" + expectedSearchResultBody + "]", searchResultBody);
		assertEquals(true, result.containsKey("message"));
	}

	private SearchResult createSearchResult() {
		List<String> trips = Collections.singletonList("KRK-BSL"); 
		List<Integer> stops = Collections.singletonList(1);
        return new SearchResult(9, Collections.emptyList(), Collections.emptyList(), trips, stops, Collections.emptyList(), Collections.emptyList());
	}

	private ScheduledSearch createScheduledSearch(List<LocalDate> dates) {
		ScheduledSearch scheduledSearch = new ScheduledSearch();
		scheduledSearch.setFrom("from");
		scheduledSearch.setTo("to");
		scheduledSearch.setMinPrice(0);
		scheduledSearch.setMaxPrice(10);
		scheduledSearch.setWithReturn(true);
		scheduledSearch.setDates(dates);
		return scheduledSearch;
	}
	
	private SearchInputData createSearchInputData(LocalDate[] dates) {
		SearchInputData searchInputData = new SearchInputData();
		searchInputData.setFrom("from");
		searchInputData.setTo("to");
		searchInputData.setMinPrice(0);
		searchInputData.setMaxPrice(10);
		searchInputData.setWithReturn(false);
		searchInputData.setDates(dates);
		return searchInputData;
	}

	private String convertToJson(Object object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper.writeValueAsString(object);
	}

    private String createToken(UserRole role) {
        Set userRoles = Collections.singleton(role);
        return Jwts.builder()
                .setSubject("username")
                .claim("roles", userRoles)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, SIGNING_KEY).compact();
    }

	@SuppressWarnings("unchecked")
	private Map<String, Object> getResult(MvcResult mvcResult) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		MockHttpServletResponse response = mvcResult.getResponse();
		byte[] contents = response.getContentAsByteArray();
		return objectMapper.readValue(contents, Map.class);
	}
}
