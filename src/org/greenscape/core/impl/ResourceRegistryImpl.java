package org.greenscape.core.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenscape.core.ModelResource;
import org.greenscape.core.Property;
import org.greenscape.core.Resource;
import org.greenscape.core.ResourceEvent;
import org.greenscape.core.ResourceRegistry;
import org.greenscape.core.ResourceType;
import org.greenscape.core.impl.config.json.ModelConfig;
import org.greenscape.core.impl.config.json.PropertyConfig;
import org.greenscape.core.impl.config.json.ResourceConfig;
import org.greenscape.core.impl.config.json.ResourcesConfig;
import org.greenscape.core.impl.config.json.WebletConfig;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component(property = { EventConstants.EVENT_TOPIC + "=" + ResourceRegistryImpl.TOPIC_BUNDLE_STARTED,
		EventConstants.EVENT_TOPIC + "=" + ResourceRegistryImpl.TOPIC_BUNDLE_STOPPED })
public class ResourceRegistryImpl implements ResourceRegistry, EventHandler {
	static final String TOPIC_BUNDLE_STARTED = "org/osgi/framework/BundleEvent/STARTED";
	static final String TOPIC_BUNDLE_STOPPED = "org/osgi/framework/BundleEvent/STOPPED";
	private static final String GS_RESOURCE = "GS-Resource";

	private final Map<String, Resource> resources;
	private EventAdmin eventAdmin;
	private LogService logService;

	public ResourceRegistryImpl() {
		resources = new HashMap<String, Resource>();
	}

	@Override
	public List<Resource> getResources() {
		return Collections.unmodifiableList(new ArrayList<Resource>(resources.values()));
	}

	@Override
	public List<Resource> getResources(ResourceType resourceType) {
		if (resourceType == null) {
			return getResources();
		}
		List<Resource> filteredResources = new ArrayList<Resource>();
		for (Resource resource : resources.values()) {
			if (resource.getType() == resourceType) {
				filteredResources.add(resource);
			}
		}
		return Collections.unmodifiableList(filteredResources);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends Resource> List<M> getResources(Class<M> clazz) {
		if (clazz == null) {
			return (List<M>) getResources();
		}
		List<M> filteredResources = new ArrayList<M>();
		for (Resource resource : resources.values()) {
			Class<?>[] interfaces = resource.getClass().getInterfaces();
			for (Class<?> cls : interfaces) {
				if (cls == clazz) {
					filteredResources.add((M) resource);
				}
			}
		}
		return Collections.unmodifiableList(filteredResources);
	}

	@Override
	public List<Resource> getResources(long bundleId) {
		List<Resource> filteredResources = new ArrayList<Resource>();
		for (Resource resource : resources.values()) {
			if (resource.getBundleId() == bundleId) {
				filteredResources.add(resource);
			}
		}
		return Collections.unmodifiableList(filteredResources);
	}

	@Override
	public List<Resource> getResources(long bundleId, ResourceType resourceType) {
		if (resourceType == null) {
			return getResources(bundleId);
		}
		List<Resource> filteredResources = new ArrayList<Resource>();
		for (Resource resource : resources.values()) {
			if (resource.getBundleId() == bundleId && resource.getType() == resourceType) {
				filteredResources.add(resource);
			}
		}
		return Collections.unmodifiableList(filteredResources);
	}

	@Override
	public Resource getResource(String name) {
		return resources.get(name.toLowerCase());
	}

	@Override
	public ModelResource getResourceByRemoteName(String name) {
		for (Resource resource : resources.values()) {
			if (resource.getType() == ResourceType.Model) {
				ModelResource modelResource = (ModelResource) resource;
				if (!modelResource.isAbstract() && modelResource.isRemote()
						&& modelResource.getRemoteName().toLowerCase().equals(name.toLowerCase())) {
					return modelResource;
				}
			}
		}
		return null;
	}

	@Override
	public void handleEvent(Event event) {
		Bundle bundle = (Bundle) event.getProperty(EventConstants.BUNDLE);
		switch (event.getTopic()) {
		case TOPIC_BUNDLE_STARTED:
			registerResources(bundle);
			break;
		case TOPIC_BUNDLE_STOPPED:
			unregisterResources(bundle);
			break;
		}
	}

	@Activate
	public void activate(ComponentContext ctx, Map<String, Object> properties) {
		registerExistingBundles(ctx);
	}

	@Modified
	public void modified(ComponentContext ctx, Map<String, Object> properties) {
		registerExistingBundles(ctx);
	}

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	public void unsetEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}

	@Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void unsetLogService(LogService logService) {
		this.logService = null;
	}

	private void registerExistingBundles(ComponentContext ctx) {
		BundleContext context = ctx.getBundleContext();
		Bundle[] bundles = context.getBundles();
		if (bundles != null) {
			for (Bundle bundle : bundles) {
				registerResources(bundle);
			}
		}
	}

