package com.syntaxphoenix.avinity.redis.model;

import com.syntaxphoenix.syntaxapi.reflection.Reflect;

public enum RType {

	END(false, false, false, REnd.class),
	
	STRING(true, false, false, RString.class),
	BOOLEAN(true, false, false, RBoolean.class),

	BYTE(true, true, false, RByte.class),
	SHORT(true, true, false, RShort.class),
	INT(true, true, false, RInt.class),
	FLOAT(true, true, false, RFloat.class),
	LONG(true, true, false, RLong.class),
	DOUBLE(true, true, false, RDouble.class),
	BIG_INTEGER(true, true, false, RBigInteger.class),
	BIG_DECIMAL(true, true, false, RBigDecimal.class),
	
	BOOLEAN_ARRAY(false, false, true, RBooleanArray.class),
	BYTE_ARRAY(false, false, true, RByteArray.class),
	SHORT_ARRAY(false, false, true, RShortArray.class),
	INT_ARRAY(false, false, true, RIntArray.class),
	FLOAT_ARRAY(false, false, true, RFloatArray.class),
	LONG_ARRAY(false, false, true, RLongArray.class),
	DOUBLE_ARRAY(false, false, true, RDoubleArray.class),

	LIST(false, false, false, RList.class),
	COMPOUND(false, false, false, RCompound.class);

	private final boolean primitive;
	private final boolean numeric;
	private final boolean array;

	private final byte id;
	private final Reflect owner;

	private <E extends RModel> RType(boolean primitive, boolean numeric, boolean array, Class<E> owner) {
		this.primitive = primitive;
		this.numeric = numeric;
		this.array = array;
		this.id = (byte) ordinal();
		this.owner = new Reflect(owner);
	}

	/**
	 * Returns a new instance of the type
	 * 
	 * @return the new instance
	 */
	public RModel init() {
		return (RModel) owner.init();
	}

	/**
	 * Returns the type with the given id.
	 *
	 * @param id the id
	 * @return the type
	 */
	public static RType getById(int id) {
		return values()[id];
	}

	/**
	 * <p>
	 * Returns the id of this tag type.
	 * </p>
	 * <p>
	 * Although this method is currently equivalent to {@link #ordinal()}, it should
	 * always be used in its stead, since it is not guaranteed that this behavior
	 * will remain consistent.
	 * </p>
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * <p>
	 * Returns the id of this tag type.
	 * </p>
	 * <p>
	 * Although this method is currently equivalent to {@link #ordinal()}, it should
	 * always be used in its stead, since it is not guaranteed that this behavior
	 * will remain consistent.
	 * </p>
	 *
	 * @return the id
	 */
	public byte getByteId() {
		return id;
	}

	/**
	 * Return the type with the given owning class
	 * 
	 * @param clazz the owning class
	 * @return the type or null
	 */
	public static RType getByClass(Class<? extends RModel> clazz) {
		for (RType type : values()) {
			if (type.getOwningClass().equals(clazz)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * Returns the owning class of this type.
	 *
	 * @return the owning class
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends RModel> getOwningClass() {
		return (Class<? extends RModel>) owner.getOwner();
	}

	/**
	 * Returns the owning class of this type.
	 *
	 * @return the owning class
	 */
	public Reflect getOwner() {
		return owner;
	}

	/**
	 * 
	 * @return the name
	 */
	@Override
	public String toString() {
		return name();
	}

	/**
	 * <p>
	 * Returns whether this tag type is numeric.
	 * </p>
	 * <p>
	 * All tag types with payloads that are representable as a {@link Number} are
	 * compliant with this definition.
	 * </p>
	 *
	 * @return whether this type is numeric
	 */
	public boolean isNumeric() {
		return numeric;
	}

	/**
	 * Returns whether this tag type is primitive.
	 *
	 * @return whether this type is numeric
	 */
	public boolean isPrimitive() {
		return primitive;
	}

	/**
	 * Returns whether this tag type is is an array type such as {@link RByteArray}
	 * or {@link RIntArray}.
	 *
	 * @return whether this type is an array type
	 */
	public boolean isArray() {
		return array;
	}

}
