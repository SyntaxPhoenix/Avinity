package com.syntaxphoenix.avinity.redis.model.data;

import java.util.Set;

import com.syntaxphoenix.avinity.redis.model.RCompound;
import com.syntaxphoenix.avinity.redis.model.RModel;
import com.syntaxphoenix.syntaxapi.data.DataAdapterContext;
import com.syntaxphoenix.syntaxapi.data.DataContainer;
import com.syntaxphoenix.syntaxapi.data.DataType;
import com.syntaxphoenix.syntaxapi.utils.java.Primitives;

public class RedisContainer extends DataContainer implements DataAdapterContext {

	private final RedisAdapterRegistry registry = RedisAdapterRegistry.GLOBAL;
	private final RCompound root = new RCompound();

	@Override
	public RedisContainer newDataContainer() {
		return new RedisContainer();
	}

	@Override
	public DataAdapterContext getAdapterContext() {
		return this;
	}

	public RedisAdapterRegistry getAdapterRegistry() {
		return registry;
	}

	public RCompound getRoot() {
		return root;
	}

	@Override
	public Object get(String key) {
		RModel model = root.get(key);
		if (model == null)
			return model;
		return registry.extract(model);
	}

	public void set(String key, RModel model) {
		if (model == null)
			return;
		root.set(key, model);
	}

	@SuppressWarnings("unchecked")
	public void set(String key, Object object) {
		set(key, registry.wrap(Primitives.fromPrimitive((Class<Object>) object.getClass()), object));
	}

	@Override
	public <E, V> void set(String key, E value, DataType<V, E> type) {
		set(key, registry.wrap(type.getPrimitive(), type.toPrimitive(getAdapterContext(), value)));
	}

	@Override
	public boolean remove(String key) {
		return root.remove(key) != null;
	}

	@Override
	public Set<String> getKeyspaces() {
		return root.getKeys();
	}

	@Override
	public boolean isEmpty() {
		return root.isEmpty();
	}

	@Override
	public int size() {
		return root.size();
	}

}
