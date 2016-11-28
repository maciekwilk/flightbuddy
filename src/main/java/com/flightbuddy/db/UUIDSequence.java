package com.flightbuddy.db;

import java.util.UUID;
import java.util.Vector;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.Session;

public class UUIDSequence extends Sequence implements SessionCustomizer {

	private static final long serialVersionUID = 1L;

	public UUIDSequence() {
		super();
	}

	public UUIDSequence(final String name) {
		super(name);
	}

	@Override
	public Object getGeneratedValue(final Accessor accessor,final AbstractSession writeSession, final String seqName) {
		return UUID.randomUUID().toString();
	}

	@Override
	public Vector<?> getGeneratedVector(final Accessor accessor,
			final AbstractSession writeSession, final String seqName, final int size) {
		return null;
	}

	@Override
	public void onConnect() {
	}

	@Override
	public void onDisconnect() {
	}

	@Override
	public boolean shouldAcquireValueAfterInsert() {
		return false;
	}

	public boolean shouldOverrideExistingValue(final String seqName,
			final Object existingValue) {
		return ((String) existingValue).isEmpty();
	}

	@Override
	public boolean shouldUseTransaction() {
		return false;
	}

	@Override
	public boolean shouldUsePreallocation() {
		return false;
	}

	@Override
	public void customize(final Session session) throws Exception {
		final UUIDSequence sequence = new UUIDSequence("system-uuid");

		session.getLogin().addSequence(sequence);
	}
}