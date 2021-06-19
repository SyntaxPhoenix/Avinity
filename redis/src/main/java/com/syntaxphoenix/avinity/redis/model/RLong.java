package com.syntaxphoenix.avinity.redis.model;

public class RLong extends RNumber {

	private long value;
	
	public RLong() {
		this(0L);
	}

	public RLong(long value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.LONG;
	}

	@Override
	public Long getValue() {
		return value;
	}
	
	public long getPrimitive() {
		return value;
	}

	public RLong setValue(long value) {
		this.value = value;
		return this;
	}

	@Override
	public RLong clone() {
		return new RLong(value);
	}

}
