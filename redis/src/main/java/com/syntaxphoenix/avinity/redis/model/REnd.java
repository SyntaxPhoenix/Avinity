package com.syntaxphoenix.avinity.redis.model;

public final class REnd extends RModel {
	
	public static final REnd INSTANCE = new REnd();
	
	private REnd() { }

	@Override
	public final Void getValue() {
		return null;
	}

	@Override
	public final RType getType() {
		return RType.END;
	}

	@Override
	public final REnd clone() {
		return REnd.INSTANCE;
	}

}
