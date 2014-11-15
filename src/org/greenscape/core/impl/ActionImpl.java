package org.greenscape.core.impl;

import org.greenscape.core.Action;

public class ActionImpl implements Action {
	private final String name;
	private final Long bit;

	public ActionImpl(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		this.bit = null;
	}

	public ActionImpl(String name, Long bit) {
		if (name == null) {
			throw new NullPointerException();
		}
		this.name = name;
		this.bit = bit;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Long getBit() {
		return bit;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ActionImpl)) {
			return false;
		}
		ActionImpl action = (ActionImpl) obj;
		return name == action.name || name.equals(action.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name + ":" + bit;
	}
}
