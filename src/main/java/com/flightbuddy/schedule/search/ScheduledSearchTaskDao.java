package com.flightbuddy.schedule.search;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.db.AbstractDao;
import com.flightbuddy.schedule.search.ScheduledSearchTask.ScheduledSearchState;
import com.querydsl.core.types.EntityPath;

@Transactional
@Repository
public class ScheduledSearchTaskDao extends AbstractDao<ScheduledSearchTask, String>{

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

	public ScheduledSearchTask findNextSetTask() {
		return from()
				.where(SCHEDULED_SEARCH_TASK.state.eq(ScheduledSearchState.SET))
				.orderBy(SCHEDULED_SEARCH_TASK.executionTime.asc())
				.fetchFirst();
	}
}
