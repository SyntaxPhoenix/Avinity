package com.syntaxphoenix.avinity.command.type;

import java.util.ArrayList;

import com.syntaxphoenix.avinity.command.StringReader;

final class BooleanArgument implements IArgumentType<Boolean> {

    @Override
    public Boolean parse(StringReader reader) throws IllegalArgumentException {
        return reader.parseBoolean();
    }

    @Override
    public void suggest(StringReader remaining, ArrayList<String> suggestions) {
        String read = remaining.read();
        if (read.startsWith("false") || read.startsWith("off")) {
            suggestions.add("false");
            return;
        }
        if (read.startsWith("true") || read.startsWith("on")) {
            suggestions.add("true");
            return;
        }
    }

}
