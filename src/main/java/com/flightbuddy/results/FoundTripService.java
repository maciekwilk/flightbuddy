package com.flightbuddy.results;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FoundTripService {

	@Autowired FoundTripDao foundTripDao;
	
	public void saveFoundTrip(FoundTrip foundTrip) {
		foundTripDao.persist(foundTrip);
	}
	
	public void saveFoundTrips(List<FoundTrip> foundTrips) {
		foundTrips.forEach(foundTrip -> saveFoundTrip(foundTrip));
	}
}
