package com.syntaxphoenix.avinity.redis.model.io;

import static com.syntaxphoenix.avinity.redis.utils.DataSerialization.*;

import com.syntaxphoenix.avinity.redis.model.*;

class RIOReader {

	public RNamedModel readNamedModel(byte[] data) {
		if (data == null || data.length == 0)
			return null;
		return readNamedModel(new Offset(), data);
	}

	private RNamedModel readNamedModel(Offset offset, byte[] data) {
		RType type = RType.getById(asByte(range(offset, data, 1)));

		if (type == RType.END) {
			return new RNamedModel("", readModel(offset, type, data));
		}

		String name = readName(offset, data);

		return new RNamedModel(name, readModel(offset, type, data));

	}

	private String readName(Offset offset, byte[] data) {
		return asString(range(offset, data, asShort(range(offset, data, Short.BYTES))));
	}

	private RModel readModel(Offset offset, RType type, byte[] data) {
		switch (type) {
		case BYTE:
			return readByte(offset, data);
		case BYTE_ARRAY:
			return readByteArray(offset, data);
		case SHORT:
			return readShort(offset, data);
		case SHORT_ARRAY:
			return readShortArray(offset, data);
		case INT:
			return readInt(offset, data);
		case INT_ARRAY:
			return readIntArray(offset, data);
		case FLOAT:
			return readFloat(offset, data);
		case FLOAT_ARRAY:
			return readFloatArray(offset, data);
		case LONG:
			return readLong(offset, data);
		case LONG_ARRAY:
			return readLongArray(offset, data);
		case DOUBLE:
			return readDouble(offset, data);
		case DOUBLE_ARRAY:
			return readDoubleArray(offset, data);
		case BIG_INTEGER:
			return readBigInteger(offset, data);
		case BIG_DECIMAL:
			return readBigDecimal(offset, data);
		case STRING:
			return readString(offset, data);
		case BOOLEAN:
			return readBoolean(offset, data);
		case BOOLEAN_ARRAY:
			return readBooleanArray(offset, data);
		case LIST:
			return readList(offset, data);
		case COMPOUND:
			return readCompound(offset, data);
		case END:
			return readEnd(offset, data);
		}
		return null;
	}

	private RModel readCompound(Offset offset, byte[] data) {
		RCompound compound = new RCompound();
		while (true) {
			RNamedModel model = readNamedModel(offset, data);
			if (model == null) {
				throw new IllegalStateException("Model ends in compound?!");
			}
			RModel current = model.getModel();
			if(current.getType() == RType.END) {
				break;
			}
			compound.set(model.getName(), current);
		}
		return compound;
	}

	@SuppressWarnings({
			"rawtypes",
			"unchecked"
	})
	private RModel readList(Offset offset, byte[] data) {
		RType type = RType.getById(asByte(range(offset, data, 1)));
		int size = length(offset, data);
		
		if (size == 0) {
			return new RList<>(type);
		}

		RList list = new RList(type);
		for (int index = 0; index < size; index++) {
			list.add(readModel(offset, type, data));
		}

		return list;
	}

	private RModel readBoolean(Offset offset, byte[] data) {
		return new RBoolean(asBoolean(range(offset, data, 1)));
	}

	private RModel readBooleanArray(Offset offset, byte[] data) {
		return new RBooleanArray(asBooleanArray(range(offset, data, length(offset, data))));
	}

	private RModel readString(Offset offset, byte[] data) {
		return new RString(asString(range(offset, data, length(offset, data))));
	}

	private RModel readBigDecimal(Offset offset, byte[] data) {
		return new RBigDecimal(asBigDecimal(range(offset, data, length(offset, data))));
	}

	private RModel readBigInteger(Offset offset, byte[] data) {
		return new RBigInteger(asBigInteger(range(offset, data, length(offset, data))));
	}

	private RModel readDoubleArray(Offset offset, byte[] data) {
		return new RDoubleArray(asDoubleArray(range(offset, data, length(offset, data))));
	}

	private RModel readDouble(Offset offset, byte[] data) {
		return new RDouble(asDouble(range(offset, data, Long.BYTES)));
	}

	private RModel readLongArray(Offset offset, byte[] data) {
		return new RLongArray(asLongArray(range(offset, data, length(offset, data))));
	}

	private RModel readLong(Offset offset, byte[] data) {
		return new RLong(asLong(range(offset, data, Long.BYTES)));
	}

	private RModel readFloatArray(Offset offset, byte[] data) {
		return new RFloatArray(asFloatArray(range(offset, data, length(offset, data))));
	}

	private RModel readFloat(Offset offset, byte[] data) {
		return new RFloat(asFloat(range(offset, data, Integer.BYTES)));
	}

	private RModel readIntArray(Offset offset, byte[] data) {
		return new RIntArray(asIntArray(range(offset, data, length(offset, data))));
	}

	private RModel readInt(Offset offset, byte[] data) {
		return new RInt(asInt(range(offset, data, Integer.BYTES)));
	}

	private RModel readShortArray(Offset offset, byte[] data) {
		return new RByteArray(asByteArray(range(offset, data, length(offset, data))));
	}

	private RModel readShort(Offset offset, byte[] data) {
		return new RShort(asShort(range(offset, data, Short.BYTES)));
	}

	private RModel readByteArray(Offset offset, byte[] data) {
		return new RByteArray(asByteArray(range(offset, data, length(offset, data))));
	}

	private RModel readByte(Offset offset, byte[] data) {
		return new RByte(asByte(range(offset, data, 1)));
	}

	private RModel readEnd(Offset offset, byte[] data) {
		return REnd.INSTANCE;
	}

	private int length(Offset offset, byte[] data) {
		return asInt(range(offset, data, Integer.BYTES));
	}

	private byte[] range(Offset offset, byte[] data, int length) {
		byte[] output = new byte[length];
		System.arraycopy(data, offset.inc(length), output, 0, length);
		return output;
	}

}
