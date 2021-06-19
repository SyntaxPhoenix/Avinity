package com.syntaxphoenix.avinity.redis.model;

public abstract class RModel {

	/**
	 * Gets the value of this model.
	 * 
	 * @return the value of this model
	 */
	public abstract Object getValue();

	/**
	 * Returns the type of this model.
	 *
	 * @return the type of this model
	 */
	public abstract RType getType();

	/**
	 * Convenience method for getting the id of this model's type.
	 *
	 * @return the type id
	 */
	public int getTypeId() {
		return getType().getId();
	}

	/**
	 * Returns a exact clone of this model
	 *
	 * @return a exact clone of this model
	 */
	public abstract RModel clone();

	public boolean isNumeric() {
		return getType().isNumeric();
	}

	public boolean isArray() {
		return getType().isArray();
	}

	public boolean isPrimitive() {
		return getType().isPrimitive();
	}

	// MISC

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RModel) {
			RModel model = (RModel) obj;
			return this.getType() == model.getType() && this.getValue().equals(model.getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	@Override
	public String toString() {
		Object value = getValue();
		return value == null ? "null" : value.toString();
	}

}
