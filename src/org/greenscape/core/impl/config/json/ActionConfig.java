package org.greenscape.core.impl.config.json;

public class ActionConfig {
	public String action;
	public long bit;

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("action={").append(action).append(",bit=").append(bit).append("}");
		return string.toString();
	}
}
