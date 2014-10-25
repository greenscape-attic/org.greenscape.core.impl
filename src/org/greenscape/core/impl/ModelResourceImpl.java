package org.greenscape.core.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenscape.core.ModelResource;
import org.greenscape.core.Property;
import org.greenscape.core.ResourceType;

public class ModelResourceImpl extends ResourceBase implements ModelResource {
	private String modelClass;
	private final Map<String, Property> properties;
	private final List<ModelResource> parents;
	private boolean isAbstract;
	private boolean remote;
	private String remoteName;
	private String remoteClass;

	public ModelResourceImpl(long bundleId, String name, ResourceType resourceType) {
		super(bundleId, name, resourceType);
		properties = new HashMap<String, Property>();
		parents = new ArrayList<ModelResource>();
	}

	@Override
	public String getModelClass() {
		return modelClass;
	}

	void setModelClass(String modelClass) {
		this.modelClass = modelClass;
	}

	@Override
	public Map<String, Property> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}

	void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	@Override
	public boolean isRemote() {
		return remote;
	}

	void setRemote(boolean remote) {
		this.remote = remote;
	}

	@Override
	public String getRemoteName() {
		return remoteName;
	}

	void setRemoteName(String remoteName) {
		this.remoteName = remoteName;
	}

	@Override
	public String getRemoteClass() {
		return remoteClass;
	}

	void setRemoteClass(String remoteClass) {
		this.remoteClass = remoteClass;
	}

	@Override
	public List<ModelResource> getParents() {
		return parents;
	}

	void addProperty(Property property) {
		properties.put(property.getName(), property);
	}

	@Override
	public String toString() {
		return getName();
	}
}
