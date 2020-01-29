package com.flightbuddy.db;

import java.util.Collection;

interface Dao<T> {

	T merge(T entity);

	void persist(T entity);

	void remove(T entity);

	void remove(Collection<T> entity);

}
