package com.flightbuddy.schedule;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ScheduledSearchService {
	
	@Autowired ScheduledSearchDao scheduledSearchDao;
	
	public List<ScheduledSearch> getAllScheduledSearches() {
		return scheduledSearchDao.findAll();
	}

	public void save(ScheduledSearch scheduledSearch) {
		scheduledSearchDao.persist(scheduledSearch);
	}
}
