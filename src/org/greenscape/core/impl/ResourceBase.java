package org.greenscape.core.impl;

import java.util.List;

import org.greenscape.core.Resource;
import org.greenscape.core.ResourcePermission;
import org.greenscape.core.ResourceType;

public abstract class ResourceBase implements Resource {
	private final String name;
	private final ResourceType resourceType;
	private final long bundleId;
	private List<ResourcePermission> resourcePermissions;
	private boolean initialised;

	public ResourceBase(long bundleId, String name, ResourceType resourceType) {
		this.bundleId = bundleId;
		this.name = name;
		this.resourceType = resourceType;
		this.initialised = false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ResourceType getType() {
		return resourceType;
	}

	@Override
	public List<ResourcePermission> getPermissions() {
		return resourcePermissions;
	}

	@Override
	public long getBundleId() {
		return bundleId;
	}

	/**
	 * @return the initialised
	 */
	public boolean isInitialised() {
		return initialised;
	}

	/**
	 * @param initialised
	 *            the initialised to set
	 */
	public void setInitialised(boolean initialised) {
		this.initialised = initialised;
	}

}
