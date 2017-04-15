package com.flightbuddy.google.schedule;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

public class GoogleTrigger implements Trigger {

	private static final int TWENTY_FOUR_HOURS_IN_SECONDS = 24*60*60;
	
	private final int maxAmountOfRequests;
	private final int searchInputDataSize;
	
	public GoogleTrigger(int searchInputDataSize, int maxAmountOfRequests) {
		this.searchInputDataSize = searchInputDataSize;
		this.maxAmountOfRequests = maxAmountOfRequests;
	}

	@Override 
	public Date nextExecutionTime(TriggerContext triggerContext) {
        Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
        if (lastActualExecutionTime == null) {
        	lastActualExecutionTime = new Date();
        }
		if (searchInputDataSize == 0) {
			return lastActualExecutionTime;
		}
		return getNextExecutionTime(lastActualExecutionTime); 
	}

	private Date getNextExecutionTime(Date lastActualExecutionTime) {
		LocalDateTime nextExecutionTime = toLocalDateTime(lastActualExecutionTime);
		long singleExecutionInterval = TWENTY_FOUR_HOURS_IN_SECONDS / maxAmountOfRequests;
		long secondsToNextExecution = searchInputDataSize * singleExecutionInterval;
		return toDate(nextExecutionTime.plusSeconds(secondsToNextExecution));
	}
	
	private LocalDateTime toLocalDateTime(Date date) {
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = date.toInstant();
		return LocalDateTime.ofInstant(instant, zone);
	}

	private Date toDate(LocalDateTime localDateTime) {
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = localDateTime.atZone(zone).toInstant();
		return Date.from(instant);
	}

}
