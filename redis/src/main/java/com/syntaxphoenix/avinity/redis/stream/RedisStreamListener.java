package com.syntaxphoenix.avinity.redis.stream;

import com.syntaxphoenix.avinity.redis.event.stream.RedisStreamMessageEvent;
import com.syntaxphoenix.avinity.redis.model.io.RIOModel;
import com.syntaxphoenix.avinity.redis.utils.DataSerialization;
import com.syntaxphoenix.syntaxapi.event.EventManager;

import redis.clients.jedis.BinaryJedisPubSub;

class RedisStreamListener extends BinaryJedisPubSub {

	private final EventManager manager;

	public RedisStreamListener(EventManager manager) {
		this.manager = manager;
	}

	@Override
	public void onMessage(byte[] channel, byte[] message) {
		manager.call(new RedisStreamMessageEvent(DataSerialization.asString(channel), RIOModel.MODEL.read(message)));
	}

}
