package com.flightbuddy.search;

import java.time.LocalDate;

import com.flightbuddy.schedule.search.ScheduledSearch;

public class SearchInputDataConverter {

	public static ImmutableSearchInputData convertToImmutable(ScheduledSearch scheduledSearch) {
		String from = scheduledSearch.getFrom();
		String to = scheduledSearch.getTo();
		String price = String.valueOf(scheduledSearch.getPrice());
		boolean withReturn = scheduledSearch.isWithReturn();
		LocalDate[] dates = scheduledSearch.getDates().toArray(new LocalDate[]{});
		return new ImmutableSearchInputData(from, to, price, dates, withReturn);
	}
	
	public static ImmutableSearchInputData convertToImmutable(SearchInputData searchInputData) {
		String from = searchInputData.getFrom();
		String to = searchInputData.getTo();
		String price = searchInputData.getPrice();
		boolean withReturn = searchInputData.isWithReturn();
		LocalDate[] dates = searchInputData.getDates();
		return new ImmutableSearchInputData(from, to, price, dates, withReturn);
	}
}
