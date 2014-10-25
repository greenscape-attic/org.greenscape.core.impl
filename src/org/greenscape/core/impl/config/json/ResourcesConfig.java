package org.greenscape.core.impl.config.json;

import java.util.List;

public class ResourcesConfig {
	public List<ResourceConfig> resources;

	@Override
	public String toString() {
		return resources != null ? resources.toString() : "<empty>";
	}
}
