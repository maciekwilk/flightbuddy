package com.flightbuddy.results;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.db.AbstractDao;
import com.querydsl.core.types.EntityPath;

@Transactional
@Repository
public class FlightDao extends AbstractDao<Flight, UUID> {

	private static final QFlight FLIGHT = QFlight.flight;
	
	@Override
	protected EntityPath<Flight> getEntityPath() {
		return FLIGHT;
	}

}
