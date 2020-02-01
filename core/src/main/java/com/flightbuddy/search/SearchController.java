package com.flightbuddy.search;

import com.flightbuddy.resources.Messages;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchService;
import com.flightbuddy.user.User;
import com.flightbuddy.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
class SearchController {

	@Autowired
	private UserService userService;
	@Autowired
	private ScheduledSearchService scheduledSearchService;
	@Autowired
	private SearchService searchService;
	
	@RequestMapping("/search/schedule/save")
	public Map<String, String> saveScheduledSearch(@RequestBody ScheduledSearch scheduledSearch, @AuthenticationPrincipal Principal principal) {
		User currentUser = userService.findByUsername(principal.getName());
		try {
			scheduledSearchService.save(scheduledSearch, currentUser);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Collections.singletonMap("error", e.getMessage());
		}
		return Collections.singletonMap("message", Messages.get("search.scheduled"));

	}
	
	@RequestMapping("/search/perform")
	public Map<String, Object> performSearch(@RequestBody SearchInputData searchData) {
		try {
			return doPerformSearch(searchData);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Collections.singletonMap("error", e.getMessage());
		}
	}

	private Map<String, Object> doPerformSearch(SearchInputData searchData) {
		Map<String, Object> results = new HashMap<>();
		SearchResultsWrapper searchResultsWrapper = searchService.performSearch(searchData);
		String errorMessage = searchResultsWrapper.getErrorMessage();
		if (StringUtils.isNotEmpty(errorMessage)) {
			results.put("message", errorMessage);
		} else {
			results.put("searchResults", searchResultsWrapper.getSearchResults());
			results.put("message", Messages.get("search.successful"));
		}
		return results;
	}
}
