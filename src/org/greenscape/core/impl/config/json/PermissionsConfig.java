package org.greenscape.core.impl.config.json;

import java.util.List;

public class PermissionsConfig {
	public List<ActionConfig> supports;
	public List<String> siteMemberDefaults;
	public List<String> guestDefaults;
	public List<String> guestUnsupported;

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("permissions={").append("supports=").append(supports).append(",siteMemberDefaults=")
		.append(siteMemberDefaults).append(",guestDefaults=").append(guestDefaults)
		.append(",guestUnsupported=").append(guestUnsupported).append("}");
		return string.toString();
	}
}
