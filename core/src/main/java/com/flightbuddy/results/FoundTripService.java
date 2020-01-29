package com.flightbuddy.results;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FoundTripService {

	@Autowired
	private FoundTripDao foundTripDao;

	public void saveFoundTrips(List<FoundTrip> foundTrips) { foundTrips.forEach(this::saveFoundTrip); }

	private void saveFoundTrip(FoundTrip foundTrip) {
		foundTripDao.persist(foundTrip);
	}
}
