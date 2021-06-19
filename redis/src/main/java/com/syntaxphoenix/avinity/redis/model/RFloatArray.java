package com.syntaxphoenix.avinity.redis.model;

public class RFloatArray extends RArray {

	private float[] value;
	
	public RFloatArray() {
		this(new float[0]);
	}

	public RFloatArray(float[] value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.FLOAT_ARRAY;
	}

	@Override
	public float[] getValue() {
		return value;
	}

	public RFloatArray setValue(float[] value) {
		this.value = value;
		return this;
	}

	@Override
	public RFloatArray clone() {
		return new RFloatArray(value.clone());
	}

}
