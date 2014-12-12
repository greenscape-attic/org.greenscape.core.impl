package org.greenscape.core.service.impl;

import org.greenscape.core.model.UserModel;
import org.greenscape.core.service.Service;
import org.greenscape.core.service.UserService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component
public class UserServiceImpl implements UserService {
	private Service service;

	@Override
	public UserModel findById(String modelId) {
		return service.findByModelId(UserModel.MODEL_NAME, modelId);
	}

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void setService(Service service) {
		this.service = service;
	}

	public void unsetService(Service service) {
		this.service = null;
	}
}
