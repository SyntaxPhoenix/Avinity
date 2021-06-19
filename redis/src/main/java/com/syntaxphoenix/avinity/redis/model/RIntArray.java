package com.syntaxphoenix.avinity.redis.model;

public class RIntArray extends RArray {

	private int[] value;
	
	public RIntArray() {
		this(new int[0]);
	}

	public RIntArray(int[] value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.INT_ARRAY;
	}

	@Override
	public int[] getValue() {
		return value;
	}

	public RIntArray setValue(int[] value) {
		this.value = value;
		return this;
	}

	@Override
	public RIntArray clone() {
		return new RIntArray(value.clone());
	}

}
