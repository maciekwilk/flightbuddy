package com.flightbuddy.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.flightbuddy.SearchInputData;
import com.flightbuddy.schedule.search.ScheduledSearch;

public class SearchInputDataConverterTest {
		
	@Test
	public void getInputDataForScheduledSearchWithOneScheduledSearchWithOneDate() {
		LocalDate date = LocalDate.of(2017, 12, 2);
		ScheduledSearch scheduledSearch = createScheduledSearch("from", "to", "10.00", true, Collections.singletonList(date));
		SearchInputData result = SearchInputDataConverter.convert(scheduledSearch);
		assertEqualFields(result, scheduledSearch);
	}
	
	@Test
	public void getInputDataForScheduledSearchWithOneScheduledSearchWithThreeDates() {
		ScheduledSearch scheduledSearch = createScheduledSearchWithThreeDates("from", "to", "10.00", true);
		SearchInputData result = SearchInputDataConverter.convert(scheduledSearch);
		assertEqualFields(result, scheduledSearch);
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

	private void assertEqualFields(SearchInputData searchInputData, ScheduledSearch scheduledSearch) {
		assertThat(searchInputData.getFrom(), equalTo(scheduledSearch.getFrom()));
		assertThat(searchInputData.getTo(), equalTo(scheduledSearch.getTo()));
		BigDecimal price = scheduledSearch.getPrice();
		assertThat(searchInputData.getPrice(), equalTo(price.toString()));
		assertThat(searchInputData.isWithReturn(), equalTo(scheduledSearch.isWithReturn()));
		List<LocalDate> dates = scheduledSearch.getDates();
		assertThat(searchInputData.getDates(), equalTo(dates.toArray()));
	}
}
