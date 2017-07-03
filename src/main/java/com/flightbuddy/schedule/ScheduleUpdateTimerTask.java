package com.flightbuddy.schedule;

public class ScheduleUpdateTimerTask {

	private static final int TWENTY_FOUR_HOURS_IN_SECONDS = 24*60*60;


	long singleExecutionInterval = TWENTY_FOUR_HOURS_IN_SECONDS / maxAmountOfRequests;
	long secondsToNextExecution = searchInputDataSize * singleExecutionInterval;
}
