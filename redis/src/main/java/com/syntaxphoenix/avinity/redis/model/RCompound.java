package com.syntaxphoenix.avinity.redis.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.syntaxphoenix.avinity.redis.model.data.RedisAdapterRegistry;
import com.syntaxphoenix.syntaxapi.utils.java.Primitives;

import java.util.Map.Entry;

public class RCompound extends RModel {

	private final HashMap<String, RModel> models = new HashMap<>();

	public RCompound() {}

	private RCompound(Map<String, RModel> models) {
		for (Entry<String, RModel> entry : models.entrySet()) {
			models.put(entry.getKey(), entry.getValue().clone());
		}
	}

	@Override
	public final RType getType() {
		return RType.COMPOUND;
	}

	@Override
	public Map<String, RModel> getValue() {
		return Collections.unmodifiableMap(models);
	}

	@Override
	public RCompound clone() {
		return new RCompound(models);
	}

	public Set<String> getKeys() {
		return models.keySet();
	}

	public RCompound set(String key, RModel model) {
		models.put(key, Objects.requireNonNull(model));
		return this;
	}

	@SuppressWarnings("unchecked")
	public boolean set(String key, Object object) {
		Objects.requireNonNull(object);
		RModel model = RedisAdapterRegistry.GLOBAL.wrap(Primitives.fromPrimitive((Class<Object>) object.getClass()), object);
		if (model == null) {
			return false;
		}
		set(key, model);
		return true;
	}

	public RModel get(String key) {
		return models.get(key);
	}

	public Optional<RModel> optional(String key) {
		return Optional.ofNullable(models.get(key));
	}

	public Optional<RModel> optional(String key, RType type) {
		return optional(key).filter(model -> model.getType() == type);
	}

	public boolean has(String key) {
		return models.containsKey(key);
	}

	public boolean has(String key, RType type) {
		RModel model = get(key);
		return model != null && model.getType() == type;
	}

	public RModel remove(String key) {
		return models.remove(key);
	}

	public int size() {
		return models.size();
	}

	public boolean isEmpty() {
		return models.isEmpty();
	}

}