	private void registerResources(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders();
		String resourcePath = headers.get(GS_RESOURCE);
		if (resourcePath == null) {
			return;
		}
		URL webletFile = bundle.getEntry(resourcePath);
		ObjectMapper mapper = new ObjectMapper();
		try {
			ResourcesConfig root = mapper.readValue(webletFile, ResourcesConfig.class);
			for (ResourceConfig resourceConfig : root.resources) {
				if (resourceConfig instanceof ModelConfig) {
					registerModelResource((ModelConfig) resourceConfig, bundle);
				} else if (resourceConfig instanceof WebletConfig) {
					registerWebletResource((WebletConfig) resourceConfig, bundle);
				}
			}
		} catch (IOException e) {
			logService.log(LogService.LOG_ERROR, e.getMessage(), e);
		}
	}

	private void registerModelResource(ModelConfig config, Bundle bundle) {
		ModelResourceImpl resource = new ModelResourceImpl(bundle.getBundleId(), config.name, ResourceType.Model);
		resource.setModelClass(config.modelClass);
		resource.setAbstract(config.isAbstract);
		resource.setRemote(config.remote);
		if (config.remoteName != null && !config.remoteName.equals("")) {
			resource.setRemoteName(config.remoteName);
		} else {
			resource.setRemoteName(config.name);
		}
		// TODO: validate remote name is unique
		if (config.inherits != null) {
			for (String inherits : config.inherits) {
				ModelResource parentResource = (ModelResource) getResource(inherits);
				if (parentResource == null) {
					// wrong parent name or parent does not exist (yet?)
					if (logService != null) {
						logService.log(LogService.LOG_ERROR, "Parent " + inherits + " does not exist");
					}
					return;
				} else {
					// add parent properties to this resource
					for (Property prop : parentResource.getProperties().values()) {
						Property property = new PropertyImpl(prop.getName(), prop.getType());
						resource.addProperty(property);
					}
					resource.getParents().add(parentResource);
				}
			}
		}
		if (config.properties != null) {
			for (PropertyConfig prop : config.properties) {
				Property property = new PropertyImpl(prop.name, prop.type);
				resource.addProperty(property);
			}
		}
		resources.put(resource.getName().toLowerCase(), resource);
		if (!resource.isAbstract()) {
			postEvent(TOPIC_RESOURCE_REGISTERED, resource, bundle);
		}
	}

	private void registerWebletResource(WebletConfig config, Bundle bundle) {
		WebletResourceImpl resource = new WebletResourceImpl(bundle.getBundleId(), config.name, ResourceType.Weblet);
		resource.setId(bundle.getSymbolicName() + "." + config.name);
		resource.setGroupId(bundle.getSymbolicName());
		resource.setTitle(config.title);
		resource.setInstanceable(config.instanceable);
		resource.setIcon(config.icon);
		resource.setViewURL(config.viewURL);
		resource.setHelpURL(config.helpURL);
		resource.setLoadJS(config.loadJS);
		resource.setLoadCSS(config.loadCSS);
		resources.put(resource.getName().toLowerCase(), resource);
		postEvent(TOPIC_RESOURCE_REGISTERED, resource, bundle);
	}

	private void unregisterResources(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders();
		String resourcePath = headers.get(GS_RESOURCE);
		if (resourcePath == null) {
			return;
		}
		List<Resource> bundleResources = getResources(bundle.getBundleId());
		for (Resource resource : bundleResources) {
			resources.remove(resource);
			if (resource.getType() == ResourceType.Model) {
				if (!((ModelResource) resource).isAbstract()) {
					postEvent(TOPIC_RESOURCE_UNREGISTERED, resource, bundle);
				}
			} else if (resource.getType() == ResourceType.Weblet) {
				postEvent(TOPIC_RESOURCE_UNREGISTERED, resource, bundle);
			}
		}
	}

	private void postEvent(String topic, Resource resource, Bundle bundle) {
		if (eventAdmin != null) {
			int eventType = topic.equals(TOPIC_RESOURCE_REGISTERED) ? ResourceEvent.REGISTERED : topic
					.equals(TOPIC_RESOURCE_UNREGISTERED) ? ResourceEvent.UNREGISTERED : ResourceEvent.UPDATED;
			ResourceEvent resourceEvent = new ResourceEvent(eventType, bundle);
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(ResourceEvent.RESOURCE_NAME, resource.getName());
			properties.put(ResourceEvent.RESOURCE_TYPE, resource.getType().name());
			properties.put(EventConstants.BUNDLE, bundle);
			properties.put(EventConstants.BUNDLE_ID, bundle.getBundleId());
			properties.put(EventConstants.BUNDLE_SYMBOLICNAME, bundle.getSymbolicName());
			properties.put(EventConstants.BUNDLE_VERSION, bundle.getVersion());
			properties.put(EventConstants.EVENT, resourceEvent);
			properties.put(EventConstants.EVENT_TOPIC, topic);
			properties.put(EventConstants.TIMESTAMP, System.currentTimeMillis());
			Event event = new Event(topic, properties);
			eventAdmin.postEvent(event);
		}
	}

}
