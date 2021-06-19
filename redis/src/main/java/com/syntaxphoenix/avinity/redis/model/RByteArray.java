package com.syntaxphoenix.avinity.redis.model;

public class RByteArray extends RArray {

	private byte[] value;
	
	public RByteArray() {
		this(new byte[0]);
	}

	public RByteArray(byte[] value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.BYTE_ARRAY;
	}

	@Override
	public byte[] getValue() {
		return value;
	}

	public RByteArray setValue(byte[] value) {
		this.value = value;
		return this;
	}

	@Override
	public RByteArray clone() {
		return new RByteArray(value.clone());
	}

}
