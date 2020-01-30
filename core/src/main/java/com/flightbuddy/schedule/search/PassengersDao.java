package com.flightbuddy.schedule.search;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.db.AbstractDao;
import com.querydsl.core.types.EntityPath;

@Transactional
@Repository
public class PassengersDao extends AbstractDao<Passengers, String>{

	private static final QPassengers PASSENGERS = QPassengers.passengers;
	
	@Override
	protected EntityPath<Passengers> getEntityPath() {
		return PASSENGERS;
	}
}
