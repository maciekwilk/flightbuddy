package com.flightbuddy.schedule;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchTask;
import com.flightbuddy.schedule.search.ScheduledSearchTaskService;
import com.flightbuddy.user.authentication.AuthenticationService;

@Component
public class ScheduleTrigger implements Trigger {
	
	Logger log = LoggerFactory.getLogger(ScheduleTrigger.class);

	@Autowired ScheduledSearchTaskService scheduledSearchTaskService;
	@Autowired AuthenticationService authenticationService;
	@Value("${schedule.enable}")
	private boolean scheduleEnabled;
	
	@Override 
	@Transactional
	public Date nextExecutionTime(TriggerContext triggerContext) {
		Date nextExecutionTime = toDate(LocalDateTime.now().plusHours(24));
		if (scheduleEnabled) {
			authenticationService.loginAsSystem();
			log.info("setting next execution time started");
			try {
				nextExecutionTime = getNextExecutionTime();
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				nextExecutionTime = getFiveMinutesFromNow();
			}
			log.info("setting next execution time finished, next execution time = " + nextExecutionTime);
			authenticationService.logoutUsers();
		}
		return nextExecutionTime;
	}

	private Date getNextExecutionTime() {
		ScheduledSearchTask readySearchTask = scheduledSearchTaskService.findNextReadyTask();
		if (readySearchTask != null && readySearchTask.getExecutionTime() != null) {
			return getNextExecutionTime(readySearchTask);
		} else {
			log.info("no scheduled search task found with status READY");
			return getFiveMinutesFromNow();
		}
	}

	private Date getNextExecutionTime(ScheduledSearchTask readySearchTask) {
		ScheduledSearch scheduledSearch = readySearchTask.getScheduledSearch();
		log.info("next READY search task found, id = " + readySearchTask.getId() + ", service = " 
					+ readySearchTask.getService() + ", search = " + scheduledSearch.getId());
		changeStateToSet(readySearchTask);
		LocalDateTime executionTime = readySearchTask.getExecutionTime();
		return toDate(executionTime);
	}

	private void changeStateToSet(ScheduledSearchTask readySearchTask) {
		scheduledSearchTaskService.changeTaskStateToSet(readySearchTask.getId());
		log.info("READY search task changed state to SET, id = " + readySearchTask.getId());
	}

	private Date getFiveMinutesFromNow() {
		LocalDateTime nowPlusFiveMinutes = LocalDateTime.now().plusMinutes(5);
		return toDate(nowPlusFiveMinutes);
	}

	private Date toDate(LocalDateTime localDateTime) {
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = localDateTime.atZone(zone).toInstant();
		return Date.from(instant);
	}
}
