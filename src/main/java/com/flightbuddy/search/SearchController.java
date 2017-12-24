package com.flightbuddy.search;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightbuddy.resources.Messages;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchService;
import com.flightbuddy.user.User;
import com.flightbuddy.user.UserService;

@RestController
public class SearchController {

	final Logger log = LoggerFactory.getLogger(SearchController.class);

	@Autowired UserService userService;
	@Autowired ScheduledSearchService scheduledSearchService;
	@Autowired SearchService searchService;
	
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
		List<SearchResult> searchResults = searchService.performSearch(searchData);
		if (searchResults.isEmpty()) {
			results.put("message", Messages.get("search.empty"));
		} else {
			results.put("searchResults", searchResults);
			results.put("message", Messages.get("search.successful"));
		}
		return results;
	}
}
