package com.syntaxphoenix.avinity.redis.model;

import java.math.BigInteger;

public class RBigInteger extends RNumber {
	
	private BigInteger value;
	
	public RBigInteger() {
		this(BigInteger.ZERO);
	}
	
	public RBigInteger(BigInteger value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.BIG_INTEGER;
	}
	
	@Override
	public BigInteger getValue() {
		return value;
	}
	
	public RBigInteger setValue(BigInteger value) {
		this.value = value;
		return this;
	}

	@Override
	public RBigInteger clone() {
		return new RBigInteger(value);
	}

}
