package com.flightbuddy.schedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.flightbuddy.google.GoogleService;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchService;
import com.flightbuddy.schedule.search.ScheduledSearchTask;
import com.flightbuddy.schedule.search.ScheduledSearchTask.RequestService;
import com.flightbuddy.schedule.search.ScheduledSearchTask.ScheduledSearchState;
import com.flightbuddy.user.authentication.AuthenticationService;
import com.flightbuddy.schedule.search.ScheduledSearchTaskService;

@Component
class ScheduleUpdateTimerTask {
	
	private final static Logger log = LoggerFactory.getLogger(ScheduleUpdateTimerTask.class);

	@Autowired
	private GoogleService googleService;
	@Autowired
	private ScheduledSearchService scheduledSearchService;
	@Autowired
	private ScheduledSearchTaskService scheduledSearchTaskService;
	@Autowired
	private AuthenticationService authenticationService;
	@Value("${schedule.enable}")
	private boolean scheduleEnabled;
	
	private static final int TWENTY_FOUR_HOURS_IN_SECONDS = 24*60*60;
	
	@Scheduled(cron = "0 0 0 * * *")
	public void run() {
		if (scheduleEnabled) {
			authenticationService.loginAsSystem();
			log.info("updating schedule started");
			List<ScheduledSearch> scheduledSearches = scheduledSearchService.getAllScheduledSearches();
			if (!scheduledSearches.isEmpty()) {
				createScheduleSearchTasks(scheduledSearches);
			} else {
				log.info("no scheduled searches found");
			}
			log.info("updating schedule ended, created tasks for " + scheduledSearches.size() + " scheduled searches");
			authenticationService.logoutUsers();
		}
	}

	private void createScheduleSearchTasks(List<ScheduledSearch> scheduledSearches) {
		List<LocalDateTime> executionTimes = calculateExecutionTimes(scheduledSearches.size());
		log.info("creating " + executionTimes.size() + " tasks for every scheduled search");
		for (ScheduledSearch scheduledSearch : scheduledSearches) {
			for (LocalDateTime executionTime : executionTimes) {
				ScheduledSearchTask scheduledSearchTask = createReadyScheduledSearchTask(scheduledSearch, executionTime);
				scheduledSearchTaskService.create(scheduledSearchTask);
			}
		}
	}

	List<LocalDateTime> calculateExecutionTimes(int scheduledSearchAmount) {
		int maxAmountOfRequests = googleService.getMaxAmountOfRequests();
		int requestsPerScheduledSearch = maxAmountOfRequests / scheduledSearchAmount;
		long requestInterval = TWENTY_FOUR_HOURS_IN_SECONDS / requestsPerScheduledSearch;
		return calculateExecutionTimes(requestsPerScheduledSearch, requestInterval);
	}

	private List<LocalDateTime> calculateExecutionTimes(int requestsPerScheduledSearch, long requestInterval) {
		List<LocalDateTime> executionTimes = new ArrayList<>(requestsPerScheduledSearch);
		LocalDateTime nextTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
		for (int i = 0; i < requestsPerScheduledSearch; i++) {
			nextTime = nextTime.plusSeconds(requestInterval);
			executionTimes.add(nextTime);
		}
		return executionTimes;
	}

	ScheduledSearchTask createReadyScheduledSearchTask(ScheduledSearch scheduledSearch, LocalDateTime executionTime) {
		ScheduledSearchTask scheduledSearchTask = new ScheduledSearchTask();
		scheduledSearchTask.setScheduledSearch(scheduledSearch);
		scheduledSearchTask.setState(ScheduledSearchState.READY);
		scheduledSearchTask.setService(RequestService.GOOGLE);
		scheduledSearchTask.setExecutionTime(executionTime);
		return scheduledSearchTask;
	}
}
