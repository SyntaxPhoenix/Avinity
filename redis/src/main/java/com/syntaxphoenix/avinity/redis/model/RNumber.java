package com.syntaxphoenix.avinity.redis.model;

public abstract class RNumber extends RModel {
	
	@Override
	public abstract Number getValue();
	
	@Override
	public abstract RNumber clone();

}
