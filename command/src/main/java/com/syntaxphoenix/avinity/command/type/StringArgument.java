package com.syntaxphoenix.avinity.command.type;

import com.syntaxphoenix.avinity.command.StringReader;

final class StringArgument implements IArgumentType<String> {

    @Override
    public String parse(StringReader reader) throws IllegalArgumentException {
        return reader.read();
    }

}
