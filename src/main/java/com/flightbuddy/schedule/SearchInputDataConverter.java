package com.flightbuddy.schedule;

import java.time.LocalDate;

import com.flightbuddy.SearchInputData;
import com.flightbuddy.schedule.search.ScheduledSearch;

public class SearchInputDataConverter {

	public static SearchInputData convert(ScheduledSearch scheduledSearch) {
		String from = scheduledSearch.getFrom();
		String to = scheduledSearch.getTo();
		String price = String.valueOf(scheduledSearch.getPrice());
		boolean withReturn = scheduledSearch.isWithReturn();
		LocalDate[] dates = scheduledSearch.getDates().toArray(new LocalDate[]{});
		SearchInputData searchInputData = new SearchInputData(from, to, price, dates, withReturn);
		return searchInputData;
	}
}
