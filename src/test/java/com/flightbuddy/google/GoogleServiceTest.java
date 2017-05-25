package com.flightbuddy.google;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import java.time.LocalDate;
import java.util.ArrayList;
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

import com.flightbuddy.SearchInputData;
import com.flightbuddy.google.response.GoogleResponse;
import com.flightbuddy.google.response.Trips;
import com.flightbuddy.google.response.tripoption.TripOption;
import com.flightbuddy.google.schedule.GoogleTask;
import com.flightbuddy.results.FoundTrip;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest
@PrepareForTest(GoogleFlightConverter.class)
public class GoogleServiceTest {
	
	@Autowired
	private GoogleService googleService;
	
	@MockBean
	private GoogleConnectionService googleConnectionService;
	@MockBean
	private GoogleTask googleTask;
	
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
}