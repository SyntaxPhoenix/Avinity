package com.syntaxphoenix.avinity.redis.model.io;

class Offset {
	
	private int offset;
	
	public int inc(int amount) {
		int buf = offset;
		offset += amount;
		return buf;
	}
	
	public int cur() {
		return offset;
	}

}
