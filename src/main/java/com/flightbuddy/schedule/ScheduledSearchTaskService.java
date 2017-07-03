package com.flightbuddy.schedule;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.schedule.ScheduledSearchTask.ScheduledSearchState;

@Transactional
@Service
public class ScheduledSearchTaskService {
	
	@Autowired ScheduledSearchTaskDao scheduledSearchTaskDao;

	public ScheduledSearchTask findNextReadyTask() {
		return scheduledSearchTaskDao.findNextReadyTask();
	}

	public void changeTaskStateToSet(String id) {
		ScheduledSearchTask scheduledSearchTask = scheduledSearchTaskDao.findById(UUID.fromString(id));
		scheduledSearchTask.setState(ScheduledSearchState.SET);
		scheduledSearchTaskDao.merge(scheduledSearchTask);
	}
	
}
