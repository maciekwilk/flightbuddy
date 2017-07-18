package com.flightbuddy.search;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.flightbuddy.Application;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchService;
import com.flightbuddy.user.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class SearchControllerTest {

	@Autowired MockMvc mvc;
    
    @MockBean UserService userService;
    @MockBean ScheduledSearchService scheduledSearchService;
    @MockBean SearchService searchService;
    
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
    	String requestBody = convertToJson(search);
		mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
		.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(authorities = {"ROLE_ADMIN"})
	public void saveScheduledSearchAsAdmin() throws Exception {
		ScheduledSearch search = new ScheduledSearch();
    	String requestBody = convertToJson(search);
		mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
		.andExpect(status().isOk());
	}
	
	@Test
	@WithAnonymousUser
	public void saveScheduledSearchAsAnonymous() throws Exception {
		ScheduledSearch search = new ScheduledSearch();
    	String requestBody = convertToJson(search);
		mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	@WithMockUser(authorities = {"ROLE_USER"})
	public void saveScheduledSearchWithSchedule() throws Exception {
		ScheduledSearch scheduledSearch = createScheduledSearch("from", "to", "10.00", true, Collections.singletonList(LocalDate.of(2017, 8, 21)));
		String requestBody = convertToJson(scheduledSearch);
		mvc.perform(post("/search/schedule/save").contentType(MediaType.APPLICATION_JSON).content(requestBody).with(csrf()))
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
		SearchInputData searchData = createSearchInputData("from", "to", "10.00", false, new LocalDate[] {LocalDate.of(2017, 8, 21)});
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
		SearchInputData searchData = createSearchInputData("from", "to", "10.00", false, new LocalDate[] {LocalDate.of(2017, 8, 21)});
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
		SearchResult searchResult = new SearchResult("9.00", Collections.emptyList(), Collections.emptyList(), trips, stops);
		return searchResult;
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
	
	private SearchInputData createSearchInputData(String from, String to, String price, boolean withReturn, LocalDate[] dates) {
		SearchInputData searchInputData = new SearchInputData();
		searchInputData.setFrom(from);
		searchInputData.setTo(to);
		searchInputData.setPrice(price);
		searchInputData.setWithReturn(withReturn);
		searchInputData.setDates(dates);
		return searchInputData;
	}

	private String convertToJson(Object object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		String requestBody = objectMapper.writeValueAsString(object);
		return requestBody;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getResult(MvcResult mvcResult) throws IOException, JsonParseException, JsonMappingException {
		ObjectMapper objectMapper = new ObjectMapper();
		MockHttpServletResponse response = mvcResult.getResponse();
		byte[] contents = response.getContentAsByteArray();
		return objectMapper.readValue(contents, Map.class);
	}
}
