package org.greenscape.core.impl.config.json;

public class WebletConfig extends ResourceConfig {
	public String name;
	public String title;
	public String icon;
	public Boolean instanceable;
	public String viewURL;
	public String helpURL;
	public String loadJS;
	public String loadCSS;

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("webletResource={").append("name=").append(name).append(",title=").append(title).append(",icon=")
		.append(icon).append(",instanceable=").append(instanceable).append(",viewURL=").append(viewURL)
		.append(",helpURL=").append(helpURL).append(",loadJS=").append(loadJS).append(",loadCSS=")
		.append(loadCSS).append(",permissions=").append(permissions).append("}");
		return string.toString();
	}
}
