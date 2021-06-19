package com.syntaxphoenix.avinity.redis.stream;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.syntaxphoenix.avinity.redis.utils.Tools;
import com.syntaxphoenix.syntaxapi.event.EventManager;

import redis.clients.jedis.Jedis;

public class RedisStream implements Closeable {

	private final ExecutorService service = Executors.newCachedThreadPool();

	private final RedisStreamListener listener;
	private final Jedis jedis;

	private final HashSet<String> channels = new HashSet<>();

	private boolean closed = false;

	private Future<?> future;

	public RedisStream(Jedis jedis, EventManager manager) {
		this.listener = new RedisStreamListener(manager);
		this.jedis = jedis;
	}

	public boolean subscribe(String channel) {
		boolean state = channels.add(channel);
		if (state && isRunning()) {
			listener.subscribe(Tools.generateKey(channel));
		}
		return state;
	}

	public boolean unsubscribe(String channel) {
		boolean state = channels.remove(channel);
		if (state && isRunning()) {
			listener.unsubscribe(Tools.generateKey(channel));
		}
		return state;
	}

	public boolean hasSubscribed(String channel) {
		return channels.contains(channel);
	}

	public void start() {
		if (isRunning() || channels.isEmpty()) {
			return;
		}
		future = service.submit(() -> {
			jedis.subscribe(listener, channels.stream().map(Tools::generateKey).toArray(size -> new byte[size][]));
		});
	}

	public void stop() {
		if (!isRunning()) {
			return;
		}
		if (future != null) {
			future.cancel(true);
			future = null;
		}
	}

	public boolean isRunning() {
		return future != null && !future.isDone();
	}

	public boolean hasSubscription() {
		return !channels.isEmpty();
	}

	@Override
	public void close() throws IOException {
		if (closed) {
			return;
		}
		closed = true;
		jedis.close();
	}

	public final Jedis getHandle() {
		return jedis;
	}

}
