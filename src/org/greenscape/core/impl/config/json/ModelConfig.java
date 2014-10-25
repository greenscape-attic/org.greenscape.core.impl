package org.greenscape.core.impl.config.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModelConfig extends ResourceConfig {

	public String name;

	public String modelClass;

	@JsonProperty("abstract")
	public boolean isAbstract;

	public List<String> inherits;

	public List<PropertyConfig> properties;

	public boolean remote;

	public String remoteName;

	public String remoteClass;

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("modelResource={").append("name=").append(name).append(",modelClass=").append(modelClass)
		.append(",abstract=").append(isAbstract).append(",remote=").append(remote).append(",remoteName=")
		.append(remoteName).append(",remoteClass=").append(remoteClass).append(",parent=").append(inherits)
		.append(",properties=").append(properties).append(",permissions=").append(permissions).append("}");

		return string.toString();
	}
}
