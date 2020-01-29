package com.flightbuddy.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TriggerContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.flightbuddy.Application;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchTask;
import com.flightbuddy.schedule.search.ScheduledSearchTaskService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@TestPropertySource(properties = "schedule.enable=true")
public class ScheduleTriggerTest {
	
	@Autowired
	private ScheduleTrigger scheduleTrigger;
	
	@MockBean
	private ScheduledSearchTaskService scheduledSearchTaskService;
	
	@Test
	public void whenNoReadyTasksThenNextExecutionInFiveMins() {
		TriggerContext triggerContext = mock(TriggerContext.class);
		when(scheduledSearchTaskService.findNextReadyTask()).thenReturn(null);
		Date result = scheduleTrigger.nextExecutionTime(triggerContext);
		assertDatesAreEqual(result, LocalDateTime.now().plusMinutes(5));
	}
	
	@Test
	public void whenReadyTaskWithoutExecutionTimeThenNextExecutionInFiveMins() {
		TriggerContext triggerContext = mock(TriggerContext.class);
		ScheduledSearchTask readySearchTaskWithoutExecutionTime = new ScheduledSearchTask();
		when(scheduledSearchTaskService.findNextReadyTask()).thenReturn(readySearchTaskWithoutExecutionTime);
		Date result = scheduleTrigger.nextExecutionTime(triggerContext);
		assertDatesAreEqual(result, LocalDateTime.now().plusMinutes(5));
	}
	
	@Test
	public void whenReadyTaskThenNextExecution() {
		LocalDateTime nextExecution = LocalDateTime.now().plusHours(2);
		TriggerContext triggerContext = mock(TriggerContext.class);
		String searchTaskId = UUID.randomUUID().toString();
		ScheduledSearchTask readySearchTask = createScheduledSearchTask(nextExecution, searchTaskId);
		when(scheduledSearchTaskService.findNextReadyTask()).thenReturn(readySearchTask);
		Date result = scheduleTrigger.nextExecutionTime(triggerContext);
		assertDatesAreEqual(result, nextExecution);
		verify(scheduledSearchTaskService, times(1)).changeTaskStateToSet(eq(searchTaskId));
	}

	private ScheduledSearchTask createScheduledSearchTask(LocalDateTime nextExecution, String searchTaskId) {
		ScheduledSearchTask readySearchTask = new ScheduledSearchTask();
		readySearchTask.setId(searchTaskId);
		readySearchTask.setScheduledSearch(new ScheduledSearch());
		readySearchTask.setExecutionTime(nextExecution);
		return readySearchTask;
	}

	private LocalDateTime toLocalDate(Date localDateTime) {
		ZoneId zone = ZoneId.systemDefault();
		ZonedDateTime zonedDateTime = localDateTime.toInstant().atZone(zone);
		return LocalDateTime.from(zonedDateTime);
	}

	private void assertDatesAreEqual(Date date, LocalDateTime anotherDate) {
		LocalDateTime expectedDate = toLocalDate(date);
		assertThat(expectedDate.getYear(), equalTo(anotherDate.getYear()));
		assertThat(expectedDate.getMonth(), equalTo(anotherDate.getMonth()));
		assertThat(expectedDate.getDayOfMonth(), equalTo(anotherDate.getDayOfMonth()));
		assertThat(expectedDate.getHour(), equalTo(anotherDate.getHour()));
		assertThat(expectedDate.getMinute(), equalTo(anotherDate.getMinute()));
	}
}
