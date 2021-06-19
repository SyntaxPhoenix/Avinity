package com.syntaxphoenix.avinity.redis.model;

import java.math.BigDecimal;

public class RBigDecimal extends RNumber {
	
	private BigDecimal value;
	
	public RBigDecimal() {
		this(BigDecimal.ZERO);
	}
	
	public RBigDecimal(BigDecimal value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.BIG_DECIMAL;
	}
	
	@Override
	public BigDecimal getValue() {
		return value;
	}
	
	public RBigDecimal setValue(BigDecimal value) {
		this.value = value;
		return this;
	}

	@Override
	public RBigDecimal clone() {
		return new RBigDecimal(value);
	}

}
