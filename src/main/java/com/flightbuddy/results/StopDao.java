package com.flightbuddy.results;

import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.db.AbstractDao;
import com.querydsl.core.types.EntityPath;

@Transactional
@Repository
public class StopDao extends AbstractDao<Stop, UUID> {

	private static final QStop STOP = QStop.stop;
	
	@Override
	protected EntityPath<Stop> getEntityPath() {
		return STOP;
	}

}
