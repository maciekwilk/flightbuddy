package com.flightbuddy.results;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.db.AbstractDao;
import com.querydsl.core.types.EntityPath;

@Transactional
@Repository
public class AirlineDao extends AbstractDao<Airline, String> {

	private static final QAirline AIRLINE = QAirline.airline;

	@Override
	protected EntityPath<Airline> getEntityPath() {
		return AIRLINE;
	}
}
