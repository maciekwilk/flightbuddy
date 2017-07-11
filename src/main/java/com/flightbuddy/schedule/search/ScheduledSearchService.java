package com.flightbuddy.schedule.search;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.user.User;

@Transactional
@Service
public class ScheduledSearchService {
	
	@Autowired ScheduledSearchDao scheduledSearchDao;
	
	@PreAuthorize("hasRole('SYSTEM')")
	public List<ScheduledSearch> getAllScheduledSearches() {
		return scheduledSearchDao.findAll();
	}

	public void save(ScheduledSearch newScheduledSearch, User currentUser) {
		ScheduledSearch alreadySavedOne = scheduledSearchDao.findForUser(currentUser);
		if (alreadySavedOne != null) {
			updateSavedSchedule(alreadySavedOne, newScheduledSearch);
			scheduledSearchDao.merge(alreadySavedOne);
		} else {
			newScheduledSearch.setUser(currentUser);
			currentUser.setScheduledSearch(newScheduledSearch);
			scheduledSearchDao.persist(newScheduledSearch);
		}
	}

	private void updateSavedSchedule(ScheduledSearch oldScheduledSearch, ScheduledSearch newScheduledSearch) {
		oldScheduledSearch.setFrom(newScheduledSearch.getFrom());
		oldScheduledSearch.setTo(newScheduledSearch.getTo());
		oldScheduledSearch.setDates(newScheduledSearch.getDates());
		oldScheduledSearch.setPrice(newScheduledSearch.getPrice());
		oldScheduledSearch.setWithReturn(newScheduledSearch.isWithReturn());
	}
}
