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
	
	private static final String GUEST_USERNAME = "guest";
	
	@PreAuthorize("hasRole('SYSTEM')")
	public List<ScheduledSearch> getAllScheduledSearches() {
		return scheduledSearchDao.findAll();
	}

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public void save(ScheduledSearch newScheduledSearch, User currentUser) {
		if (!currentUser.getUsername().equals(GUEST_USERNAME)) {
			ScheduledSearch alreadySavedOne = scheduledSearchDao.findForUser(currentUser);
			if (alreadySavedOne != null) {
				updateSavedSchedule(alreadySavedOne, newScheduledSearch);
				scheduledSearchDao.merge(alreadySavedOne);
			} else {
				persist(newScheduledSearch, currentUser);
			}
		}
	}

	private void updateSavedSchedule(ScheduledSearch oldScheduledSearch, ScheduledSearch newScheduledSearch) {
		oldScheduledSearch.setFrom(newScheduledSearch.getFrom());
		oldScheduledSearch.setTo(newScheduledSearch.getTo());
		oldScheduledSearch.setDates(newScheduledSearch.getDates());
		oldScheduledSearch.setMinPrice(newScheduledSearch.getMinPrice());
		oldScheduledSearch.setMaxPrice(newScheduledSearch.getMaxPrice());
		oldScheduledSearch.setWithReturn(newScheduledSearch.isWithReturn());
		Passengers passengers = newScheduledSearch.getPassengers();
		passengers.setScheduledSearch(oldScheduledSearch);
		oldScheduledSearch.setPassengers(passengers);
	}

	private void persist(ScheduledSearch newScheduledSearch, User currentUser) {
		newScheduledSearch.setUser(currentUser);
		currentUser.setScheduledSearch(newScheduledSearch);
		Passengers passengers = newScheduledSearch.getPassengers();
		passengers.setScheduledSearch(newScheduledSearch);
		scheduledSearchDao.persist(newScheduledSearch);
	}
}
