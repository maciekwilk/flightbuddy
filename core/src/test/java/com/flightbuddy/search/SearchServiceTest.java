package com.flightbuddy.search;

import com.flightbuddy.Application;
import com.flightbuddy.google.GoogleService;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.FoundTripService;
import com.flightbuddy.results.FoundTripsWrapper;
import com.flightbuddy.schedule.search.ScheduledSearch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@ExtendWith(PowerMockExtension.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
@SpringBootTest(classes = Application.class)
@PrepareForTest(SearchDataConverter.class)
public class SearchServiceTest {

	@Autowired
	private SearchService searchService;
	@MockBean
	private GoogleService googleService;
    @MockBean
	private FoundTripService foundTripService;

    private ImmutableSearchInputData emptyInputData;

    @BeforeEach
	public void setUp() {
    	PassengersTO passengers = new PassengersTO();
    	ImmutablePassengers immutablePassengers = new ImmutablePassengers(passengers);
		emptyInputData = new ImmutableSearchInputData(null, null, 0, 0, new LocalDate[]{}, false, immutablePassengers);
		SearchResult emptyConvertedSearchResult = new SearchResult(0, emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList());
		mockStatic(SearchDataConverter.class);
		when(SearchDataConverter.convertToImmutable(any(ScheduledSearch.class))).thenReturn(emptyInputData);
		when(SearchDataConverter.convertToImmutable(any(SearchInputData.class))).thenReturn(emptyInputData);
		when(SearchDataConverter.convertToSearchResult(any())).thenReturn(emptyConvertedSearchResult);
	}

	@Test
	public void performSearchWithEmptyInputDataReturnsEmptyList() {
		SearchResultsWrapper results = searchService.performSearch(null);
		assertEquals(results.getSearchResults(), Collections.emptyList());
		verify(googleService, times(0)).getTrips(any());
		verify(foundTripService, times(0)).saveFoundTrips(any());
	}

	@Test
	public void performSearchWithInputDataAndNoFoundTrips() {
		SearchInputData searchData = new SearchInputData();
		when(googleService.getTrips(eq(emptyInputData))).thenReturn(new FoundTripsWrapper("error"));
		SearchResultsWrapper results = searchService.performSearch(searchData);
		assertEquals(results.getSearchResults(), Collections.emptyList());
		verify(googleService, times(1)).getTrips(any());
		verify(foundTripService, times(0)).saveFoundTrips(any());
	}

	@Test
	public void performSearchWithInputDataAndFoundTrip() {
		SearchInputData searchData = new SearchInputData();
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
		SearchInputData searchData = new SearchInputData();
		List<FoundTrip> trips = createFoundTripsWithThreeTrips();
		when(googleService.getTrips(any())).thenReturn(new FoundTripsWrapper(trips));
		SearchResultsWrapper results = searchService.performSearch(searchData);
		List<SearchResult> searchResults = results.getSearchResults();
		assertEquals(searchResults.size(), 3);
		verify(googleService, times(1)).getTrips(any());
		verify(foundTripService, times(1)).saveFoundTrips(any());
	}

	private List<FoundTrip> createFoundTripsWithOneTrip() {
		List<FoundTrip> trips = new ArrayList<>(1);
		trips.add(new FoundTrip());
		return trips;
	}

	private List<FoundTrip> createFoundTripsWithThreeTrips() {
		List<FoundTrip> trips = new ArrayList<>(3);
		trips.add(new FoundTrip());
		trips.add(new FoundTrip());
		trips.add(new FoundTrip());
		return trips;
	}
}
