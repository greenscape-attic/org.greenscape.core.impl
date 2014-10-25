package org.greenscape.core.service.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.greenscape.core.ModelResource;
import org.greenscape.core.Property;
import org.greenscape.core.PropertyNotFoundException;
import org.greenscape.core.ResourceRegistry;
import org.greenscape.core.service.Service;
import org.greenscape.persistence.DocumentModel;
import org.greenscape.persistence.PersistedModel;
import org.greenscape.persistence.PersistenceService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

@Component
public class ServiceImpl implements Service {

	private ResourceRegistry resourceRegistry;
	private PersistenceService persistenceService;

	@SuppressWarnings("unchecked")
	@Override
	public <M extends DocumentModel> List<M> find(Class<? extends DocumentModel> clazz) {
		return (List<M>) persistenceService.find(clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends DocumentModel> List<M> find(String modelName) {
		return (List<M>) persistenceService.find(modelName);
	}

	@Override
	public <M extends DocumentModel> List<M> find(String organizationId, Class<? extends DocumentModel> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends DocumentModel> M find(String modelName, String id) {
		return (M) persistenceService.findById(modelName, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends DocumentModel> M find(Class<? extends DocumentModel> clazz, String id) {
		return (M) persistenceService.findById(clazz, id);
	}

	@Override
	public <M extends DocumentModel> M find(String organizationId, Class<? extends DocumentModel> clazz, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends DocumentModel> List<M> find(String modelName, Map<String, List<String>> properties) {
		ModelResource modelResource = (ModelResource) resourceRegistry.getResource(modelName);
		Map<String, Property> modelProperties = modelResource.getProperties();

		Map<String, Object> props = new HashMap<>();
		for (String propertyName : properties.keySet()) {
			Property property = modelProperties.get(propertyName);
			if (property == null) {
				throw new PropertyNotFoundException("Property " + propertyName + " not found in model " + modelName);
			}
			List<String> values = properties.get(propertyName);
			List<Object> typedValues = new ArrayList<>();

			if (values == null) {
				continue;
			}
			for (String val : values) {
				switch (property.getType()) {
				case "java.lang.Boolean":
					typedValues.add(Boolean.parseBoolean(val));
					break;
				case "java.lang.Double":
					typedValues.add(Double.parseDouble(val));
					break;
				case "java.lang.Float":
					typedValues.add(Float.parseFloat(val));
					break;
				case "java.lang.Integer":
					typedValues.add(Integer.parseInt(val));
					break;
				case "java.lang.Long":
					typedValues.add(Long.parseLong(val));
					break;
				case "java.lang.String":
					typedValues.add(val);
					break;
				}
			}
			if (typedValues.size() > 1) {
				props.put(propertyName, typedValues);
			} else {
				props.put(propertyName, typedValues.get(0));
			}
		}

		return (List<M>) persistenceService.findByProperties(modelName, props);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends DocumentModel> List<M> find(Class<? extends DocumentModel> clazz,
			Map<String, List<String>> properties) {
		Map<String, Object> props = new HashMap<>();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method m : methods) {
			String name = m.getName();
			if (name.startsWith("get")) {
				Type type = m.getGenericReturnType();
				String prop = name.substring(3, 4).toLowerCase() + name.substring(4);
				List<String> values = properties.get(prop);
				List<Object> typedValues = new ArrayList<>();

				if (values == null) {
					continue;
				}
				for (String val : values) {
					if (type == Double.class) {
						typedValues.add(Double.parseDouble(val));
					} else if (type == Float.class) {
						typedValues.add(Float.parseFloat(val));
					} else if (type == Integer.class) {
						typedValues.add(Integer.parseInt(val));
					} else if (type == Long.class) {
						typedValues.add(Long.parseLong(val));
					} else if (type == String.class) {
						typedValues.add(val);
					}
				}
				if (typedValues.size() > 1) {
					props.put(prop, typedValues);
				} else {
					props.put(prop, typedValues.get(0));
				}
			} else if (name.startsWith("is")) {
				String prop = name.substring(2, 3).toLowerCase() + name.substring(3);
				List<String> values = properties.get(prop);
				List<Object> typedValues = new ArrayList<>();

				if (values == null) {
					continue;
				}

				for (String val : values) {
					typedValues.add(Boolean.parseBoolean(val));
				}
				if (typedValues.size() > 1) {
					props.put(prop, typedValues);
				} else {
					props.put(prop, typedValues.get(0));
				}
			}
		}
		return (List<M>) persistenceService.findByProperties(clazz, props);
	}

	@Override
	public <M extends DocumentModel> List<M> find(String organizationId, Class<? extends DocumentModel> clazz,
			Map<String, List<String>> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends DocumentModel> List<M> find(String modelName, String propertyName, Object value) {
		return (List<M>) persistenceService.findByProperty(modelName, propertyName, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends DocumentModel> List<M> find(Class<? extends DocumentModel> clazz, String propertyName,
			Object value) {
		return (List<M>) persistenceService.findByProperty(clazz, propertyName, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends DocumentModel> List<M> find(String organizationId, String modelName, String propertyName,
			Object value) {
		Map<String, Object> props = new HashMap<>();
		props.put(PersistedModel.ORGANIZATION_ID, organizationId);
		props.put(propertyName, value);
		return (List<M>) persistenceService.findByProperties(modelName, props);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends DocumentModel> List<M> find(String organizationId, Class<? extends DocumentModel> clazz,
			String propertyName, Object value) {
		Map<String, Object> props = new HashMap<>();
		props.put(PersistedModel.ORGANIZATION_ID, organizationId);
		props.put(propertyName, value);
		return (List<M>) persistenceService.findByProperties(clazz, props);
	}

	@Override
	public <M extends DocumentModel> M save(String modelName, M model) {
		persistenceService.save(modelName, model);
		return model;
	}

	@Override
	public <M extends DocumentModel> M save(M model) {
		persistenceService.save(model);
		return model;
	}

	@Override
	public <M extends DocumentModel> M update(String modelName, M model) {
		persistenceService.update(modelName, model);
		return model;
	}

	@Override
	public <M extends DocumentModel> M update(M model) {
		persistenceService.update(model);
		return model;
	}

	@Override
	public void delete(String modelName) {
		persistenceService.delete(modelName);
	}

	@Override
	public void delete(Class<? extends DocumentModel> clazz) {
		persistenceService.delete(clazz);
	}

	@Override
	public void delete(String modelName, String modelId) {
		persistenceService.delete(modelName, modelId);
	}

	@Override
	public void delete(Class<? extends DocumentModel> clazz, String modelId) {
		persistenceService.delete(clazz, modelId);
	}

	@Reference
	public void setResourceRegistry(ResourceRegistry resourceRegistry) {
		this.resourceRegistry = resourceRegistry;
	}

	public void unsetResourceRegistry(ResourceRegistry resourceRegistry) {
		this.resourceRegistry = null;
	}

	@Reference(policy = ReferencePolicy.DYNAMIC)
	public void setPersistenceService(PersistenceService persistence) {
		this.persistenceService = persistence;
	}

	public void unsetPersistenceService(PersistenceService persistence) {
		this.persistenceService = null;
	}

}