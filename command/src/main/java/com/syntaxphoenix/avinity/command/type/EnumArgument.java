package com.syntaxphoenix.avinity.command.type;

import java.util.ArrayList;

import com.syntaxphoenix.avinity.command.StringReader;

final class EnumArgument<T extends Enum<T>> implements IArgumentType<T> {

    private final T[] values;
    private final String[] keys;

    public EnumArgument(Class<T> clazz) {
        values = clazz.getEnumConstants();
        keys = new String[values.length];
        for (int index = 0; index < values.length; index++) {
            keys[index] = values[index].name().toLowerCase().replace('_', ' ');
        }
    }

    @Override
    public T parse(StringReader reader) throws IllegalArgumentException {
        String value = reader.read().toLowerCase().replace('_', ' ');
        for (int index = 0; index < keys.length; index++) {
            if (!value.equals(keys[index])) {
                continue;
            }
            return values[index];
        }
        throw new IllegalArgumentException("Unable to find valid counterpart for '" + value + "'!");
    }

    @Override
    public void suggest(StringReader remaining, ArrayList<String> suggestions) {
        String remain = remaining.read().toLowerCase().replace('_', ' ');
        for (String key : keys) {
            if (!key.startsWith(remain)) {
                continue;
            }
            if (key.contains(" ")) {
                key = '"' + key + '"';
            }
            suggestions.add(key);
        }
    }
    
    @Override
    public String print(T value) {
        String key = keys[value.ordinal()];
        return key.contains(" ") ? '"' + key + '"' : key;
    }

}
