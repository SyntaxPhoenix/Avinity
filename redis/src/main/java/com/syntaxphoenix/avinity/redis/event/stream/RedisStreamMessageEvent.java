package com.syntaxphoenix.avinity.redis.event.stream;

import com.syntaxphoenix.avinity.redis.event.RedisEvent;
import com.syntaxphoenix.avinity.redis.model.RModel;
import com.syntaxphoenix.avinity.redis.model.io.RNamedModel;

public final class RedisStreamMessageEvent extends RedisEvent {

	private final String channel;
	private final String command;
	private final RModel data;

	public RedisStreamMessageEvent(String channel, RNamedModel message) {
		this.channel = channel;
		this.command = message.getName();
		this.data = message.getModel();
	}

	public final String getChannel() {
		return channel;
	}

	public final String getCommand() {
		return command;
	}

	public final RModel getData() {
		return data;
	}

}
