package org.greenscape.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.greenscape.core.Action;
import org.greenscape.core.ResourcePermission;

public class ResourcePermissionImpl implements ResourcePermission {
	private final List<Action> supports;
	private final List<Action> guestDefaults;

	public ResourcePermissionImpl() {
		supports = new ArrayList<>();
		guestDefaults = new ArrayList<>();
	}

	@Override
	public List<Action> getSupports() {
		return supports;
	}

	@Override
	public List<Action> getGuestDefaults() {
		return guestDefaults;
	}
}
