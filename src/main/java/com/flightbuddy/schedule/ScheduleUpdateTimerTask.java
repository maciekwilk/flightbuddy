package com.flightbuddy.schedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.flightbuddy.google.GoogleService;
import com.flightbuddy.schedule.search.ScheduledSearch;
import com.flightbuddy.schedule.search.ScheduledSearchService;
import com.flightbuddy.schedule.search.ScheduledSearchTask;
import com.flightbuddy.schedule.search.ScheduledSearchTaskService;
import com.flightbuddy.schedule.search.ScheduledSearchTask.RequestService;
import com.flightbuddy.schedule.search.ScheduledSearchTask.ScheduledSearchState;

@Component
public class ScheduleUpdateTimerTask {

	@Autowired GoogleService googleService;
	@Autowired ScheduledSearchService scheduledSearchService;
	@Autowired ScheduledSearchTaskService scheduledSearchTaskService;
	
	private static final int TWENTY_FOUR_HOURS_IN_SECONDS = 24*60*60;
	
	@Scheduled(cron = "0 0 18 * * *")
	public void run() {
		List<ScheduledSearch> scheduledSearches = scheduledSearchService.getAllScheduledSearches();
		if (!scheduledSearches.isEmpty()) {
			List<LocalDateTime> executionTimes = calculateExecutionTimes(scheduledSearches.size());
			for (ScheduledSearch scheduledSearch : scheduledSearches) {
				for (LocalDateTime executionTime : executionTimes) {
					ScheduledSearchTask scheduledSearchTask = createReadyScheduledSearchTask(scheduledSearch, executionTime);
					scheduledSearchTaskService.create(scheduledSearchTask);
				}
			}
		}
	}

	protected List<LocalDateTime> calculateExecutionTimes(int scheduledSearchAmount) {
		int maxAmountOfRequests = googleService.getMaxAmountOfRequests();
		int requestsPerScheduledSearch = maxAmountOfRequests / scheduledSearchAmount;
		long requestInterval = TWENTY_FOUR_HOURS_IN_SECONDS / requestsPerScheduledSearch;
		return calculateExecutionTimes(requestsPerScheduledSearch, requestInterval);
	}

	private List<LocalDateTime> calculateExecutionTimes(int requestsPerScheduledSearch, long requestInterval) {
		List<LocalDateTime> executionTimes = new ArrayList<>(requestsPerScheduledSearch);
		LocalDateTime midnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
		LocalDateTime nextTime = midnight;
		for (int i = 0; i < requestsPerScheduledSearch; i++) {
			nextTime = nextTime.plusSeconds(requestInterval);
			executionTimes.add(nextTime);
		}
		return executionTimes;
	}

	protected ScheduledSearchTask createReadyScheduledSearchTask(ScheduledSearch scheduledSearch, LocalDateTime executionTime) {
		ScheduledSearchTask scheduledSearchTask = new ScheduledSearchTask();
		scheduledSearchTask.setScheduledSearch(scheduledSearch);
		scheduledSearchTask.setState(ScheduledSearchState.READY);
		scheduledSearchTask.setService(RequestService.GOOGLE);
		scheduledSearchTask.setExecutionTime(executionTime);
		return scheduledSearchTask;
	}
}
