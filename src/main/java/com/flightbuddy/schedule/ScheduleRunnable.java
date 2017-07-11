package com.flightbuddy.schedule;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.flightbuddy.SearchInputData;
import com.flightbuddy.google.GoogleService;
import com.flightbuddy.mails.MailService;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.FoundTripService;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchTask;
import com.flightbuddy.schedule.search.ScheduledSearchTaskService;
import com.flightbuddy.user.AuthenticationService;

@Component
public class ScheduleRunnable implements Runnable {
	
	Logger log = Logger.getLogger(ScheduleRunnable.class);

	@Autowired ScheduledSearchTaskService scheduledSearchTaskService;
	@Autowired GoogleService googleService;
    @Autowired FoundTripService foundTripService;
    @Autowired MailService mailService;
	@Autowired AuthenticationService authenticationService;
	@Value("${schedule.enable}")
	private boolean scheduleEnabled;
	
	@Override
	public void run() {
		if(scheduleEnabled) {
			authenticationService.loginAsSystem();
			log.info("running scheduled search started");
			try {
				runNextSetTask();
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
			log.info("running scheduled search ended");
			authenticationService.logoutUsers();
		}
	}

	private void runNextSetTask() {
		ScheduledSearchTask scheduledSearchTask = scheduledSearchTaskService.findNextSetTask();
		if (scheduledSearchTask == null || scheduledSearchTask.getScheduledSearch() == null) {
			log.info("no scheduled search task found with status SET");
			return;
		}
		log(scheduledSearchTask, scheduledSearchTask.getScheduledSearch(), "scheduled search task found");
		waitForExecutionTime(scheduledSearchTask);
		changeStateToStarted(scheduledSearchTask);
		List<FoundTrip> foundTrips = performSearch(scheduledSearchTask);
		handleFoundTrips(scheduledSearchTask, foundTrips);
		changeStateToFinished(scheduledSearchTask);
	}

	private void waitForExecutionTime(ScheduledSearchTask scheduledSearchTask) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime executionTime = scheduledSearchTask.getExecutionTime();
		if (now.isBefore(executionTime)) {
			long timeToWait = toMillis(executionTime) - toMillis(now);
			try {
				Thread.sleep(timeToWait);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
		
	}

	private void changeStateToStarted(ScheduledSearchTask scheduledSearchTask) {
		ScheduledSearch scheduledSearch = scheduledSearchTask.getScheduledSearch();
		scheduledSearchTaskService.changeTaskStateToStarted(scheduledSearchTask.getId());
		log(scheduledSearchTask, scheduledSearch, "scheduled search task changed state to STARTED");
	}

	private List<FoundTrip> performSearch(ScheduledSearchTask scheduledSearchTask) {
		ScheduledSearch scheduledSearch = scheduledSearchTask.getScheduledSearch();
		SearchInputData searchInputData = SearchInputDataConverter.convert(scheduledSearch);
		List<FoundTrip> foundTrips = googleService.getTrips(searchInputData);
		return foundTrips;
	}

	private void handleFoundTrips(ScheduledSearchTask scheduledSearchTask, List<FoundTrip> foundTrips) {
		if (!foundTrips.isEmpty()) {
	    	foundTripService.saveFoundTrips(foundTrips);
	    	mailService.sendTrips(foundTrips);
		} else {
			logFailedSearch(scheduledSearchTask);
		}
	}

	private void logFailedSearch(ScheduledSearchTask setSearchTask) {
		ScheduledSearch scheduledSearch = setSearchTask.getScheduledSearch();
		log.error("no trips found during search, id = " + setSearchTask.getId() + ", service = " 
					+ setSearchTask.getService() + ", search = " + scheduledSearch.getId());
	}

	private void changeStateToFinished(ScheduledSearchTask scheduledSearchTask) {
		ScheduledSearch scheduledSearch = scheduledSearchTask.getScheduledSearch();
		scheduledSearchTaskService.changeTaskStateToFinished(scheduledSearchTask.getId());
		log(scheduledSearchTask, scheduledSearch, "scheduled search task changed state to FINISHED");
	}

	private void log(ScheduledSearchTask scheduledSearchTask, ScheduledSearch scheduledSearch, String message) {
		log.info(message + ", id = " + scheduledSearchTask.getId() + ", service = " 
				+ scheduledSearchTask.getService() + ", search = " + scheduledSearch.getId());
	}

	private long toMillis(LocalDateTime localDateTime) {
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = localDateTime.atZone(zone).toInstant();
		return Date.from(instant).getTime();
	}
}