package org.greenscape.core.impl.config.json;

public class PropertyConfig {
	public String name;
	public String type;

	@Override
	public String toString() {
		return "property={name=" + name + ",type=" + type + "}";
	}
}
