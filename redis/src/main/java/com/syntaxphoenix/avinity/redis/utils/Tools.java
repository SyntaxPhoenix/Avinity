package com.syntaxphoenix.avinity.redis.utils;

import static com.syntaxphoenix.avinity.redis.utils.DataSerialization.fromString;

import java.util.Map.Entry;

public final class Tools {
	
	private Tools() {};
	
	/*
	 * Array Helper
	 */

	public static byte[][] partition(byte[] args, int length) {
		int size = (int) Math.floor(args.length / (float) length);
		if(args.length % length != 0) {
			size++;
		}
		byte[][] output = new byte[size][length];
		for(int index = 0; index < size; index++) {
			System.arraycopy(args, index * length, output[index], 0, length);
		}
		return output;
	}
	
	/*
	 * Key Helper
	 */

	public static final byte[] SPLIT = fromString(".");

	public static byte[] generateKey(final String path) {
		return fromString(path);
	}

	public static byte[] sectionKey(final byte[] section, final String path) {
		return mergeKeys(section, generateKey(path));
	}

	public static byte[] mergeKeys(final byte[] key0, final byte[] key1) {
		byte[] output = new byte[key0.length + SPLIT.length + key1.length];
		System.arraycopy(key0, 0, output, 0, key0.length);
		System.arraycopy(SPLIT, 0, output, key0.length, SPLIT.length);
		System.arraycopy(key1, 0, output, key0.length + SPLIT.length, key1.length);
		return output;
	}

	/*
	 * Entry Helper
	 */

	public static <K, V> Entry<K, V> entry(K key, V value) {
		return new Entry<K, V>() {
			private final K key0 = key;
			private V value0 = value;

			@Override
			public K getKey() {
				return key0;
			}

			@Override
			public V getValue() {
				return value0;
			}

			@Override
			public V setValue(V value) {
				V buf = value0;
				value0 = value;
				return buf;
			}
		};
	}

}
