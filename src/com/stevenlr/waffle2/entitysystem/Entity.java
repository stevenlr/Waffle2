package com.stevenlr.waffle2.entitysystem;

import java.util.BitSet;

import com.stevenlr.waffle2.Waffle2;

public class Entity {

	private static EntitySystem _system = Waffle2.getInstance().getEntitySystem();

	public int id;
	private BitSet _components = new BitSet();

	public Entity() {
		_system.registerEntity(this);
	}

	public <T extends Component> T getAs(Class<T> type) {
		return _system.getComponent(this, type);
	}

	public void addComponent(Class<? extends Component> componentType, Component component) {
		int componentId = _system.getComponentId(componentType);

		_components.set(componentId);
		_system.addComponent(this, componentType, component);
	}

	public boolean hasComponent(Class<? extends Component> componentType) {
		return _components.get(_system.getComponentId(componentType));
	}

	public void removeComponent(Class<? extends Component> componentType) {
		int componentId = _system.getComponentId(componentType);

		_system.removeComponent(this, componentType);
		_components.clear(componentId);
	}

	public void removeAllComponents() {
		for (int i = 0; i < _components.length(); ++i) {
			if (_components.get(i)) {
				_system.removeComponent(this, _system.getComponentType(i));
			}
		}

		_components.clear();
	}

	boolean hasAllComponents(BitSet bitset) {
		for (int i = 0; i < bitset.size(); ++i) {
			if (bitset.get(i) && !_components.get(i)) {
				return false;
			}
		}

		return true;
	}
}
