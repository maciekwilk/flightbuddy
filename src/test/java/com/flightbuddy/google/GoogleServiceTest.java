package com.flightbuddy.google;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.flightbuddy.Application;
import com.flightbuddy.SearchInputData;
import com.flightbuddy.google.response.GoogleResponse;
import com.flightbuddy.google.response.Trips;
import com.flightbuddy.google.response.tripoption.TripOption;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.schedule.ScheduleTask;
import com.flightbuddy.schedule.ScheduledSearch;
import com.flightbuddy.schedule.ScheduledSearchService;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@PrepareForTest(GoogleFlightConverter.class)
public class GoogleServiceTest {
	
	@Autowired
	private GoogleService googleService;
	
	@MockBean
	private GoogleConnectionService googleConnectionService;
	@MockBean
	private ScheduleTask googleTask;
	@MockBean
	private ScheduledSearchService scheduledSearchService;
	
	private SearchInputData emptyInputData;

	@Before
	public void setUp() {
		emptyInputData = new SearchInputData(null, null, null, new LocalDate[]{}, false);
		mockStatic(GoogleFlightConverter.class);
	}
	
	@Test
	public void checkGetGoogleFlightsWithEmptyGoogleConnectionServiceResponse() {
		GoogleResponse emptyGoogleResponse = new GoogleResponse();
		when(googleConnectionService.askGoogleForTheTrips(any())).thenReturn(emptyGoogleResponse);
		googleService.getTrips(emptyInputData);
		verifyStatic(times(1));
		GoogleFlightConverter.convertResponseToTrips(emptyGoogleResponse);
	}
	
	@Test
	public void checkGetGoogleFlightsWithGoogleConnectionServiceResponseWithNullTripOption() {
		Trips trips = createTrips(null);
		GoogleResponse googleResponse = createGoogleResponse(trips);
		when(googleConnectionService.askGoogleForTheTrips(any())).thenReturn(googleResponse);
		googleService.getTrips(emptyInputData);
		verifyStatic(times(1));
		GoogleFlightConverter.convertResponseToTrips(googleResponse);
	}
	
	@Test
	public void checkGetGoogleFlightsWithGoogleConnectionServiceResponseWithEmptyTripOption() {
		Trips trips = createTrips(new TripOption[0]);
		GoogleResponse googleResponse = createGoogleResponse(trips);
		when(googleConnectionService.askGoogleForTheTrips(any())).thenReturn(googleResponse);
		googleService.getTrips(emptyInputData);
		verifyStatic(times(1));
		GoogleFlightConverter.convertResponseToTrips(googleResponse);
	}
	
	@Test
	public void checkGetGoogleFlightsWithValidGoogleConnectionServiceResponse() {
		TripOption[] tripOption = new TripOption[]{new TripOption()};
		Trips trips = createTrips(tripOption);
		GoogleResponse googleResponse = createGoogleResponse(trips);
		when(googleConnectionService.askGoogleForTheTrips(any())).thenReturn(googleResponse);
		List<FoundTrip> conversionResult = mockGoogleFlightConverter(googleResponse);
		List<FoundTrip> result = googleService.getTrips(emptyInputData);
		assertThat(result, equalTo(conversionResult));
		verifyStatic(times(1));
		GoogleFlightConverter.convertResponseToTrips(googleResponse);
	}
	
	@Test
	public void getInputDataForScheduledSearchWithoutScheduledSearches() {
		when(scheduledSearchService.getAllScheduledSearches()).thenReturn(Collections.emptyList());
		List<SearchInputData> result = googleService.getInputDataForScheduledSearch();
		verify(scheduledSearchService, times(1)).getAllScheduledSearches();
		assertThat(result, equalTo(Collections.emptyList()));
	}
	
	@Test
	public void getInputDataForScheduledSearchWithOneScheduledSearchWithOneDate() {
		LocalDate date = LocalDate.of(2017, 12, 2);
		ScheduledSearch scheduledSearch = createScheduledSearch("from", "to", "10.00", true, Collections.singletonList(date));
		when(scheduledSearchService.getAllScheduledSearches()).thenReturn(Collections.singletonList(scheduledSearch));
		List<SearchInputData> result = googleService.getInputDataForScheduledSearch();
		verify(scheduledSearchService, times(1)).getAllScheduledSearches();
		assertThat(result.size(), equalTo(1));
		assertEqualFields(result.get(0), scheduledSearch);
	}
	
	@Test
	public void getInputDataForScheduledSearchWithOneScheduledSearchWithThreeDates() {
		ScheduledSearch scheduledSearch = createScheduledSearchWithThreeDates("from", "to", "10.00", true);
		when(scheduledSearchService.getAllScheduledSearches()).thenReturn(Collections.singletonList(scheduledSearch));
		List<SearchInputData> result = googleService.getInputDataForScheduledSearch();
		verify(scheduledSearchService, times(1)).getAllScheduledSearches();
		assertThat(result.size(), equalTo(1));
		assertEqualFields(result.get(0), scheduledSearch);
	}
	
	@Test
	public void getInputDataForScheduledSearchWithThreeScheduledSearch() {
		ScheduledSearch firstScheduledSearch = createScheduledSearchWithThreeDates("from1", "to1", "10.00", true);
		ScheduledSearch secondScheduledSearch = createScheduledSearchWithThreeDates("from2", "to2", "120.00", false);
		ScheduledSearch thirdScheduledSearch = createScheduledSearchWithThreeDates("from3", "to3", "25.00", true);
		when(scheduledSearchService.getAllScheduledSearches()).thenReturn(Arrays.asList(
											new ScheduledSearch[]{firstScheduledSearch, secondScheduledSearch, thirdScheduledSearch}
											));
		List<SearchInputData> result = googleService.getInputDataForScheduledSearch();
		verify(scheduledSearchService, times(1)).getAllScheduledSearches();
		assertThat(result.size(), equalTo(3));
		assertEqualFields(result.get(0), firstScheduledSearch);
		assertEqualFields(result.get(1), secondScheduledSearch);
		assertEqualFields(result.get(2), thirdScheduledSearch);
	}

	private Trips createTrips(TripOption[] tripOption) {
		Trips trips = new Trips();
		trips.setTripOption(tripOption);
		return trips;
	}

	private GoogleResponse createGoogleResponse(Trips trips) {
		GoogleResponse googleResponse = new GoogleResponse();
		googleResponse.setTrips(trips);
		return googleResponse;
	}

	private List<FoundTrip> mockGoogleFlightConverter(GoogleResponse googleResponse) {
		List<FoundTrip> conversionResult = new ArrayList<>(1);
		conversionResult.add(new FoundTrip());
		when(GoogleFlightConverter.convertResponseToTrips(googleResponse)).thenReturn(conversionResult);
		return conversionResult;
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