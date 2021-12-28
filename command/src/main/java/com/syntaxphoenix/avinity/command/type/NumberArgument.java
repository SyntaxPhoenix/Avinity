package com.syntaxphoenix.avinity.command.type;

import com.syntaxphoenix.avinity.command.StringReader;

public abstract class NumberArgument<E extends Number> implements IArgumentType<E> {

    public static final byte BYTE_STEP = 1;
    public static final short SHORT_STEP = 1;
    public static final int INTEGER_STEP = 1;
    public static final long LONG_STEP = 1;
    public static final float FLOAT_STEP = 1;
    public static final double DOUBLE_STEP = 1;

    protected final E minimum;
    protected final E maximum;
    protected final E step;

    public NumberArgument(E minimum, E maximum, E step) {
        this.minimum = minimum;
        this.maximum = maximum;
        this.step = step;
    }

    @Override
    public final E parse(StringReader reader) throws IllegalArgumentException {
        E value = read(reader);
        if (isLower(value)) {
            return minimum;
        }
        if (isHigher(value)) {
            return maximum;
        }
        return value;
    }

    protected abstract E read(StringReader reader) throws IllegalArgumentException;

    protected abstract boolean isLower(E value);

    protected abstract boolean isHigher(E value);

}
