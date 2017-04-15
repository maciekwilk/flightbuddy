package com.flightbuddy.schedule;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.TriggerContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.flightbuddy.google.GoogleService;
import com.flightbuddy.google.schedule.GoogleTrigger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoogleTriggerTest {
	
	private static final int TWENTY_FOUR_HOURS_IN_SECONDS = 24*60*60;

	@Autowired GoogleService googleService;
	
	private GoogleTrigger googleTrigger;
	private int maxAmountOfRequests;
	
	@Before
	public void setUp() {
		maxAmountOfRequests = googleService.getMaxAmountOfRequests();
	}
	
	@Test
	public void noSearchInputData() {
		googleTrigger = new GoogleTrigger(0, maxAmountOfRequests);
		LocalDateTime lastExecution = LocalDateTime.now();
		LocalDateTime nextExecution = lastExecution;
		TriggerContext triggerContext = mock(TriggerContext.class);
		when(triggerContext.lastActualExecutionTime()).thenReturn(toDate(lastExecution));
		Date result = googleTrigger.nextExecutionTime(triggerContext);
		assertThat(result, equalTo(toDate(nextExecution)));
	}
	
	@Test
	public void oneSearchInputData() {
		googleTrigger = new GoogleTrigger(1, maxAmountOfRequests);
		LocalDateTime lastExecution = LocalDateTime.now();
		long secondsToNextExecution = TWENTY_FOUR_HOURS_IN_SECONDS / maxAmountOfRequests;
		LocalDateTime nextExecution = lastExecution.plusSeconds(secondsToNextExecution);
		TriggerContext triggerContext = mock(TriggerContext.class);
		when(triggerContext.lastActualExecutionTime()).thenReturn(toDate(lastExecution));
		Date result = googleTrigger.nextExecutionTime(triggerContext);
		assertThat(result, equalTo(toDate(nextExecution)));
	}
	
	@Test
	public void manySearchInputData() {
		googleTrigger = new GoogleTrigger(15, maxAmountOfRequests);
		LocalDateTime lastExecution = LocalDateTime.now();
		int singleExecutionInterval = TWENTY_FOUR_HOURS_IN_SECONDS / maxAmountOfRequests;
		long secondsToNextExecution = 15 * singleExecutionInterval;
		LocalDateTime nextExecution = lastExecution.plusSeconds(secondsToNextExecution);
		TriggerContext triggerContext = mock(TriggerContext.class);
		when(triggerContext.lastActualExecutionTime()).thenReturn(toDate(lastExecution));
		Date result = googleTrigger.nextExecutionTime(triggerContext);
		assertThat(result, equalTo(toDate(nextExecution)));
	}

	private Date toDate(LocalDateTime localDateTime) {
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = localDateTime.atZone(zone).toInstant();
		return Date.from(instant);
	}
}
