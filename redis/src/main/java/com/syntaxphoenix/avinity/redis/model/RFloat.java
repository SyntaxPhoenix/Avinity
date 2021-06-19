package com.syntaxphoenix.avinity.redis.model;

public class RFloat extends RNumber {

	private float value;
	
	public RFloat() {
		this(0.0f);
	}

	public RFloat(float value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.FLOAT;
	}

	@Override
	public Float getValue() {
		return value;
	}
	
	public float getPrimitive() {
		return value;
	}

	public RFloat setValue(float value) {
		this.value = value;
		return this;
	}

	@Override
	public RFloat clone() {
		return new RFloat(value);
	}

}
