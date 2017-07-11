package com.flightbuddy.schedule.search;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.db.AbstractDao;
import com.flightbuddy.user.User;
import com.querydsl.core.types.EntityPath;

@Transactional
@Repository
public class ScheduledSearchDao extends AbstractDao<ScheduledSearch, String>{

	private static final QScheduledSearch SCHEDULED_SEARCH = QScheduledSearch.scheduledSearch;
	
	@Override
	protected EntityPath<ScheduledSearch> getEntityPath() {
		return SCHEDULED_SEARCH;
	}

	public List<ScheduledSearch> findAll() {
		return from().fetch();
	}

	public ScheduledSearch findForUser(User user) {
		return from().where(SCHEDULED_SEARCH.user.eq(user)).fetchFirst();
	}

}
