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
import com.flightbuddy.schedule.search.Passengers;
import com.flightbuddy.schedule.search.ScheduledSearch;

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
		LocalDateTime[] datesTo = new LocalDateTime[] {LocalDateTime.of(2017, 8, 29, 12, 30), LocalDateTime.of(2017, 8, 29, 14, 30), 
				LocalDateTime.of(2017, 8, 29, 15, 30), LocalDateTime.of(2017, 8, 29, 17, 0)};
		LocalDateTime[] datesFrom = new LocalDateTime[] {LocalDateTime.of(2017, 9, 14, 14, 30), LocalDateTime.of(2017, 9, 14, 16, 0),
				LocalDateTime.of(2017, 9, 14, 17, 0), LocalDateTime.of(2017, 9, 14, 18, 15)};
		int[] durations = new int[] {120, 65};
		String[][] airlineNames = new String[][] {{"KLM", "EZY"},{"RYA", "LFH"}};
		String[][] stopCodes = new String[][] {{"BSL", "AMS", "KRK"}, {"KRK", "LND", "BSL"}};
		FoundTrip foundTrip = createFoundTrip(datesTo, datesFrom, durations, airlineNames, stopCodes);
		SearchResult result = SearchDataConverter.convertToSearchResult(foundTrip);
		assertEqualFields(result, foundTrip);
	}

	private Passengers createPassengers(int adultCount, int childCount, int infantInLapCount, int infantInSeatCount, int seniorCount) {
		Passengers passengers = new Passengers();
		passengers.setAdultCount(adultCount);
		passengers.setChildCount(childCount);
		passengers.setInfantInLapCount(infantInLapCount);
		passengers.setInfantInSeatCount(infantInSeatCount);
		passengers.setSeniorCount(seniorCount);
		return passengers;
	}

	private ScheduledSearch createScheduledSearchWithThreeDates(Passengers passengers) {
		List<LocalDate> dates = Arrays.asList(LocalDate.of(2017, 8, 5), LocalDate.of(2017, 9, 12), LocalDate.of(2018, 3, 2));
		return createScheduledSearch(dates, passengers);
	}

	private ScheduledSearch createScheduledSearch(List<LocalDate> dates, Passengers passengers) {
		ScheduledSearch scheduledSearch = new ScheduledSearch();
		scheduledSearch.setFrom("from");
		scheduledSearch.setTo("to");
		scheduledSearch.setMinPrice(0);
		scheduledSearch.setMaxPrice(10);
		scheduledSearch.setWithReturn(true);
		scheduledSearch.setDates(dates);
		scheduledSearch.setPassengers(passengers);
		return scheduledSearch;
	}

	private PassengersTO createPassengersTO(int adultCount, int childCount, int infantInLapCount, int infantInSeatCount, int seniorCount) {
		PassengersTO passengers = new PassengersTO();
		passengers.adultCount = adultCount;
		passengers.childCount = childCount;
		passengers.infantInLapCount = infantInLapCount;
		passengers.infantInSeatCount = infantInSeatCount;
		passengers.seniorCount = seniorCount;
		return passengers;
	}
	
	private SearchInputData createSearchInputDataWithThreeDates(PassengersTO passengers) {
		LocalDate[] dates = new LocalDate[] {LocalDate.of(2017, 8, 5), LocalDate.of(2017, 9, 12), LocalDate.of(2018, 3, 2)};
		return createSearchInputData(dates, passengers);
	}

	private SearchInputData createSearchInputData(LocalDate[] dates, PassengersTO passengers) {
		SearchInputData searchInputData = new SearchInputData();
		searchInputData.setFrom("from");
		searchInputData.setTo("to");
		searchInputData.setMinPrice(0);
		searchInputData.setMaxPrice(10);
		searchInputData.setWithReturn(true);
		searchInputData.setDates(dates);
		searchInputData.setPassengers(passengers);
		return searchInputData;
	}
	
	private FoundTrip createFoundTrip(LocalDateTime[] datesTo, LocalDateTime[] datesFrom, int[] durations, String[][] airlineNames,
									  String[][] stopCodes) {
		FoundTrip foundTrip = new FoundTrip();
		foundTrip.setFlights(createFlights(datesTo, datesFrom, durations, airlineNames, stopCodes));
		foundTrip.setPrice(new BigDecimal("150.00"));
		return foundTrip;
	}

	private List<Flight> createFlights(LocalDateTime[] datesTo, LocalDateTime[] datesFrom, int[] durations, String[][] airlineNames, String[][] stopCodes) {
		List<Flight> flights = new ArrayList<>();
		flights.add(createFlight(datesTo, durations[0], airlineNames[0], stopCodes[0]));
		flights.add(createFlight(datesFrom, durations[1], airlineNames[1], stopCodes[1]));
		return flights;
	}

	private Flight createFlight(LocalDateTime[] dates, int duration, String[] airlineName, String[] stopCode) {
		Flight flight = new Flight();
		flight.setAirlines(new ArrayList<>());
		flight.setStops(new ArrayList<>());
		flight.setDate(dates[0]);
		flight.setDuration(duration);
		addAirline(airlineName[0], flight);
		addAirline(airlineName[1], flight);
		addStop(stopCode[0], flight, null, dates[0]);
		addStop(stopCode[1], flight, dates[1], dates[2]);
		addStop(stopCode[2], flight, dates[3], null);
		return flight;
	}

	private void addAirline(String airlineName, Flight flight) {
		List<Airline> airlines = flight.getAirlines();
		Airline airline = new Airline();
		airline.setName(airlineName);
		airlines.add(airline);
	}

	private void addStop(String code, Flight flight, LocalDateTime arrivalTime, LocalDateTime departureTime) {
		List<Stop> stops = flight.getStops();
		Stop stop = new Stop();
		stop.setCode(code);
		stop.setArrivalTime(arrivalTime);
		stop.setDepartureTime(departureTime);
		stops.add(stop);
	}

	private void assertEqualFields(ImmutableSearchInputData immutableSearchInputData, ScheduledSearch scheduledSearch) {
		assertThat(immutableSearchInputData.getFrom(), equalTo(scheduledSearch.getFrom()));
		assertThat(immutableSearchInputData.getTo(), equalTo(scheduledSearch.getTo()));
		assertThat(immutableSearchInputData.getMaxPrice(), equalTo(scheduledSearch.getMaxPrice()));
		assertThat(immutableSearchInputData.getMinPrice(), equalTo(scheduledSearch.getMinPrice()));
		assertThat(immutableSearchInputData.isWithReturn(), equalTo(scheduledSearch.isWithReturn()));
		List<LocalDate> dates = scheduledSearch.getDates();
		assertThat(immutableSearchInputData.getDates(), equalTo(dates.toArray()));
		assertPassengersEqual(immutableSearchInputData, scheduledSearch);
	}
	
	private void assertPassengersEqual(ImmutableSearchInputData immutableSearchInputData, ScheduledSearch scheduledSearch) {
		ImmutablePassengers scheduledSearchPassengers = new ImmutablePassengers(scheduledSearch.getPassengers());
		ImmutablePassengers immutablePassengers = immutableSearchInputData.getPassengers();
		assertThat(immutablePassengers.getAdultCount(), equalTo(scheduledSearchPassengers.getAdultCount()));
		assertThat(immutablePassengers.getChildCount(), equalTo(scheduledSearchPassengers.getChildCount()));
		assertThat(immutablePassengers.getInfantInLapCount(), equalTo(scheduledSearchPassengers.getInfantInLapCount()));
		assertThat(immutablePassengers.getInfantInSeatCount(), equalTo(scheduledSearchPassengers.getInfantInSeatCount()));
		assertThat(immutablePassengers.getSeniorCount(), equalTo(scheduledSearchPassengers.getSeniorCount()));
	}

	private void assertEqualFields(ImmutableSearchInputData immutableSearchInputData, SearchInputData searchInputData) {
		assertThat(immutableSearchInputData.getFrom(), equalTo(searchInputData.getFrom()));
		assertThat(immutableSearchInputData.getTo(), equalTo(searchInputData.getTo()));
		assertThat(immutableSearchInputData.getMaxPrice(), equalTo(searchInputData.getMaxPrice()));
		assertThat(immutableSearchInputData.isWithReturn(), equalTo(searchInputData.isWithReturn()));
		assertThat(immutableSearchInputData.getDates(), equalTo(searchInputData.getDates()));
	}

	private void assertEqualFields(SearchResult result, FoundTrip foundTrip) {
		assertThat(result.getPrice(), equalTo(foundTrip.getPrice().intValue()));
		assertThat(result.getDurations(), equalTo(Arrays.asList("2h", "1h 05m")));
		assertThat(result.getHours(), equalTo(Arrays.asList("12:30-14:30", "14:30-15:35")));
		assertThat(result.getStops().toArray(), equalTo(new int[] {1, 1}));
		assertThat(result.getTrips(), equalTo(Arrays.asList("BSL -> AMS -> KRK (KLM -> EZY)", "KRK -> LND -> BSL (RYA -> LFH)")));
		assertThat(result.getDates(), equalTo(Arrays.asList("TUESDAY, AUGUST 29", "THURSDAY, SEPTEMBER 14")));
	}
}
