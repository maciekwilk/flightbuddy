package com.flightbuddy.results;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.db.AbstractDao;
import com.querydsl.core.types.EntityPath;

@Transactional
@Repository
public class FoundTripDao extends AbstractDao<FoundTrip, String> {

	private static final QFoundTrip FOUND_TRIP = QFoundTrip.foundTrip;
	
	@Override
	protected EntityPath<FoundTrip> getEntityPath() {
		return FOUND_TRIP;
	}

}
