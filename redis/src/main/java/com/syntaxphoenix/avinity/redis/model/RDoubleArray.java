package com.syntaxphoenix.avinity.redis.model;

public class RDoubleArray extends RArray {

	private double[] value;

	public RDoubleArray() {
		this(new double[0]);
	}
	
	public RDoubleArray(double[] value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.DOUBLE_ARRAY;
	}

	@Override
	public double[] getValue() {
		return value;
	}

	public RDoubleArray setValue(double[] value) {
		this.value = value;
		return this;
	}

	@Override
	public RDoubleArray clone() {
		return new RDoubleArray(value.clone());
	}

}
