package com.flightbuddy.schedule.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.schedule.search.ScheduledSearchTask.ScheduledSearchState;

@Transactional
@Service
@PreAuthorize("hasRole('SYSTEM')")
public class ScheduledSearchTaskService {
	
	@Autowired ScheduledSearchTaskDao scheduledSearchTaskDao;
	
	public void create(ScheduledSearchTask scheduledSearchTask) {
		scheduledSearchTaskDao.persist(scheduledSearchTask);
	}

	public ScheduledSearchTask findNextReadyTask() {
		return scheduledSearchTaskDao.findNextReadyTask();
	}

	public void changeTaskStateToSet(String id) {
		ScheduledSearchTask scheduledSearchTask = scheduledSearchTaskDao.findById(id);
		scheduledSearchTask.setState(ScheduledSearchState.SET);
		scheduledSearchTaskDao.merge(scheduledSearchTask);
	}

	public ScheduledSearchTask findNextSetTask() {
		return scheduledSearchTaskDao.findNextSetTask();
	}
	

	public void changeTaskStateToStarted(String id) {
		ScheduledSearchTask scheduledSearchTask = scheduledSearchTaskDao.findById(id);
		scheduledSearchTask.setState(ScheduledSearchState.STARTED);
		scheduledSearchTaskDao.merge(scheduledSearchTask);
	}
	
	public void changeTaskStateToFinished(String id) {
		ScheduledSearchTask scheduledSearchTask = scheduledSearchTaskDao.findById(id);
		scheduledSearchTask.setState(ScheduledSearchState.FINISHED);
		scheduledSearchTaskDao.merge(scheduledSearchTask);
	}
}
