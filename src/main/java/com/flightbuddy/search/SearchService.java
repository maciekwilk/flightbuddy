package com.flightbuddy.search;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightbuddy.google.GoogleService;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.FoundTripService;

@Service
public class SearchService {
	
	@Autowired GoogleService googleService;
	@Autowired FoundTripService foundTripService;

	public List<SearchResult> performSearch(SearchInputData searchData) {
		if (searchData == null) {
			return Collections.emptyList();
		}
		ImmutableSearchInputData searchInputData = SearchDataConverter.convertToImmutable(searchData);
		List<FoundTrip> foundTrips = googleService.getTrips(searchInputData);
		if (foundTrips.isEmpty()) {
			return Collections.emptyList();
		}
    	foundTripService.saveFoundTrips(foundTrips);
		return convertToSearchResults(foundTrips);
	}

	private List<SearchResult> convertToSearchResults(List<FoundTrip> foundTrips) {
		return foundTrips.stream()
				.map(foundTrip -> SearchDataConverter.convertToSearchResult(foundTrip))
				.collect(Collectors.toList());
	}

}
