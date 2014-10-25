package org.greenscape.core.impl.config.json;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonSubTypes({ @Type(value = ModelConfig.class, name = "modelResource"),
	@Type(value = WebletConfig.class, name = "webletResource") })
public abstract class ResourceConfig {
	public PermissionsConfig permissions;
}
