package com.flightbuddy.user;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flightbuddy.db.AbstractDao;
import com.querydsl.core.types.EntityPath;

@Transactional
@Repository
public class UserDao extends AbstractDao<User, String> {

	private static final QUser USER = QUser.user;

	@Override
	protected EntityPath<User> getEntityPath() {
		return USER;
	}

	public User findByUsername(String username) {
		return from().where(USER.username.eq(username)).fetchOne();
	}
}