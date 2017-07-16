package com.flightbuddy.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.search.ImmutableSearchInputData;
import com.flightbuddy.search.SearchInputData;
import com.flightbuddy.search.SearchInputDataConverter;

public class SearchInputDataConverterTest {
		
	@Test
	public void convertToImmutableForScheduledSearchWithOneScheduledSearchWithOneDate() {
		LocalDate date = LocalDate.of(2017, 12, 2);
		ScheduledSearch scheduledSearch = createScheduledSearch("from", "to", "10.00", true, Collections.singletonList(date));
		ImmutableSearchInputData result = SearchInputDataConverter.convertToImmutable(scheduledSearch);
		assertEqualFields(result, scheduledSearch);
	}
	
	@Test
	public void convertToImmutableForScheduledSearchWithOneScheduledSearchWithThreeDates() {
		ScheduledSearch scheduledSearch = createScheduledSearchWithThreeDates("from", "to", "10.00", true);
		ImmutableSearchInputData result = SearchInputDataConverter.convertToImmutable(scheduledSearch);
		assertEqualFields(result, scheduledSearch);
	}
	
	@Test
	public void convertToImmutableForSearchInputDataWithOneScheduledSearchWithOneDate() {
		LocalDate date = LocalDate.of(2017, 12, 2);
		SearchInputData searchInputData = createSearchInputData("from", "to", "10.00", true, new LocalDate[] {date});
		ImmutableSearchInputData result = SearchInputDataConverter.convertToImmutable(searchInputData);
		assertEqualFields(result, searchInputData);
	}

	@Test
	public void convertToImmutableForSearchInputDataWithOneScheduledSearchWithThreeDates() {
		SearchInputData searchInputData = createSearchInputDataWithThreeDates("from", "to", "10.00", true);
		ImmutableSearchInputData result = SearchInputDataConverter.convertToImmutable(searchInputData);
		assertEqualFields(result, searchInputData);
	}

	private ScheduledSearch createScheduledSearchWithThreeDates(String from, String to, String price, boolean withReturn) {
		List<LocalDate> dates = Arrays.asList(LocalDate.of(2017, 8, 5), LocalDate.of(2017, 9, 12), LocalDate.of(2018, 3, 2));
		ScheduledSearch scheduledSearch = createScheduledSearch(from, to, price, withReturn, dates);
		return scheduledSearch;
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
	
	private SearchInputData createSearchInputDataWithThreeDates(String from, String to, String price, boolean withReturn) {
		LocalDate[] dates = new LocalDate[] {LocalDate.of(2017, 8, 5), LocalDate.of(2017, 9, 12), LocalDate.of(2018, 3, 2)};
		SearchInputData searchInputData = createSearchInputData(from, to, price, withReturn, dates);
		return searchInputData;
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

	private void assertEqualFields(ImmutableSearchInputData immutableSearchInputData, ScheduledSearch scheduledSearch) {
		assertThat(immutableSearchInputData.getFrom(), equalTo(scheduledSearch.getFrom()));
		assertThat(immutableSearchInputData.getTo(), equalTo(scheduledSearch.getTo()));
		BigDecimal price = scheduledSearch.getPrice();
		assertThat(immutableSearchInputData.getPrice(), equalTo(price.toString()));
		assertThat(immutableSearchInputData.isWithReturn(), equalTo(scheduledSearch.isWithReturn()));
		List<LocalDate> dates = scheduledSearch.getDates();
		assertThat(immutableSearchInputData.getDates(), equalTo(dates.toArray()));
	}
	
	private void assertEqualFields(ImmutableSearchInputData immutableSearchInputData, SearchInputData searchInputData) {
		assertThat(immutableSearchInputData.getFrom(), equalTo(searchInputData.getFrom()));
		assertThat(immutableSearchInputData.getTo(), equalTo(searchInputData.getTo()));
		assertThat(immutableSearchInputData.getPrice(), equalTo(searchInputData.getPrice()));
		assertThat(immutableSearchInputData.isWithReturn(), equalTo(searchInputData.isWithReturn()));
		assertThat(immutableSearchInputData.getDates(), equalTo(searchInputData.getDates()));
	}
}
