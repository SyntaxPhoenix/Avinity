package com.syntaxphoenix.avinity.redis.model;

public class RBoolean extends RModel {

	private boolean value;
	
	public RBoolean() {
		this(false);
	}

	public RBoolean(boolean value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.BOOLEAN;
	}

	@Override
	public Boolean getValue() {
		return value;
	}
	
	public boolean getPrimitive() {
		return value;
	}

	public RBoolean setValue(boolean value) {
		this.value = value;
		return this;
	}

	@Override
	public RBoolean clone() {
		return new RBoolean(value);
	}

}
