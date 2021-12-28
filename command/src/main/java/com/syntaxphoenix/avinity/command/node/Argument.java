package com.syntaxphoenix.avinity.command.node;

import com.syntaxphoenix.avinity.command.type.IArgumentType;

public final class Argument<T> {

    private final IArgumentType<T> type;
    private final boolean optional;
    private final int start, end;

    Argument(int start, int end, IArgumentType<T> type, boolean optional) {
        this.start = Math.min(start, end);
        this.end = Math.max(start, end);
        this.type = type;
        this.optional = optional;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public IArgumentType<T> getType() {
        return type;
    }

    public boolean isInRange(int index) {
        return start <= index && index <= end;
    }

    public boolean isOptional() {
        return optional;
    }

    public static <E> Argument<E> at(int index, IArgumentType<E> type, boolean optional) {
        return new Argument<>(index, index, type, optional);
    }

    public static <E> Argument<E> between(int start, int end, IArgumentType<E> type) {
        return new Argument<>(start, end, type, true);
    }

}
