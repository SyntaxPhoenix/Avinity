package com.syntaxphoenix.avinity.redis.model;

public class RInt extends RNumber {

	private int value;
	
	public RInt() {
		this(0);
	}

	public RInt(int value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.INT;
	}

	@Override
	public Integer getValue() {
		return value;
	}
	
	public int getPrimitive() {
		return value;
	}

	public RInt setValue(int value) {
		this.value = value;
		return this;
	}

	@Override
	public RInt clone() {
		return new RInt(value);
	}

}
