package com.flightbuddy.schedule;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.db.AbstractDao;
import com.querydsl.core.types.EntityPath;

@Transactional
@Repository
public class ScheduledSearchDao extends AbstractDao<ScheduledSearch, UUID>{

	private static final QScheduledSearch SCHEDULED_SEARCH = QScheduledSearch.scheduledSearch;
	
	@Override
	protected EntityPath<ScheduledSearch> getEntityPath() {
		return SCHEDULED_SEARCH;
	}

	public List<ScheduledSearch> findAll() {
		return from().fetch();
	}

}
