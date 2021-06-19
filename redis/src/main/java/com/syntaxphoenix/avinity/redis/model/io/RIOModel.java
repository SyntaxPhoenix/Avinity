package com.syntaxphoenix.avinity.redis.model.io;

public final class RIOModel {
	
	public static RIOModel MODEL = new RIOModel();
	
	private final RIOReader reader = new RIOReader();
	private final RIOWriter writer = new RIOWriter();
	
	private RIOModel() { }
	
	public byte[] write(RNamedModel model) {
		return writer.writeNamedModel(model.getName(), model.getModel());
	}
	
	public RNamedModel read(byte[] data) {
		return reader.readNamedModel(data);
	}
	
}
