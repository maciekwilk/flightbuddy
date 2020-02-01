package com.flightbuddy.schedule;

import com.flightbuddy.Application;
import com.flightbuddy.google.GoogleService;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchService;
import com.flightbuddy.schedule.search.ScheduledSearchTask;
import com.flightbuddy.schedule.search.ScheduledSearchTask.RequestService;
import com.flightbuddy.schedule.search.ScheduledSearchTask.ScheduledSearchState;
import com.flightbuddy.schedule.search.ScheduledSearchTaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@TestPropertySource(properties = "schedule.enable=true")
public class ScheduleUpdateTimerTaskTest {

	@Autowired
	private ScheduleUpdateTimerTask task;

	@MockBean
	private ScheduledSearchService scheduledSearchService;
	@MockBean
	private ScheduledSearchTaskService scheduledSearchTaskService;
	@MockBean
	private GoogleService googleService;

	@Test
	public void noScheduledSearch() {
		when(scheduledSearchService.getAllScheduledSearches()).thenReturn(Collections.emptyList());
		when(googleService.getMaxAmountOfRequests()).thenReturn(10);
		task.run();
		verify(scheduledSearchTaskService, times(0)).create(any());
	}

	@Test
	public void withScheduledSearch() {
		ScheduledSearch scheduledSearch = new ScheduledSearch();
		when(scheduledSearchService.getAllScheduledSearches()).thenReturn(Collections.singletonList(scheduledSearch));
		when(googleService.getMaxAmountOfRequests()).thenReturn(10);
		task.run();
		verify(scheduledSearchTaskService, times(10)).create(any());
	}

	@Test
	public void withManyScheduledSearches() {
		ScheduledSearch[] scheduledSearchesArray = new ScheduledSearch[] {new ScheduledSearch(), new ScheduledSearch(), new ScheduledSearch()};
		List<ScheduledSearch> scheduledSearches = Arrays.asList(scheduledSearchesArray);
		when(scheduledSearchService.getAllScheduledSearches()).thenReturn(scheduledSearches);
		when(googleService.getMaxAmountOfRequests()).thenReturn(10);
		task.run();
		verify(scheduledSearchTaskService, times(9)).create(any());
	}

	@Test
	public void createReadyScheduledSearchTask() {
		ScheduledSearch scheduledSearch = new ScheduledSearch();
		LocalDateTime executionTime = LocalDateTime.now();
		ScheduledSearchTask result = task.createReadyScheduledSearchTask(scheduledSearch, executionTime);
		assertThat(result.getExecutionTime(), equalTo(executionTime));
		assertThat(result.getScheduledSearch(), equalTo(scheduledSearch));
		assertThat(result.getService(), equalTo(RequestService.GOOGLE));
		assertThat(result.getState(), equalTo(ScheduledSearchState.READY));
	}

	@Test
	public void calculateExecutionTimes() {
		List<LocalDateTime> executionTimes = createExecutionTimesList();
		int scheduledSearchAmount = 5;
		when(googleService.getMaxAmountOfRequests()).thenReturn(60);
		List<LocalDateTime> result = task.calculateExecutionTimes(scheduledSearchAmount);
		assertThat(result.size(), equalTo(12));
		assertThat(result, equalTo(executionTimes));
	}

	private List<LocalDateTime> createExecutionTimesList() {
		List<LocalDateTime> executionTimes = new ArrayList<>(12);
		LocalDateTime firstTime = LocalDateTime.now().withHour(2).withMinute(0).withSecond(0).withNano(0);
		executionTimes.add(firstTime);
		LocalDateTime nextTime = firstTime;
		for (int i = 0; i < 11; i++) {
			nextTime = nextTime.plusHours(2);
			executionTimes.add(nextTime);
		}
		return executionTimes;
	}
}
