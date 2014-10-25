package org.greenscape.core.impl;

import org.greenscape.core.Property;

public class PropertyImpl implements Property {
	private final String name;
	private final String type;

	public PropertyImpl(String name, String type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return type;
	}

}
