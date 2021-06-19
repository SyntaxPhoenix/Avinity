package com.syntaxphoenix.avinity.redis.model;

public class RShort extends RNumber {

	private short value;
	
	public RShort() {
		this((short) 0);
	}

	public RShort(short value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.SHORT;
	}

	@Override
	public Short getValue() {
		return value;
	}
	
	public short getPrimitive() {
		return value;
	}

	public RShort setValue(short value) {
		this.value = value;
		return this;
	}

	@Override
	public RShort clone() {
		return new RShort(value);
	}

}
