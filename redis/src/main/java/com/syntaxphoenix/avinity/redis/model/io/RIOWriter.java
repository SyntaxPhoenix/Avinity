package com.syntaxphoenix.avinity.redis.model.io;

import java.util.Map.Entry;

import com.syntaxphoenix.avinity.redis.model.*;
import com.syntaxphoenix.avinity.redis.utils.DataSerialization;

import static com.syntaxphoenix.avinity.redis.utils.DataSerialization.*;

import java.util.Objects;

class RIOWriter {

	public byte[] writeNamedModel(String name, RModel model) {
		byte[] nameData = DataSerialization.fromString(name);
		if (nameData.length > Short.MAX_VALUE) {
			throw new IllegalArgumentException("Can't write a name longer than " + Short.MAX_VALUE + " bytes!");
		}
		byte[] data = fromByte((byte) model.getTypeId());
		data = append(data, fromShort((short) nameData.length));
		data = append(data, nameData);
		data = append(data, writeModel(model));
		return data;
	}

	private byte[] writeModel(RModel model) {
		Objects.requireNonNull(model);
		RType type = model.getType();
		switch (type) {
		case BYTE:
			return writeByte((RByte) model);
		case BYTE_ARRAY:
			return writeByteArray((RByteArray) model);
		case SHORT:
			return writeShort((RShort) model);
		case SHORT_ARRAY:
			return writeShortArray((RShortArray) model);
		case INT:
			return writeInt((RInt) model);
		case INT_ARRAY:
			return writeIntArray((RIntArray) model);
		case FLOAT:
			return writeFloat((RFloat) model);
		case FLOAT_ARRAY:
			return writeFloatArray((RFloatArray) model);
		case LONG:
			return writeLong((RLong) model);
		case LONG_ARRAY:
			return writeLongArray((RLongArray) model);
		case DOUBLE:
			return writeDouble((RDouble) model);
		case DOUBLE_ARRAY:
			return writeDoubleArray((RDoubleArray) model);
		case BIG_INTEGER:
			return writeBigInteger((RBigInteger) model);
		case BIG_DECIMAL:
			return writeBigDecimal((RBigDecimal) model);
		case STRING:
			return writeString((RString) model);
		case BOOLEAN:
			return writeBoolean((RBoolean) model);
		case BOOLEAN_ARRAY:
			return writeBooleanArray((RBooleanArray) model);
		case LIST:
			return writeList((RList<?>) model);
		case COMPOUND:
			return writeCompound((RCompound) model);
		case END:
			return writeEnd();
		}
		return null;
	}

	private byte[] writeCompound(RCompound model) {
		byte[] data = new byte[0];
		for (Entry<String, RModel> entry : model.getValue().entrySet()) {
			data = append(data, writeNamedModel(entry.getKey(), entry.getValue()));
		}
		data = append(data, writeEnd());
		return data;
	}

	private byte[] writeList(RList<?> model) {
		RType type = model.getValueType();
		int size = model.size();

		byte[] data = fromByte(type.getByteId());
		data = append(data, fromInt(size));

		if (size == 0) {
			return data;
		}

		for (RModel current : model) {
			data = append(data, writeModel(current));
		}
		return data;
	}

	private byte[] writeBooleanArray(RBooleanArray model) {
		return fromBooleanArray(model.getValue());
	}

	private byte[] writeBoolean(RBoolean model) {
		return fromBoolean(model.getPrimitive());
	}

	private byte[] writeString(RString model) {
		byte[] value = DataSerialization.fromString(model.getValue());
		byte[] data = fromInt(value.length);
		data = append(data, value);
		return data;
	}

	private byte[] writeBigDecimal(RBigDecimal model) {
		byte[] value = fromBigDecimal(model.getValue());
		byte[] data = fromInt(value.length);
		data = append(data, value);
		return data;
	}

	private byte[] writeBigInteger(RBigInteger model) {
		byte[] value = fromBigInteger(model.getValue());
		byte[] data = fromInt(value.length);
		data = append(data, value);
		return data;
	}

	private byte[] writeDoubleArray(RDoubleArray model) {
		byte[] value = fromDoubleArray(model.getValue());
		byte[] data = fromInt(value.length);
		data = append(data, value);
		return data;
	}

	private byte[] writeDouble(RDouble model) {
		return fromDouble(model.getPrimitive());
	}

	private byte[] writeLongArray(RLongArray model) {
		byte[] value = fromLongArray(model.getValue());
		byte[] data = fromInt(value.length);
		data = append(data, value);
		return data;
	}

	private byte[] writeLong(RLong model) {
		return fromLong(model.getPrimitive());
	}

	private byte[] writeFloatArray(RFloatArray model) {
		byte[] value = fromFloatArray(model.getValue());
		byte[] data = fromInt(value.length);
		data = append(data, value);
		return data;
	}

	private byte[] writeFloat(RFloat model) {
		return fromFloat(model.getPrimitive());
	}

	private byte[] writeIntArray(RIntArray model) {
		byte[] value = fromIntArray(model.getValue());
		byte[] data = fromInt(value.length);
		data = append(data, value);
		return data;
	}

	private byte[] writeInt(RInt model) {
		return fromInt(model.getPrimitive());
	}

	private byte[] writeShortArray(RShortArray model) {
		byte[] value = fromShortArray(model.getValue());
		byte[] data = fromInt(value.length);
		data = append(data, value);
		return data;
	}

	private byte[] writeShort(RShort model) {
		return fromShort(model.getPrimitive());
	}

	private byte[] writeByteArray(RByteArray model) {
		byte[] value = fromByteArray(model.getValue());
		byte[] data = fromInt(value.length);
		data = append(data, value);
		return data;
	}

	private byte[] writeByte(RByte model) {
		return fromByte(model.getPrimitive());
	}

	private byte[] writeEnd() {
		return fromByte(RType.END.getByteId());
	}

	private byte[] append(byte[] current, byte[] data) {
		byte[] output = new byte[current.length + data.length];
		System.arraycopy(current, 0, output, 0, current.length);
		System.arraycopy(data, 0, output, current.length, data.length);
		return output;
	}

}
