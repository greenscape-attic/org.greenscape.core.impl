package org.greenscape.core.impl;

import org.greenscape.core.Resource;
import org.greenscape.core.ResourcePermission;
import org.greenscape.core.ResourceState;
import org.greenscape.core.ResourceType;

public abstract class ResourceBase implements Resource {
	private final String name;
	private final ResourceType resourceType;
	private ResourceState state;
	private final long bundleId;
	private final ResourcePermission resourcePermission;
	private boolean initialised;

	public ResourceBase(long bundleId, String name, ResourceType resourceType) {
		this.bundleId = bundleId;
		this.name = name;
		this.resourceType = resourceType;
		this.initialised = false;
		this.resourcePermission = new ResourcePermissionImpl();
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
	public ResourceState getState() {
		return state;
	}

	void setState(ResourceState state) {
		this.state = state;
	}

	@Override
	public ResourcePermission getPermission() {
		return resourcePermission;
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
