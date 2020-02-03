package com.flightbuddy.search;

import com.flightbuddy.Application;
import com.flightbuddy.google.GoogleService;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.FoundTripService;
import com.flightbuddy.results.FoundTripsWrapper;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static com.flightbuddy.search.SearchTestUtil.createFoundTrip;
import static com.flightbuddy.search.SearchTestUtil.createSearchInputDataWithThreeDates;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class SearchServiceTest {

	@Autowired
	private SearchService searchService;
	@MockBean
	private GoogleService googleService;
    @MockBean
	private FoundTripService foundTripService;

	@Test
	public void performSearchWithEmptyInputDataReturnsEmptyList() {
		SearchResultsWrapper results = searchService.performSearch(null);
		assertEquals(results.getSearchResults(), Collections.emptyList());
		verify(googleService, times(0)).getTrips(any());
		verify(foundTripService, times(0)).saveFoundTrips(any());
	}

	@Test
	public void performSearchWithInputDataAndNoFoundTrips() {
        SearchInputData searchData = createSearchInputDataWithThreeDates();
		when(googleService.getTrips(any())).thenReturn(new FoundTripsWrapper("error"));
		SearchResultsWrapper results = searchService.performSearch(searchData);
		assertEquals(results.getSearchResults(), Collections.emptyList());
		verify(googleService, times(1)).getTrips(any());
		verify(foundTripService, times(0)).saveFoundTrips(any());
	}

	@Test
	public void performSearchWithInputDataAndFoundTrip() {
        SearchInputData searchData = createSearchInputDataWithThreeDates();
		List<FoundTrip> trips = createFoundTripsWithOneTrip();
		when(googleService.getTrips(any())).thenReturn(new FoundTripsWrapper(trips));
		SearchResultsWrapper results = searchService.performSearch(searchData);
		List<SearchResult> searchResults = results.getSearchResults();
		assertEquals(searchResults.size(), 1);
		verify(googleService, times(1)).getTrips(any());
		verify(foundTripService, times(1)).saveFoundTrips(any());
	}

	@Test
	public void performSearchWithInputDataAndFewFoundTrips() {
        SearchInputData searchData = createSearchInputDataWithThreeDates();
		List<FoundTrip> trips = createFoundTripsWithThreeTrips();
		when(googleService.getTrips(any())).thenReturn(new FoundTripsWrapper(trips));
		SearchResultsWrapper results = searchService.performSearch(searchData);
		List<SearchResult> searchResults = results.getSearchResults();
		assertEquals(searchResults.size(), 3);
		verify(googleService, times(1)).getTrips(any());
		verify(foundTripService, times(1)).saveFoundTrips(any());
	}

	private List<FoundTrip> createFoundTripsWithOneTrip() {
		return ImmutableList.of(createFoundTrip());
	}

	private List<FoundTrip> createFoundTripsWithThreeTrips() {
		return ImmutableList.of(createFoundTrip(), createFoundTrip(), createFoundTrip());
	}
}
