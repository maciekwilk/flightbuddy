package com.flightbuddy.search;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightbuddy.google.GoogleService;
import com.flightbuddy.results.FoundTrip;

@Service
public class SearchService {
	
	@Autowired GoogleService googleService;

	public List<SearchResult> performSearch(SearchInputData searchData) {
		if (searchData == null) {
			return Collections.emptyList();
		}
		ImmutableSearchInputData searchInputData = SearchDataConverter.convertToImmutable(searchData);
		List<FoundTrip> foundTrips = googleService.getTrips(searchInputData);
		if (foundTrips.isEmpty()) {
			return Collections.emptyList();
		}
		List<SearchResult> results = convertToSearchResults(foundTrips);
		return results;
	}

	private List<SearchResult> convertToSearchResults(List<FoundTrip> foundTrips) {
		List<SearchResult> results = foundTrips.stream()
													.map(foundTrip -> SearchDataConverter.convertToSearchResult(foundTrip))
													.collect(Collectors.toList());
//		foundTrips.forEach(foundTrip -> {;
//			SearchResult searchResult = SearchDataConverter.convertToSearchResult(foundTrip);
//			results.add(searchResult);
//		});
		return results;
	}

}
