package com.flightbuddy.schedule;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import com.flightbuddy.results.FoundTripsWrapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.flightbuddy.google.GoogleService;
import com.flightbuddy.mails.MailService;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.FoundTripService;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchTask;
import com.flightbuddy.schedule.search.ScheduledSearchTaskService;
import com.flightbuddy.search.ImmutableSearchInputData;
import com.flightbuddy.search.SearchDataConverter;
import com.flightbuddy.user.authentication.AuthenticationService;

@Component
public class ScheduleRunnable implements Runnable {
	
	private static final Logger log = Logger.getLogger(ScheduleRunnable.class);

	@Autowired
	private ScheduledSearchTaskService scheduledSearchTaskService;
	@Autowired
	private GoogleService googleService;
    @Autowired
	private FoundTripService foundTripService;
    @Autowired
	private MailService mailService;
	@Autowired
	private AuthenticationService authenticationService;
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
		FoundTripsWrapper foundTripsWrapper = performSearch(scheduledSearchTask);
		handleFoundTrips(scheduledSearchTask, foundTripsWrapper);
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

	private FoundTripsWrapper performSearch(ScheduledSearchTask scheduledSearchTask) {
		ScheduledSearch scheduledSearch = scheduledSearchTask.getScheduledSearch();
		ImmutableSearchInputData searchInputData = SearchDataConverter.convertToImmutable(scheduledSearch);
		return googleService.getTrips(searchInputData);
	}

	private void handleFoundTrips(ScheduledSearchTask scheduledSearchTask, FoundTripsWrapper foundTripsWrapper) {
		List<FoundTrip> foundTrips = foundTripsWrapper.getFoundTrips();
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