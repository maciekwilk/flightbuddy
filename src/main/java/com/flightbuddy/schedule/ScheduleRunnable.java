package com.flightbuddy.schedule;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.flightbuddy.SearchInputData;
import com.flightbuddy.google.GoogleService;
import com.flightbuddy.mails.MailService;
import com.flightbuddy.results.FoundTrip;
import com.flightbuddy.results.FoundTripService;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchTask;
import com.flightbuddy.schedule.search.ScheduledSearchTaskService;

@Component
public class ScheduleRunnable implements Runnable {
	
	Logger log = Logger.getLogger(ScheduleRunnable.class);

	@Autowired ScheduledSearchTaskService scheduledSearchTaskService;
	@Autowired GoogleService googleService;
    @Autowired FoundTripService foundTripService;
    @Autowired MailService mailService;
    
	@Override
	public void run() {
		log.info("running scheduled search started");
		ScheduledSearchTask scheduledSearchTask = scheduledSearchTaskService.findNextSetTask();
		if (scheduledSearchTask == null || scheduledSearchTask.getScheduledSearch() == null) {
			log.error("no scheduled search task found with status SET");
			return;
		}
		log(scheduledSearchTask, scheduledSearchTask.getScheduledSearch(), "scheduled search task found");
		changeStateToStarted(scheduledSearchTask);
		List<FoundTrip> foundTrips = performSearch(scheduledSearchTask);
		handleFoundTrips(scheduledSearchTask, foundTrips);
		changeStateToFinished(scheduledSearchTask);
		log.info("running scheduled search ended");
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
}