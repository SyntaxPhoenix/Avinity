package com.syntaxphoenix.avinity.command;

public final class ParsedArgument<T> {

    private final String name;

    private final T value;
    private final int index;

    public ParsedArgument(String name, T value, int index) {
        this.name = name;
        this.value = value;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

}
