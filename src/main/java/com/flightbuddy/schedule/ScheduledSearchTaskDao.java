package com.flightbuddy.schedule;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.db.AbstractDao;
import com.flightbuddy.schedule.ScheduledSearchTask.ScheduledSearchState;
import com.querydsl.core.types.EntityPath;

@Transactional
@Repository
public class ScheduledSearchTaskDao extends AbstractDao<ScheduledSearchTask, UUID>{

	private static final QScheduledSearchTask SCHEDULED_SEARCH_TASK = QScheduledSearchTask.scheduledSearchTask;
	
	@Override
	protected EntityPath<ScheduledSearchTask> getEntityPath() {
		return SCHEDULED_SEARCH_TASK;
	}

	public ScheduledSearchTask findNextReadyTask() {
		return from()
				.where(SCHEDULED_SEARCH_TASK.state.eq(ScheduledSearchState.READY))
				.orderBy(SCHEDULED_SEARCH_TASK.executionTime.asc())
				.fetchFirst();
	}
}
