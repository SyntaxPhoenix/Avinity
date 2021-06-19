package com.syntaxphoenix.avinity.redis.model;

public class RByte extends RNumber {

	private byte value;
	
	public RByte() {
		this((byte) 0);
	}

	public RByte(byte value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.BYTE;
	}

	@Override
	public Byte getValue() {
		return value;
	}
	
	public byte getPrimitive() {
		return value;
	}

	public RByte setValue(byte value) {
		this.value = value;
		return this;
	}

	@Override
	public RByte clone() {
		return new RByte(value);
	}

}
