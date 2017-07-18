package com.flightbuddy.search;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.flightbuddy.results.Airline;
import com.flightbuddy.results.Flight;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.Stop;
import com.flightbuddy.schedule.search.ScheduledSearch;

public class SearchDataConverterTest {
		
	@Test
	public void convertToImmutableForScheduledSearchWithOneScheduledSearchWithOneDate() {
		LocalDate date = LocalDate.of(2017, 12, 2);
		ScheduledSearch scheduledSearch = createScheduledSearch("from", "to", "10.00", true, Collections.singletonList(date));
		ImmutableSearchInputData result = SearchDataConverter.convertToImmutable(scheduledSearch);
		assertEqualFields(result, scheduledSearch);
	}
	
	@Test
	public void convertToImmutableForScheduledSearchWithOneScheduledSearchWithThreeDates() {
		ScheduledSearch scheduledSearch = createScheduledSearchWithThreeDates("from", "to", "10.00", true);
		ImmutableSearchInputData result = SearchDataConverter.convertToImmutable(scheduledSearch);
		assertEqualFields(result, scheduledSearch);
	}
	
	@Test
	public void convertToImmutableForSearchInputDataWithOneScheduledSearchWithOneDate() {
		LocalDate date = LocalDate.of(2017, 12, 2);
		SearchInputData searchInputData = createSearchInputData("from", "to", "10.00", true, new LocalDate[] {date});
		ImmutableSearchInputData result = SearchDataConverter.convertToImmutable(searchInputData);
		assertEqualFields(result, searchInputData);
	}

	@Test
	public void convertToImmutableForSearchInputDataWithOneScheduledSearchWithThreeDates() {
		SearchInputData searchInputData = createSearchInputDataWithThreeDates("from", "to", "10.00", true);
		ImmutableSearchInputData result = SearchDataConverter.convertToImmutable(searchInputData);
		assertEqualFields(result, searchInputData);
	}
	
	@Test
	public void convertToSearchResultForFoundTrip() {
		LocalDateTime[] dates = new LocalDateTime[] {LocalDateTime.of(2017, 8, 29, 12, 30), LocalDateTime.of(2017, 9, 14, 14, 30)};
		int[] durations = new int[] {120, 65};
		String[][] airlineNames = new String[][] {{"KLM", "EZY"},{"RYA", "LFH"}};
		String[][] stopCodes = new String[][] {{"BSL", "AMS", "KRK"}, {"KRK", "LND", "BSL"}};
		FoundTrip foundTrip = createFoundTrip("150.00", dates, durations, airlineNames, stopCodes);
		SearchResult result = SearchDataConverter.convertToSearchResult(foundTrip);
		assertEqualFields(result, foundTrip);
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
	
	private FoundTrip createFoundTrip(String price, LocalDateTime[] dates, int[] durations, String[][] airlineNames, String[][] stopCodes) {
		FoundTrip foundTrip = new FoundTrip();
		foundTrip.setFlights(createFlights(dates, durations, airlineNames, stopCodes));
		foundTrip.setPrice(new BigDecimal(price));
		return foundTrip;
	}

	private List<Flight> createFlights(LocalDateTime[] dates, int[] durations, String[][] airlineNames, String[][] stopCodes) {
		List<Flight> flights = new ArrayList<>();
		flights.add(createFlight(dates[0], durations[0], airlineNames[0], stopCodes[0]));
		flights.add(createFlight(dates[1], durations[1], airlineNames[1], stopCodes[1]));
		return flights;
	}

	private Flight createFlight(LocalDateTime date, int duration, String[] airlineName, String[] stopCode) {
		Flight flight = new Flight();
		flight.setAirlines(new ArrayList<>());
		flight.setStops(new ArrayList<>());
		flight.setDate(date);
		flight.setDuration(duration);
		addAirline(airlineName[0], flight);
		addAirline(airlineName[1], flight);
		addStop(stopCode[0], flight);
		addStop(stopCode[1], flight);
		addStop(stopCode[2], flight);
		return flight;
	}

	private void addAirline(String airlineName, Flight flight) {
		List<Airline> airlines = flight.getAirlines();
		Airline airline = new Airline();
		airline.setName(airlineName);
		airlines.add(airline);
	}

	private void addStop(String code, Flight flight) {
		List<Stop> stops = flight.getStops();
		Stop stop = new Stop();
		stop.setCode(code);
		stops.add(stop);
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

	private void assertEqualFields(SearchResult result, FoundTrip foundTrip) {
		assertThat(result.getPrice(), equalTo(foundTrip.getPrice().toString()));
		assertThat(result.getDurations(), equalTo(Arrays.asList(new String[] {"2h", "1h 05m"})));
		assertThat(result.getHours(), equalTo(Arrays.asList(new String[] {"12:30-14:30", "14:30-15:35"})));
		assertThat(result.getStops().toArray(), equalTo(new int[] {1, 1}));
		assertThat(result.getTrips(), equalTo(Arrays.asList(new String[] {"BSL -> AMS -> KRK", "KRK -> LND -> BSL"})));
	}
}
