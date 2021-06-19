package com.syntaxphoenix.avinity.redis.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public final class DataSerialization {

	/*
	 * Serialization from complex Objects
	 */

	public static byte[] fromString(final String value) {
		return value.getBytes(StandardCharsets.UTF_8);
	}

	public static byte[] fromBigDecimal(final BigDecimal value) {
		return fromBigInteger(value.toBigInteger());
	}

	public static byte[] fromBigInteger(final BigInteger value) {
		return value.toByteArray();
	}

	public static byte[] fromDouble(final double value) {
		return fromLong(Double.doubleToLongBits(value));
	}

	public static byte[] fromLong(long value) {
		byte[] result = new byte[8];
		for (int index = 7; index >= 0; index--) {
			result[index] = (byte) (value & 0xFF);
			value >>= 8;
		}
		return result;
	}

	public static byte[] fromFloat(final float value) {
		return fromInt(Float.floatToIntBits(value));
	}

	public static byte[] fromInt(int value) {
		byte[] result = new byte[4];
		for (int index = 3; index >= 0; index--) {
			result[index] = (byte) (value & 0xFF);
			value >>= 8;
		}
		return result;
	}

	public static byte[] fromShort(short value) {
		byte[] result = new byte[2];
		for (int index = 1; index >= 0; index--) {
			result[index] = (byte) (value & 0xFF);
			value >>= 8;
		}
		return result;
	}

	public static byte[] fromByte(final byte value) {
		return new byte[] {
				value
		};
	}

	public static byte[] fromBoolean(final boolean state) {
		return fromByte((byte) (state ? 0 : 1));
	}

	/*
	 * Serialization to complex Object
	 */

	public static String asString(final byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public static BigDecimal asBigDecimal(final byte[] bytes) {
		return new BigDecimal(asBigInteger(bytes));
	}

	public static BigInteger asBigInteger(final byte[] bytes) {
		return new BigInteger(bytes);
	}

	public static double asDouble(final byte[] bytes) {
		return Double.longBitsToDouble(asLong(bytes));
	}

	public static long asLong(final byte[] bytes) {
		long result = 0;
		for (int i = 0; i < Long.BYTES; i++) {
			result <<= 8;
			result |= (bytes[i] & 0xFF);
		}
		return result;
	}

	public static float asFloat(final byte[] bytes) {
		return Float.intBitsToFloat(asInt(bytes));
	}

	public static int asInt(final byte[] bytes) {
		int result = 0;
		for (int i = 0; i < Integer.BYTES; i++) {
			result <<= 8;
			result |= (bytes[i] & 0xFF);
		}
		return result;
	}

	public static short asShort(final byte[] bytes) {
		short result = 0;
		for (int i = 0; i < Short.BYTES; i++) {
			result <<= 8;
			result |= (bytes[i] & 0xFF);
		}
		return result;
	}

	public static byte asByte(final byte[] bytes) {
		return bytes[0];
	}

	public static boolean asBoolean(final byte[] bytes) {
		return asByte(bytes) == 1;
	}

	/*
	 * Serialization from complex Arrays
	 */

	public static byte[] fromDoubleArray(final double[] value) {
		byte[] bytes = new byte[value.length * Long.BYTES];
		byte[] current;
		for (int index = 0; index < value.length; index++) {
			current = fromDouble(value[index]);
			System.arraycopy(current, 0, bytes, index * Long.BYTES, current.length);
		}
		return bytes;
	}

	public static byte[] fromLongArray(final long[] value) {
		byte[] bytes = new byte[value.length * Long.BYTES];
		byte[] current;
		for (int index = 0; index < value.length; index++) {
			current = fromFloat(value[index]);
			System.arraycopy(current, 0, bytes, index * Long.BYTES, current.length);
		}
		return bytes;
	}

	public static byte[] fromFloatArray(final float[] value) {
		byte[] bytes = new byte[value.length * Integer.BYTES];
		byte[] current;
		for (int index = 0; index < value.length; index++) {
			current = fromFloat(value[index]);
			System.arraycopy(current, 0, bytes, index * Integer.BYTES, current.length);
		}
		return bytes;
	}

	public static byte[] fromIntArray(final int[] value) {
		byte[] bytes = new byte[value.length * Integer.BYTES];
		byte[] current;
		for (int index = 0; index < value.length; index++) {
			current = fromInt(value[index]);
			System.arraycopy(current, 0, bytes, index * Integer.BYTES, current.length);
		}
		return bytes;
	}

	public static byte[] fromShortArray(final short[] value) {
		byte[] bytes = new byte[value.length * Short.BYTES];
		byte[] current;
		for (int index = 0; index < value.length; index++) {
			current = fromShort(value[index]);
			System.arraycopy(current, 0, bytes, index * Short.BYTES, current.length);
		}
		return bytes;
	}

	public static byte[] fromByteArray(final byte[] value) {
		return value;
	}

	public static byte[] fromBooleanArray(final boolean[] value) {
		byte[] bytes = new byte[value.length];
		for (int index = 0; index < value.length; index++) {
			bytes[index] = fromBoolean(value[index])[0];
		}
		return bytes;
	}

	/*
	 * Serialization to complex Arrays
	 */
	
	public static double[] asDoubleArray(byte[] bytes) {
		byte[][] part = Tools.partition(bytes, Long.BYTES);
		double[] value = new double[part.length];
		for(int index = 0; index < value.length; index++) {
			value[index] = asDouble(part[index]);
		}
		return value;
	}
	
	public static long[] asLongArray(byte[] bytes) {
		byte[][] part = Tools.partition(bytes, Long.BYTES);
		long[] value = new long[part.length];
		for(int index = 0; index < value.length; index++) {
			value[index] = asLong(part[index]);
		}
		return value;
	}
	
	public static float[] asFloatArray(byte[] bytes) {
		byte[][] part = Tools.partition(bytes, Integer.BYTES);
		float[] value = new float[part.length];
		for(int index = 0; index < value.length; index++) {
			value[index] = asFloat(part[index]);
		}
		return value;
	}
	
	public static int[] asIntArray(byte[] bytes) {
		byte[][] part = Tools.partition(bytes, Integer.BYTES);
		int[] value = new int[part.length];
		for(int index = 0; index < value.length; index++) {
			value[index] = asInt(part[index]);
		}
		return value;
	}
	
	public static short[] asShortArray(byte[] bytes) {
		byte[][] part = Tools.partition(bytes, Short.BYTES);
		short[] value = new short[part.length];
		for(int index = 0; index < value.length; index++) {
			value[index] = asShort(part[index]);
		}
		return value;
	}

	public static byte[] asByteArray(byte[] bytes) {
		return bytes;
	}

	public static boolean[] asBooleanArray(byte[] bytes) {
		boolean[] value = new boolean[bytes.length];
		byte[] dummy = new byte[1];
		for (int index = 0; index < bytes.length; index++) {
			dummy[0] = bytes[index];
			value[index] = asBoolean(dummy);
		}
		return value;
	}

}
