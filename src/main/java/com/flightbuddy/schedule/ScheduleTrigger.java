package com.flightbuddy.schedule;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTrigger implements Trigger {

	@Autowired ScheduledSearchTaskService scheduledSearchServiceTask;
	
	@Override 
	public Date nextExecutionTime(TriggerContext triggerContext) {
        Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
        if (lastActualExecutionTime == null) {
        	lastActualExecutionTime = new Date();
        }
		Date nextExecutionTime = getNextExecutionTime(); 
		return nextExecutionTime != null ? nextExecutionTime : lastActualExecutionTime;
	}

	private Date getNextExecutionTime() {
		ScheduledSearchTask readySearchTask = scheduledSearchServiceTask.findNextReadyTask();
		if (readySearchTask != null && readySearchTask.getExecutionTime() != null) {
			scheduledSearchServiceTask.changeTaskStateToSet(readySearchTask.getId());
			LocalDateTime executionTime = readySearchTask.getExecutionTime();
			return toDate(executionTime);
		}
		return null;
	}

	private Date toDate(LocalDateTime localDateTime) {
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = localDateTime.atZone(zone).toInstant();
		return Date.from(instant);
	}

}
