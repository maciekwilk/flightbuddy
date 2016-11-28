package com.flightbuddy.db;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;

public abstract class AbstractDao<T, K> implements Dao<T> {

	@PersistenceContext
	private EntityManager em;

	public T findById(K key) {
		return getEM().find(getEntityPath().getType(), key);
	}

	protected abstract EntityPath<T> getEntityPath();

	@Override
	public void persist(T entity) {
		em.persist(entity);
	}

	@Override
	public T merge(T entity) {
		return em.merge(entity);
	}

	@Override
	public void remove(T entity) {
		em.remove(entity);
	}

	@Override
	public void remove(Collection<T> entities) {
		for (T t : entities) {
			remove(t);
		}
	}

	public boolean exists(JPAQuery<?> query) {
		return query.select(Expressions.ONE).fetchFirst() != null;
	}

	private JPAQuery<T> query() {
		return new JPAQuery<T>(em);
	}

	protected JPAQuery<T> from() {
		return query().from(getEntityPath()).select(getEntityPath());
	}

	protected EntityManager getEM() {
		return em;
	}
}