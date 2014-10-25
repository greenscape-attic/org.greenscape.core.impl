package org.greenscape.core.impl;

import org.greenscape.core.ResourceType;
import org.greenscape.core.WebletResource;

public class WebletResourceImpl extends ResourceBase implements WebletResource {
	private String id;
	private String groupId;
	private String title;
	private String icon;
	private Boolean instanceable;
	private String viewURL;
	private String helpURL;
	private String loadJS;
	private String loadCSS;

	public WebletResourceImpl(long bundleId, String name, ResourceType resourceType) {
		super(bundleId, name, resourceType);
	}

	@Override
	public String getId() {
		return id;
	}

	void setId(String id) {
		this.id = id;
	}

	@Override
	public String getGroupId() {
		return groupId;
	}

	void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public String getTitle() {
		return title;
	}

	void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getIcon() {
		return icon;
	}

	void setIcon(String icon) {
		this.icon = icon;
	}

	@Override
	public Boolean isInstanceable() {
		return instanceable;
	}

	public void setInstanceable(Boolean instanceable) {
		this.instanceable = instanceable;
	}

	@Override
	public String getViewURL() {
		return viewURL;
	}

	public void setViewURL(String viewURL) {
		this.viewURL = viewURL;
	}

	@Override
	public String getHelpURL() {
		return helpURL;
	}

	public void setHelpURL(String helpURL) {
		this.helpURL = helpURL;
	}

	@Override
	public String getLoadJS() {
		return loadJS;
	}

	public void setLoadJS(String loadJS) {
		this.loadJS = loadJS;
	}

	@Override
	public String getLoadCSS() {
		return loadCSS;
	}

	public void setLoadCSS(String loadCSS) {
		this.loadCSS = loadCSS;
	}

	@Override
	public String toString() {
		return getName();
	}
}
