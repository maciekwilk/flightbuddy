package com.flightbuddy.search;

import com.flightbuddy.google.GoogleService;
import com.flightbuddy.resources.Messages;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.FoundTripService;
import com.flightbuddy.results.FoundTripsWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
class SearchService {
	
	@Autowired
	private GoogleService googleService;
	@Autowired
	private FoundTripService foundTripService;

	public SearchResultsWrapper performSearch(SearchInputData searchData) {
		if (searchData == null) {
			return new SearchResultsWrapper(Messages.get("search.empty"));
		}
		ImmutableSearchInputData searchInputData = SearchDataConverter.convertToImmutable(searchData);
		FoundTripsWrapper foundTripsWrapper = googleService.getTrips(searchInputData);
		return getSearchResultsWrapper(foundTripsWrapper);
	}

	private SearchResultsWrapper getSearchResultsWrapper(FoundTripsWrapper foundTripsWrapper) {
		String errorMessage = foundTripsWrapper.getErrorMessage();
		if (StringUtils.isNotEmpty(errorMessage)) {
			return new SearchResultsWrapper(errorMessage);
		}
		List<FoundTrip> foundTrips = foundTripsWrapper.getFoundTrips();
		foundTripService.saveFoundTrips(foundTrips);
		List<SearchResult> searchResults = convertToSearchResults(foundTrips);
		return new SearchResultsWrapper(searchResults);
	}

	private List<SearchResult> convertToSearchResults(List<FoundTrip> foundTrips) {
		return foundTrips.stream()
				.map(SearchDataConverter::convertToSearchResult)
				.collect(Collectors.toList());
	}

}
