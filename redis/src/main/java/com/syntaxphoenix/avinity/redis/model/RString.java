package com.syntaxphoenix.avinity.redis.model;

public class RString extends RModel {

	private String value;
	
	public RString() {
		this("");
	}

	public RString(String value) {
		this.value = value;
	}

	@Override
	public final RType getType() {
		return RType.STRING;
	}

	@Override
	public String getValue() {
		return value;
	}

	public RString setValue(String value) {
		this.value = value;
		return this;
	}

	@Override
	public RString clone() {
		return new RString(value);
	}

}
