package com.flightbuddy.google;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.flightbuddy.SearchInputData;
import com.flightbuddy.exceptions.NoFlightsFoundException;
import com.flightbuddy.google.response.GoogleResponse;
import com.flightbuddy.google.response.Trips;
import com.flightbuddy.google.response.tripoption.TripOption;
import com.flightbuddy.results.FoundTrip;

@RunWith(MockitoJUnitRunner.class)
//@ContextConfiguration(classes = { Application.class})
//@TestPropertySource({"classpath:environment.properties"})
public class GoogleServiceTest {
	
	private GoogleService googleService;
	
	@Mock
	private GoogleConnectionService googleConnectionService;
	@Mock
	private GoogleFlightConverter googleFlightConverter;
	
	@Before
	public void setUp() {
		googleService = new GoogleService();
		googleService.setGoogleConnectionService(googleConnectionService);
		googleService.setGoogleFlightConverter(googleFlightConverter);
	}
	
	@Test(expected = NoFlightsFoundException.class)
	public void checkGetGoogleFlightsWithEmptyGoogleConnectionServiceResponse() {
		when(googleConnectionService.askGoogleForTheFlights(any())).thenReturn(new GoogleResponse());
		 googleService.getGoogleFlights(new SearchInputData());
	}
	
	@Test(expected = NoFlightsFoundException.class)
	public void checkGetGoogleFlightsWithGoogleConnectionServiceResponseWithNullTripOption() {
		Trips trips = createTrips(null);
		mockGoogleConnectionService(trips);
		googleService.getGoogleFlights(new SearchInputData());
	}
	
	@Test(expected = NoFlightsFoundException.class)
	public void checkGetGoogleFlightsWithGoogleConnectionServiceResponseWithEmptyTripOption() {
		Trips trips = createTrips(new TripOption[0]);
		mockGoogleConnectionService(trips);
		googleService.getGoogleFlights(new SearchInputData());
	}
	
	@Test
	public void checkGetGoogleFlightsWithValidGoogleConnectionServiceResponse() {
		TripOption[] tripOption = new TripOption[1];
		tripOption[0] = new TripOption();
		Trips trips = createTrips(tripOption);
		GoogleResponse googleResponse = mockGoogleConnectionService(trips);
		List<FoundTrip> conversionResult = mockGoogleFlightConverter(googleResponse);
		List<FoundTrip> result = googleService.getGoogleFlights(new SearchInputData());
		verify(googleFlightConverter, times(1)).convertResponseToTrips(googleResponse);
		assertThat(result, equalTo(conversionResult));
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

	private GoogleResponse mockGoogleConnectionService(Trips trips) {
		GoogleResponse googleResponse = createGoogleResponse(trips);
		when(googleConnectionService.askGoogleForTheFlights(any())).thenReturn(googleResponse);
		return googleResponse;
	}

	private List<FoundTrip> mockGoogleFlightConverter(GoogleResponse googleResponse) {
		List<FoundTrip> conversionResult = new ArrayList<>(1);
		conversionResult.add(new FoundTrip());
		when(googleFlightConverter.convertResponseToTrips(googleResponse)).thenReturn(conversionResult);
		return conversionResult;
	}
}