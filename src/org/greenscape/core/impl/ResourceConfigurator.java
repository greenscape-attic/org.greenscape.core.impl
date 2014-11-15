package org.greenscape.core.impl;

import java.util.HashMap;
import java.util.Map;

import org.greenscape.core.Action;
import org.greenscape.core.ModelResource;
import org.greenscape.core.Resource;
import org.greenscape.core.ResourceEvent;
import org.greenscape.core.ResourceRegistry;
import org.greenscape.core.ResourceState;
import org.greenscape.core.ResourceType;
import org.greenscape.core.WebletResource;
import org.greenscape.core.model.ResourceAction;
import org.greenscape.core.model.ResourceActionModel;
import org.greenscape.persistence.PersistenceService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

@Component(property = { EventConstants.EVENT_TOPIC + "=" + ResourceRegistry.TOPIC_RESOURCE_REGISTERED,
		EventConstants.EVENT_TOPIC + "=" + ResourceRegistry.TOPIC_RESOURCE_MODIFIED,
		EventConstants.EVENT_TOPIC + "=" + ResourceRegistry.TOPIC_RESOURCE_UNREGISTERED })
public class ResourceConfigurator implements EventHandler {
	private ResourceRegistry resourceRegistry;
	private PersistenceService persistenceService;
	private EventAdmin eventAdmin;
	private BundleContext context;

	@Override
	public void handleEvent(Event event) {
		String name = (String) event.getProperty(ResourceEvent.RESOURCE_NAME);
		switch (event.getTopic()) {
		case ResourceRegistry.TOPIC_RESOURCE_REGISTERED:
			configureResource(name);
			break;
		case ResourceRegistry.TOPIC_RESOURCE_MODIFIED:
			break;
		case ResourceRegistry.TOPIC_RESOURCE_UNREGISTERED:
			break;
		}
	}

	private void configureResource(String resourceName) {
		Resource resource = resourceRegistry.getResource(resourceName);
		if (resource.getType() == ResourceType.Model) {
			configureModel((ModelResource) resource);
		} else if (resource.getType() == ResourceType.Weblet) {
			configureWeblet((WebletResource) resource);
		}
	}

	private void configureModel(ModelResource resource) {
		if (!persistenceService.modelExists(resource.getName())) {
			persistenceService.addModel(resource.getName());

			for (Action action : resource.getPermission().getSupports()) {
				ResourceAction ra = new ResourceAction();
				ra.setName(resource.getName());
				ra.setActionId(action.getName());
				ra.setBitwiseValue(action.getBit());
				persistenceService.save(ResourceActionModel.MODEL_NAME, ra);
			}
			((ResourceBase) resource).setState(ResourceState.CONFIGURED);
			postEvent(resource, context.getBundle(resource.getBundleId()));
		} else {
			((ResourceBase) resource).setState(ResourceState.CONFIGURED);
			// TODO: update model if it has changed
		}
	}

	private void configureWeblet(WebletResource resource) {
		// TODO Auto-generated method stub

	}

	@Activate
	private void activate(BundleContext context, Map<String, Object> properties) {
		this.context = context;
		for (Resource resource : resourceRegistry.getResources()) {
			configureResource(resource.getName());
		}
	}

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void setResourceRegistry(ResourceRegistry resourceRegistry) {
		this.resourceRegistry = resourceRegistry;
	}

	public void unsetResourceRegistry(ResourceRegistry resourceRegistry) {
		this.resourceRegistry = null;
	}

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void setPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

	public void unsetPersistenceService(PersistenceService persistenceService) {
		this.persistenceService = null;
	}

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	public void unsetEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}

	private void postEvent(Resource resource, Bundle bundle) {
		if (eventAdmin != null) {
			ResourceEvent resourceEvent = new ResourceEvent(ResourceEvent.CONFIGURED, bundle);
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(ResourceEvent.RESOURCE_NAME, resource.getName());
			properties.put(ResourceEvent.RESOURCE_TYPE, resource.getType().name());
			properties.put(EventConstants.BUNDLE, bundle);
			properties.put(EventConstants.BUNDLE_ID, bundle.getBundleId());
			properties.put(EventConstants.BUNDLE_SYMBOLICNAME, bundle.getSymbolicName());
			properties.put(EventConstants.BUNDLE_VERSION, bundle.getVersion());
			properties.put(EventConstants.EVENT, resourceEvent);
			properties.put(EventConstants.EVENT_TOPIC, ResourceRegistry.TOPIC_RESOURCE_CONFIGURED);
			properties.put(EventConstants.TIMESTAMP, System.currentTimeMillis());
			Event event = new Event(ResourceRegistry.TOPIC_RESOURCE_CONFIGURED, properties);
			eventAdmin.postEvent(event);
		}
	}
}
