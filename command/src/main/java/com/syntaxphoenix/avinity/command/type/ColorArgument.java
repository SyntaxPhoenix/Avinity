package com.syntaxphoenix.avinity.command.type;

import java.awt.Color;

import com.syntaxphoenix.avinity.command.StringReader;

final class ColorArgument implements IArgumentType<Color> {

    @Override
    public Color parse(StringReader reader) throws IllegalArgumentException {
        return reader.parseColor();
    }

}
