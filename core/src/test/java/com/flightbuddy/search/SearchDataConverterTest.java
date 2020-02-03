package com.flightbuddy.search;

import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.schedule.search.Passengers;
import com.flightbuddy.schedule.search.ScheduledSearch;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

import static com.flightbuddy.search.SearchTestUtil.assertEqualFields;
import static com.flightbuddy.search.SearchTestUtil.createFoundTrip;
import static com.flightbuddy.search.SearchTestUtil.createPassengers;
import static com.flightbuddy.search.SearchTestUtil.createPassengersTO;
import static com.flightbuddy.search.SearchTestUtil.createScheduledSearch;
import static com.flightbuddy.search.SearchTestUtil.createScheduledSearchWithThreeDates;
import static com.flightbuddy.search.SearchTestUtil.createSearchInputData;
import static com.flightbuddy.search.SearchTestUtil.createSearchInputDataWithThreeDates;

public class SearchDataConverterTest {
		
	@Test
	public void convertToImmutableForScheduledSearchWithOneScheduledSearchWithOneDate() {
		LocalDate date = LocalDate.of(2017, 12, 2);
		Passengers passengers = createPassengers(3, 1, 5, 2, 3);
		ScheduledSearch scheduledSearch = createScheduledSearch(Collections.singletonList(date), passengers);
		ImmutableSearchInputData result = SearchDataConverter.convertToImmutable(scheduledSearch);
		assertEqualFields(result, scheduledSearch);
	}
	
	@Test
	public void convertToImmutableForScheduledSearchWithOneScheduledSearchWithThreeDates() {
		Passengers passengers = createPassengers(1, 2, 3, 10, 2);
		ScheduledSearch scheduledSearch = createScheduledSearchWithThreeDates(passengers);
		ImmutableSearchInputData result = SearchDataConverter.convertToImmutable(scheduledSearch);
		assertEqualFields(result, scheduledSearch);
	}
	
	@Test
	public void convertToImmutableForSearchInputDataWithOneScheduledSearchWithOneDate() {
		LocalDate date = LocalDate.of(2017, 12, 2);
		PassengersTO passengers = createPassengersTO(3, 1, 5, 2, 3);
		SearchInputData searchInputData = createSearchInputData(new LocalDate[] {date}, passengers);
		ImmutableSearchInputData result = SearchDataConverter.convertToImmutable(searchInputData);
		assertEqualFields(result, searchInputData);
	}

	@Test
	public void convertToImmutableForSearchInputDataWithOneScheduledSearchWithThreeDates() {
		PassengersTO passengers = createPassengersTO(1, 2, 3, 10, 2);
		SearchInputData searchInputData = createSearchInputDataWithThreeDates(passengers);
		ImmutableSearchInputData result = SearchDataConverter.convertToImmutable(searchInputData);
		assertEqualFields(result, searchInputData);
	}
	
	@Test
	public void convertToSearchResultForFoundTrip() {
		FoundTrip foundTrip = createFoundTrip();
		SearchResult result = SearchDataConverter.convertToSearchResult(foundTrip);
		assertEqualFields(result, foundTrip);
	}
}
