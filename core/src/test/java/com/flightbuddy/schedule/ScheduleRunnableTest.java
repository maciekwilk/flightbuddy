package com.flightbuddy.schedule;

import com.flightbuddy.Application;
import com.flightbuddy.google.GoogleService;
import com.flightbuddy.mails.MailService;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.FoundTripService;
import com.flightbuddy.results.FoundTripsWrapper;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchTask;
import com.flightbuddy.schedule.search.ScheduledSearchTask.ScheduledSearchState;
import com.flightbuddy.schedule.search.ScheduledSearchTaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.flightbuddy.search.SearchTestUtil.createScheduledSearchWithThreeDates;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@TestPropertySource(properties = "schedule.enable=true")
public class ScheduleRunnableTest {

	@Autowired
	private ScheduleRunnable scheduleRunnable;

	@MockBean
	private GoogleService googleService;
    @MockBean
    private FoundTripService foundTripService;
    @MockBean
    private MailService mailService;
    @MockBean
    private ScheduledSearchTaskService scheduledSearchTaskService;

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
        ScheduledSearch scheduledSearch = createScheduledSearchWithThreeDates();
		ScheduledSearchTask scheduledSearchTask = createScheduledSearchTask(scheduledSearch, scheduledSearchTaskId);
		when(scheduledSearchTaskService.findNextSetTask()).thenReturn(scheduledSearchTask);
		when(googleService.getTrips(any())).thenReturn(new FoundTripsWrapper(""));
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
        ScheduledSearch scheduledSearch = createScheduledSearchWithThreeDates();
        ScheduledSearchTask scheduledSearchTask = createScheduledSearchTask(scheduledSearch, scheduledSearchTaskId);
		when(scheduledSearchTaskService.findNextSetTask()).thenReturn(scheduledSearchTask);
		when(googleService.getTrips(any())).thenThrow(new RuntimeException());
		try {
			scheduleRunnable.run();
		} catch(Throwable ignored) {}
		verify(googleService, times(1)).getTrips(any());
		verify(foundTripService, times(0)).saveFoundTrips(any());
		verify(mailService, times(0)).sendTrips(any());
		verify(scheduledSearchTaskService, times(1)).changeTaskStateToStarted(eq(scheduledSearchTaskId));
		verify(scheduledSearchTaskService, times(0)).changeTaskStateToFinished(eq(scheduledSearchTaskId));
	}

	@Test
	public void runWithSetScheduledSearchTaskWithFoundTrips() {
		String scheduledSearchTaskId = UUID.randomUUID().toString();
        ScheduledSearch scheduledSearch = createScheduledSearchWithThreeDates();
		ScheduledSearchTask scheduledSearchTask = createScheduledSearchTask(scheduledSearch, scheduledSearchTaskId);
		when(scheduledSearchTaskService.findNextSetTask()).thenReturn(scheduledSearchTask);
		List<FoundTrip> trips = createFoundTripsWithOneTrip();
		when(googleService.getTrips(any())).thenReturn(new FoundTripsWrapper(trips));
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
