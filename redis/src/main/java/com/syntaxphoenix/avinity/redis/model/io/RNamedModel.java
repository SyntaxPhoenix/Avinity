package com.syntaxphoenix.avinity.redis.model.io;

import com.syntaxphoenix.avinity.redis.model.RModel;

public final class RNamedModel {
	
	private final String name;
	private final RModel model;
	
	public RNamedModel(String name, RModel model) {
		this.name = name;
		this.model = model;
	}
	
	public String getName() {
		return name;
	}
	
	public RModel getModel() {
		return model;
	}

}
