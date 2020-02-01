package com.flightbuddy.google;

import com.flightbuddy.Application;
import com.flightbuddy.google.response.GoogleResponse;
import com.flightbuddy.google.response.Trips;
import com.flightbuddy.google.response.tripoption.TripOption;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.FoundTripsWrapper;
import com.flightbuddy.schedule.ScheduleRunnable;
import com.flightbuddy.schedule.search.ScheduledSearchService;
import com.flightbuddy.search.ImmutablePassengers;
import com.flightbuddy.search.ImmutableSearchInputData;
import com.flightbuddy.search.PassengersTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@SuppressWarnings("unused")
@ExtendWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
@SpringBootTest(classes = Application.class)
@PrepareForTest(GoogleFlightConverter.class)
public class GoogleServiceTest {

	@Autowired
	private GoogleService googleService;

	@MockBean
	private GoogleConnectionService googleConnectionService;
	@MockBean
	private ScheduleRunnable googleTask;
	@MockBean
	private ScheduledSearchService scheduledSearchService;

	private ImmutableSearchInputData emptyInputData;

	@BeforeEach
	public void setUp() {
		ImmutablePassengers immutablePassengers = new ImmutablePassengers(new PassengersTO());
		emptyInputData = new ImmutableSearchInputData(null, null, 0, 0, new LocalDate[]{}, false, immutablePassengers);
		mockStatic(GoogleFlightConverter.class);
	}

	@Test
	public void checkGetGoogleFlightsWithEmptyGoogleConnectionServiceResponse() {
		GoogleResponse emptyGoogleResponse = new GoogleResponse();
		when(googleConnectionService.askGoogleForTheTrips(any())).thenReturn(emptyGoogleResponse);
        FoundTripsWrapper result = googleService.getTrips(emptyInputData);
        assertThat(result.getFoundTrips(), is(Collections.emptyList()));
		verifyStatic(GoogleFlightConverter.class, times(0));
		GoogleFlightConverter.convertResponseToTrips(emptyGoogleResponse, 0);
	}

	@Test
	public void checkGetGoogleFlightsWithGoogleConnectionServiceResponseWithNullTripOption() {
		Trips trips = createTrips(null);
		GoogleResponse googleResponse = createGoogleResponse(trips);
		when(googleConnectionService.askGoogleForTheTrips(any())).thenReturn(googleResponse);
		FoundTripsWrapper result = googleService.getTrips(emptyInputData);
		assertThat(result.getFoundTrips(), is(Collections.emptyList()));
		verifyStatic(GoogleFlightConverter.class, times(0));
		GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
	}

	@Test
	public void checkGetGoogleFlightsWithGoogleConnectionServiceResponseWithEmptyTripOption() {
		Trips trips = createTrips(new TripOption[0]);
		GoogleResponse googleResponse = createGoogleResponse(trips);
		when(googleConnectionService.askGoogleForTheTrips(any())).thenReturn(googleResponse);
        FoundTripsWrapper result = googleService.getTrips(emptyInputData);
        assertThat(result.getFoundTrips(), is(Collections.emptyList()));
		verifyStatic(GoogleFlightConverter.class, times(0));
		GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
	}

	@Test
	public void checkGetGoogleFlightsWithValidGoogleConnectionServiceResponse() {
		TripOption[] tripOption = new TripOption[]{new TripOption()};
		Trips trips = createTrips(tripOption);
		GoogleResponse googleResponse = createGoogleResponse(trips);
		when(googleConnectionService.askGoogleForTheTrips(any())).thenReturn(googleResponse);
		List<FoundTrip> conversionResult = mockGoogleFlightConverter(googleResponse);
		FoundTripsWrapper result = googleService.getTrips(emptyInputData);
		List<FoundTrip> foundTrips = result.getFoundTrips();
		assertThat(foundTrips, equalTo(conversionResult));
		verifyStatic(GoogleFlightConverter.class, times(1));
		GoogleFlightConverter.convertResponseToTrips(googleResponse, 0);
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
		when(GoogleFlightConverter.convertResponseToTrips(eq(googleResponse), anyInt())).thenReturn(conversionResult);
		return conversionResult;
	}
}