package com.flightbuddy.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TriggerContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.flightbuddy.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ScheduleTriggerTest {
	
	@Autowired ScheduleTrigger scheduleTrigger;
	
	@MockBean ScheduledSearchTaskService scheduledSearchTaskService;
	
	@Test
	public void whenNoReadyTasksThenNoNextExecution() {
		LocalDateTime lastExecution = LocalDateTime.now();
		TriggerContext triggerContext = mock(TriggerContext.class);
		when(triggerContext.lastActualExecutionTime()).thenReturn(toDate(lastExecution));
		when(scheduledSearchTaskService.findNextReadyTask()).thenReturn(null);
		Date result = scheduleTrigger.nextExecutionTime(triggerContext);
		assertThat(result, equalTo(toDate(lastExecution)));
	}
	
	@Test
	public void whenReadyTaskWithoutExecutionTimeThenNoNextExecution() {
		LocalDateTime lastExecution = LocalDateTime.now();
		TriggerContext triggerContext = mock(TriggerContext.class);
		when(triggerContext.lastActualExecutionTime()).thenReturn(toDate(lastExecution));
		ScheduledSearchTask readySearchTaskWithoutExecutionTime = new ScheduledSearchTask();
		when(scheduledSearchTaskService.findNextReadyTask()).thenReturn(readySearchTaskWithoutExecutionTime);
		Date result = scheduleTrigger.nextExecutionTime(triggerContext);
		assertThat(result, equalTo(toDate(lastExecution)));
	}
	
	@Test
	public void whenReadyTaskThenNextExecution() {
		LocalDateTime lastExecution = LocalDateTime.now();
		LocalDateTime nextExecution = lastExecution.plusHours(2);
		TriggerContext triggerContext = mock(TriggerContext.class);
		when(triggerContext.lastActualExecutionTime()).thenReturn(toDate(lastExecution));
		String searchTaskId = UUID.randomUUID().toString();
		ScheduledSearchTask readySearchTask = createScheduledSearchTask(nextExecution, searchTaskId);
		when(scheduledSearchTaskService.findNextReadyTask()).thenReturn(readySearchTask);
		Date result = scheduleTrigger.nextExecutionTime(triggerContext);
		assertThat(result, equalTo(toDate(nextExecution)));
		verify(scheduledSearchTaskService, times(1)).changeTaskStateToSet(eq(searchTaskId));
	}

	private ScheduledSearchTask createScheduledSearchTask(LocalDateTime nextExecution, String searchTaskId) {
		ScheduledSearchTask readySearchTask = new ScheduledSearchTask();
		readySearchTask.setId(searchTaskId);
		readySearchTask.setExecutionTime(nextExecution);
		return readySearchTask;
	}

	private Date toDate(LocalDateTime localDateTime) {
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = localDateTime.atZone(zone).toInstant();
		return Date.from(instant);
	}
}
