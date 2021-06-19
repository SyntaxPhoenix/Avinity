package com.syntaxphoenix.avinity.redis.model;

public class RBooleanArray extends RArray {

	private boolean[] value;
	
	public RBooleanArray() {
		this(new boolean[0]);
	}

	public RBooleanArray(boolean[] value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.BOOLEAN_ARRAY;
	}

	@Override
	public boolean[] getValue() {
		return value;
	}

	public RBooleanArray setValue(boolean[] value) {
		this.value = value;
		return this;
	}

	@Override
	public RBooleanArray clone() {
		return new RBooleanArray(value.clone());
	}

}
