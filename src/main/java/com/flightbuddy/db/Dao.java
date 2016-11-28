package com.flightbuddy.db;

import java.util.Collection;

public interface Dao<T> {

	public T merge(T entity);

	public void persist(T entity);

	public void remove(T entity);

	public void remove(Collection<T> entity);

}
