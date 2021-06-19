package com.syntaxphoenix.avinity.redis.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

public abstract class RCollection<E extends RModel> extends RModel implements Iterable<E> {

	@Override
	public abstract Collection<E> getValue();

	@Override
	public abstract RCollection<E> clone();
	
	public abstract boolean add(E model);
	
	public abstract boolean add(int index, E model);
	
	public abstract E set(int index, E model);
	
	public abstract E remove(int index);
	
	public abstract boolean remove(E model);
	
	public abstract boolean contains(E model);
	
	public abstract int size();
	
	public abstract Stream<E> stream();
	
	public abstract Iterator<E> iterator();
	
	public abstract boolean isEmpty();
	
	public abstract RCollection<E> clear();

}
