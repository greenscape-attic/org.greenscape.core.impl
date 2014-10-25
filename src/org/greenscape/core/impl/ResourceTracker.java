package org.greenscape.core.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenscape.core.Resource;
import org.greenscape.core.ResourceRegistry;
import org.greenscape.core.ResourceType;
import org.greenscape.core.impl.config.json.ModelConfig;
import org.greenscape.core.impl.config.json.ResourceConfig;
import org.greenscape.core.impl.config.json.ResourcesConfig;
import org.greenscape.core.impl.config.json.WebletConfig;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;

@Deprecated
public class ResourceTracker {
	private static final String RESOURCE_EVENT_UNREGISTERED = "org/greenscape/core/ResourceEvent/UNREGISTERED";
	private static final String RESOURCE_EVENT_REGISTERED = "org/greenscape/core/ResourceEvent/REGISTERED";
	private static final String RESOURCE_EVENT_MODIFIED = "org/greenscape/core/ResourceEvent/MODIFIED";
	private static final String GS_RESOURCE = "GS-Resource";

	private final ResourceRegistryImpl registryImpl;
	private BundleContext context;
	private EventAdmin eventAdmin;

	public ResourceTracker() {
		registryImpl = new ResourceRegistryImpl();
		context.registerService(ResourceRegistry.class, registryImpl, null);
	}

	@Activate
	public void activate(ComponentContext ctx, Map<String, Object> properties) {
		context = ctx.getBundleContext();
	}

	public String addingBundle(Bundle bundle, BundleEvent event) {
		Dictionary<String, String> headers = bundle.getHeaders();
		String resourcePath = headers.get(GS_RESOURCE);
		if (resourcePath != null) {
			registerResources(bundle, resourcePath);
		}
		return null;
	}

	public void removedBundle(Bundle bundle, BundleEvent event, String object) {
		Dictionary<String, String> headers = bundle.getHeaders();
		String resourcePath = headers.get(GS_RESOURCE);
		if (resourcePath != null) {
			unregister(bundle.getBundleId());
		}
	}

	@Reference
	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	private void registerResources(Bundle bundle, String resourcePath) {
		URL webletFile = bundle.getEntry(resourcePath);
		ObjectMapper mapper = new ObjectMapper();
		try {
			ResourcesConfig root = mapper.readValue(webletFile, ResourcesConfig.class);
			for (ResourceConfig resourceConfig : root.resources) {
				if (resourceConfig instanceof ModelConfig) {
					registerModelResource((ModelConfig) resourceConfig);
				} else if (resourceConfig instanceof WebletConfig) {
					registerWebletResource((WebletConfig) resourceConfig);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void registerModelResource(ModelConfig config) {
		// ModelResourceImpl resource = new ModelResourceImpl();
		// postEvent(RESOURCE_EVENT_REGISTERED, resource);
	}

	private void registerWebletResource(WebletConfig config) {
		// WebletResource resource = new WebletResourceImpl();
		// postEvent(RESOURCE_EVENT_REGISTERED, resource);
	}

	private void unregister(long bundleId) {
		List<Resource> resources = registryImpl.getResources(bundleId);
		for (Resource resource : resources) {
			if (resource.getType() == ResourceType.Model) {
				postEvent(RESOURCE_EVENT_UNREGISTERED, resource);
			} else if (resource.getType() == ResourceType.Weblet) {
				postEvent(RESOURCE_EVENT_UNREGISTERED, resource);
			}
		}
	}

	private void postEvent(String topic, Resource resource) {
		ServiceReference<EventAdmin> ref = context.getServiceReference(EventAdmin.class);
		eventAdmin = context.getService(ref);
		if (eventAdmin != null) {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("resourceName", resource.getName());
			properties.put("resourceType", resource.getType().name());
			Event event = new Event(topic, properties);
			eventAdmin.postEvent(event);
		}
	}
}
