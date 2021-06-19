package com.syntaxphoenix.avinity.redis.model;

public class RDouble extends RNumber {

	private double value;
	
	public RDouble() {
		this(0.0D);
	}

	public RDouble(double value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.DOUBLE;
	}

	@Override
	public Double getValue() {
		return value;
	}
	
	public double getPrimitive() {
		return value;
	}

	public RDouble setValue(double value) {
		this.value = value;
		return this;
	}

	@Override
	public RDouble clone() {
		return new RDouble(value);
	}

}
