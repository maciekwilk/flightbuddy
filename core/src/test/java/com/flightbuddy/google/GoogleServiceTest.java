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
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SuppressWarnings("unused")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
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
	}

	@Test
	public void checkGetGoogleFlightsWithEmptyGoogleConnectionServiceResponse() {
		GoogleResponse emptyGoogleResponse = new GoogleResponse();
		when(googleConnectionService.askGoogleForTheTrips(any())).thenReturn(emptyGoogleResponse);
        FoundTripsWrapper result = googleService.getTrips(emptyInputData);
        assertThat(result.getFoundTrips(), is(Collections.emptyList()));
	}

	@Test
	public void checkGetGoogleFlightsWithGoogleConnectionServiceResponseWithNullTripOption() {
		Trips trips = createTrips(null);
		GoogleResponse googleResponse = createGoogleResponse(trips);
		when(googleConnectionService.askGoogleForTheTrips(any())).thenReturn(googleResponse);
		FoundTripsWrapper result = googleService.getTrips(emptyInputData);
		assertThat(result.getFoundTrips(), is(Collections.emptyList()));
	}

	@Test
	public void checkGetGoogleFlightsWithGoogleConnectionServiceResponseWithEmptyTripOption() {
		Trips trips = createTrips(new TripOption[0]);
		GoogleResponse googleResponse = createGoogleResponse(trips);
		when(googleConnectionService.askGoogleForTheTrips(any())).thenReturn(googleResponse);
        FoundTripsWrapper result = googleService.getTrips(emptyInputData);
        assertThat(result.getFoundTrips(), is(Collections.emptyList()));
	}

	@Test
	public void checkGetGoogleFlightsWithValidGoogleConnectionServiceResponse() {
		TripOption[] tripOptions = new TripOption[]{};
		Trips trips = createTrips(tripOptions);
		GoogleResponse googleResponse = createGoogleResponse(trips);
		when(googleConnectionService.askGoogleForTheTrips(any())).thenReturn(googleResponse);
		FoundTripsWrapper result = googleService.getTrips(emptyInputData);
		List<FoundTrip> foundTrips = result.getFoundTrips();
		assertThat(foundTrips, equalTo(ImmutableList.of()));
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
}