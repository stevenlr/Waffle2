package com.stevenlr.waffle2.entitysystem;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EntitySystem {

	private int _nextEntityId = 0;
	private int _nextComponentId = 0;
	private Map<Class, Integer> _componentsIds = new HashMap<Class, Integer>();
	private Map<Integer, Class> _componentsFromIds = new HashMap<Integer, Class>();
	private Map<Integer, Entity> _entities = new HashMap<Integer, Entity>();
	private Map<Class, HashMap<Entity, Component>> _components = new HashMap<Class, HashMap<Entity, Component>>();

	private int getNextId() {
		return _nextEntityId++;
	}

	void registerEntity(Entity e) {
		e.id = getNextId();
		_entities.put(e.id, e);
	}

	int getComponentId(Class<? extends Component> componentType) {
		Integer i = _componentsIds.get(componentType);

		if (i == null) {
			i = _nextComponentId++;
			_componentsIds.put(componentType, i);
			_componentsFromIds.put(i, componentType);
			_components.put(componentType, new HashMap<Entity, Component>());
		}

		return i;
	}

	Class<? extends Component> getComponentType(int id) {
		return _componentsFromIds.get(id);
	}

	public Entity getEntity(int id) {
		return _entities.get(id);
	}

	public void removeEntity(Entity e) {
		e.removeAllComponents();
		_entities.remove(e.id);
	}

	<T> T getComponent(Entity entity, Class<T> componentType) {
		HashMap<Entity, ? extends Component> store = _components.get(componentType);
		T component = (T) store.get(entity);

		if (component == null) {
			throw new RuntimeException("Component doesn't exists for requested entity");
		}

		return component;
	}

	public List<Entity> getAllEntitiesPossessing(Class... requiredComponents) {
		List<Entity> list = new LinkedList<Entity>();

		BitSet bitset = new BitSet();

		for (Class c : requiredComponents) {
			int id = getComponentId(c);
			bitset.set(id);
		}

		for (Map.Entry<Integer, Entity> entry : _entities.entrySet()) {
			Entity e = entry.getValue();

			if (e.hasAllComponents(bitset)) {
				list.add(e);
			}
		}

		return list;
	}

	void addComponent(Entity entity, Class<? extends Component> componentType, Component component) {
		HashMap<Entity, Component> store = _components.get(componentType);

		if (store != null) {
			store.put(entity, component);
		}
	}

	void removeComponent(Entity entity, Class<? extends Component> componentType) {
		HashMap<Entity, Component> store = _components.get(componentType);

		if (store != null) {
			store.remove(entity);
		}
	}
}
