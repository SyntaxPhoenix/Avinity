package com.syntaxphoenix.avinity.command.type;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;

import com.syntaxphoenix.avinity.command.StringReader;

public interface IArgumentType<E> {

    public static final IArgumentType<Boolean> BOOLEAN = new BooleanArgument();
    public static final IArgumentType<String> STRING = new StringArgument();
    public static final IArgumentType<Color> COLOR = new ColorArgument();

    public static final NumberArgument<Byte> BYTE = new ByteArgument();
    public static final NumberArgument<Short> SHORT = new ShortArgument();
    public static final NumberArgument<Integer> INTEGER = new IntegerArgument();
    public static final NumberArgument<Long> LONG = new LongArgument();
    public static final NumberArgument<Float> FLOAT = new FloatArgument();
    public static final NumberArgument<Double> DOUBLE = new DoubleArgument();

    public static NumberArgument<Byte> minimum(byte minimum) {
        return new ByteArgument(minimum);
    }

    public static NumberArgument<Short> minimum(short minimum) {
        return new ShortArgument(minimum);
    }

    public static NumberArgument<Integer> minimum(int minimum) {
        return new IntegerArgument(minimum);
    }

    public static NumberArgument<Long> minimum(long minimum) {
        return new LongArgument(minimum);
    }

    public static NumberArgument<Float> minimum(float minimum) {
        return new FloatArgument(minimum);
    }

    public static NumberArgument<Double> minimum(double minimum) {
        return new DoubleArgument(minimum);
    }

    public static NumberArgument<Byte> range(byte minimum, byte maximum) {
        return new ByteArgument(minimum, maximum);
    }

    public static NumberArgument<Short> range(short minimum, short maximum) {
        return new ShortArgument(minimum, maximum);
    }

    public static NumberArgument<Integer> range(int minimum, int maximum) {
        return new IntegerArgument(minimum, maximum);
    }

    public static NumberArgument<Long> range(long minimum, long maximum) {
        return new LongArgument(minimum, maximum);
    }

    public static NumberArgument<Float> range(float minimum, float maximum) {
        return new FloatArgument(minimum, maximum);
    }

    public static NumberArgument<Double> range(double minimum, double maximum) {
        return new DoubleArgument(minimum, maximum);
    }

    public static <T extends Enum<T>> IArgumentType<T> of(Class<T> clazz) {
        return new EnumArgument<>(Objects.requireNonNull(clazz));
    }

    default E safeParse(StringReader reader) throws IllegalArgumentException {
        int cursor = reader.getCursor();
        try {
            E value = parse(reader);
            if (value == null) {
                reader.setCursor(cursor);
                return null;
            }
            return value;
        } catch (IllegalArgumentException passthrough) {
            reader.setCursor(cursor);
            throw passthrough;
        }
    }

    E parse(StringReader reader) throws IllegalArgumentException;
    
    default void suggest(StringReader remaining, ArrayList<String> suggestions) {}
    
    @SuppressWarnings("unchecked")
    default String printAbstract(Object obj) {
        try {
            return print((E) obj);
        } catch(ClassCastException ignore) {
            return "";
        }
    }
    
    default String print(E value) {
        return value.toString();
    }

}
