package com.syntaxphoenix.avinity.redis.model;

public class RLongArray extends RArray {

	private long[] value;
	
	public RLongArray() {
		this(new long[0]);
	}

	public RLongArray(long[] value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.LONG_ARRAY;
	}

	@Override
	public long[] getValue() {
		return value;
	}

	public RLongArray setValue(long[] value) {
		this.value = value;
		return this;
	}

	@Override
	public RLongArray clone() {
		return new RLongArray(value.clone());
	}

}
