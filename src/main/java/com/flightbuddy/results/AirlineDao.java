package com.flightbuddy.results;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.flightbuddy.db.AbstractDao;
import com.querydsl.core.types.EntityPath;

@Repository
public class AirlineDao extends AbstractDao<Airline, UUID> {

	private static final QAirline AIRLINE = QAirline.airline;

	@Override
	protected EntityPath<Airline> getEntityPath() {
		return AIRLINE;
	}
}
