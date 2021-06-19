package com.syntaxphoenix.avinity.redis.model;

public class RShortArray extends RArray {

	private short[] value;
	
	public RShortArray() {
		this(new short[0]);
	}

	public RShortArray(short[] value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.SHORT_ARRAY;
	}

	@Override
	public short[] getValue() {
		return value;
	}

	public RShortArray setValue(short[] value) {
		this.value = value;
		return this;
	}

	@Override
	public RShortArray clone() {
		return new RShortArray(value.clone());
	}

}
