package com.flightbuddy.schedule;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.flightbuddy.Application;
import com.flightbuddy.google.GoogleService;
import com.flightbuddy.mails.MailService;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.FoundTripService;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchTask;
import com.flightbuddy.schedule.search.ScheduledSearchTask.ScheduledSearchState;
import com.flightbuddy.schedule.search.ScheduledSearchTaskService;
import com.flightbuddy.search.ImmutablePassengers;
import com.flightbuddy.search.ImmutableSearchInputData;
import com.flightbuddy.search.PassengersTO;
import com.flightbuddy.search.SearchDataConverter;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@PrepareForTest(SearchDataConverter.class)
@TestPropertySource(properties = "schedule.enable=true")
public class ScheduleRunnableTest {
	
	@Autowired ScheduleRunnable scheduleRunnable;
	
	@MockBean
	private GoogleService googleService;
    @MockBean
    private FoundTripService foundTripService;
    @MockBean
    private MailService mailService;
    @MockBean
    private ScheduledSearchTaskService scheduledSearchTaskService;
	
    private ImmutableSearchInputData emptyInputData;
    
    @Before
	public void setUp() {
		ImmutablePassengers immutablePassengers = new ImmutablePassengers(new PassengersTO());
		emptyInputData = new ImmutableSearchInputData(null, null, 0, 0, new LocalDate[]{}, false, immutablePassengers);
		mockStatic(SearchDataConverter.class);
		when(SearchDataConverter.convertToImmutable(any(ScheduledSearch.class))).thenReturn(emptyInputData);
	}
    
	@Test
	public void runWithoutSetScheduledSearchTask() {
		when(scheduledSearchTaskService.findNextSetTask()).thenReturn(null);
		scheduleRunnable.run();
		verify(googleService, times(0)).getTrips(any());
		verify(foundTripService, times(0)).saveFoundTrips(any());
		verify(mailService, times(0)).sendTrips(any());
	}
	
	@Test
	public void runWithoutSetScheduledSearchTaskWithoutScheduledSearch() {
		ScheduledSearchTask scheduledSearchTask = new ScheduledSearchTask();
		when(scheduledSearchTaskService.findNextSetTask()).thenReturn(scheduledSearchTask);
		scheduleRunnable.run();
		verify(googleService, times(0)).getTrips(any());
		verify(foundTripService, times(0)).saveFoundTrips(any());
		verify(mailService, times(0)).sendTrips(any());
	}

	@Test
	public void runWithSetScheduledSearchTaskWithoutFoundTrips() {
		String scheduledSearchTaskId = UUID.randomUUID().toString();
		ScheduledSearchTask scheduledSearchTask = createScheduledSearchTask(new ScheduledSearch(), scheduledSearchTaskId);
		when(scheduledSearchTaskService.findNextSetTask()).thenReturn(scheduledSearchTask);
		List<FoundTrip> emptyTrips = new ArrayList<>(0);
		when(googleService.getTrips(any())).thenReturn(emptyTrips);
		scheduleRunnable.run();
		verify(googleService, times(1)).getTrips(any());
		verify(foundTripService, times(0)).saveFoundTrips(any());
		verify(mailService, times(0)).sendTrips(any());
		verify(scheduledSearchTaskService, times(1)).changeTaskStateToStarted(eq(scheduledSearchTaskId));
		verify(scheduledSearchTaskService, times(1)).changeTaskStateToFinished(eq(scheduledSearchTaskId));
	}
	
	@Test()
	public void runWithSetScheduledSearchTaskWithError() {
		String scheduledSearchTaskId = UUID.randomUUID().toString();
		ScheduledSearchTask scheduledSearchTask = createScheduledSearchTask(new ScheduledSearch(), scheduledSearchTaskId);
		when(scheduledSearchTaskService.findNextSetTask()).thenReturn(scheduledSearchTask);
		when(googleService.getTrips(eq(emptyInputData))).thenThrow(new RuntimeException());
		try {
			scheduleRunnable.run();
		} catch(Throwable e) {}
		verify(googleService, times(1)).getTrips(any());
		verify(foundTripService, times(0)).saveFoundTrips(any());
		verify(mailService, times(0)).sendTrips(any());
		verify(scheduledSearchTaskService, times(1)).changeTaskStateToStarted(eq(scheduledSearchTaskId));
		verify(scheduledSearchTaskService, times(0)).changeTaskStateToFinished(eq(scheduledSearchTaskId));
	}
	
	@Test
	public void runWithSetScheduledSearchTaskWithFoundTrips() {
		String scheduledSearchTaskId = UUID.randomUUID().toString();
		ScheduledSearchTask scheduledSearchTask = createScheduledSearchTask(new ScheduledSearch(), scheduledSearchTaskId);
		when(scheduledSearchTaskService.findNextSetTask()).thenReturn(scheduledSearchTask);
		List<FoundTrip> trips = createFoundTripsWithOneTrip();
		when(googleService.getTrips(eq(emptyInputData))).thenReturn(trips);
		scheduleRunnable.run();
		verify(googleService, times(1)).getTrips(any());
		verify(foundTripService, times(1)).saveFoundTrips(any());
		verify(mailService, times(1)).sendTrips(any());
		verify(scheduledSearchTaskService, times(1)).changeTaskStateToStarted(eq(scheduledSearchTaskId));
		verify(scheduledSearchTaskService, times(1)).changeTaskStateToFinished(eq(scheduledSearchTaskId));
	}

	private ScheduledSearchTask createScheduledSearchTask(ScheduledSearch scheduledSearch, String id) {
		ScheduledSearchTask scheduledSearchTask = new ScheduledSearchTask();
		scheduledSearchTask.setState(ScheduledSearchState.SET);
		scheduledSearchTask.setId(id);
		scheduledSearchTask.setScheduledSearch(scheduledSearch);
		scheduledSearchTask.setExecutionTime(LocalDateTime.now());
		return scheduledSearchTask;
	}

	private List<FoundTrip> createFoundTripsWithOneTrip() {
		List<FoundTrip> trips = new ArrayList<>(1);
		FoundTrip trip = new FoundTrip();
		trips.add(trip);
		return trips;
	}
}
