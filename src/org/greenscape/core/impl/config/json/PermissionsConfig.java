package org.greenscape.core.impl.config.json;

import java.util.List;

public class PermissionsConfig {
	public List<String> supports;
	public List<String> siteMemberDefaults;
	public List<String> guestUnsupported;

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("permissions={").append("supports=").append(supports).append(",siteMemberDefaults=")
		.append(siteMemberDefaults).append(",guestUnsupported").append(guestUnsupported).append("}");
		return string.toString();
	}
}
