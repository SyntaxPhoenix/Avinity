package com.syntaxphoenix.avinity.redis.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class RList<E extends RModel> extends RCollection<E> {

	private final ArrayList<E> list = new ArrayList<>();
	private final RType type;

	public RList(Class<E> clazz) {
		this.type = Objects.requireNonNull(RType.getByClass(clazz));
	}
	
	public RList(RType type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	private RList(RType type, ArrayList<E> list) {
		this.type = type;
		for (E value : list) {
			this.list.add((E) value.clone());
		}
	}

	@Override
	public List<E> getValue() {
		return Collections.unmodifiableList(list);
	}

	@Override
	public RList<E> clone() {
		return new RList<E>(type, list);
	}
	
	public final RType getValueType() {
		return type;
	}

	@Override
	public final RType getType() {
		return RType.LIST;
	}

	@Override
	public boolean add(E model) {
		if (model.getType() != type) {
			return false;
		}
		return list.add(model);
	}

	@Override
	public boolean add(int index, E model) {
		if (model.getType() != type) {
			return false;
		}
		list.add(index, model);
		return true;
	}

	@Override
	public E set(int index, E model) {
		if (model.getType() != type) {
			return null;
		}
		return list.set(index, model);
	}

	@Override
	public E remove(int index) {
		return list.remove(index);
	}

	@Override
	public boolean remove(E model) {
		if (model.getType() != type) {
			return false;
		}
		return list.remove(model);
	}

	@Override
	public boolean contains(E model) {
		if (model.getType() != type) {
			return false;
		}
		return list.contains(model);
	}

	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public Stream<E> stream() {
		return list.stream();
	}
	
	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public RList<E> clear() {
		list.clear();
		return this;
	}

}
