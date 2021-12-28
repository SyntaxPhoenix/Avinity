package com.syntaxphoenix.avinity.command.type;

import java.util.ArrayList;

import com.syntaxphoenix.avinity.command.StringReader;

final class ShortArgument extends NumberArgument<Short> {

    public ShortArgument() {
        this(Short.MIN_VALUE);
    }

    public ShortArgument(short minimum) {
        this(minimum, Short.MAX_VALUE);
    }

    public ShortArgument(short minimum, short maximum) {
        super(minimum, maximum, SHORT_STEP);
    }

    public ShortArgument(short minimum, short maximum, short step) {
        super(minimum, maximum, step);
    }

    @Override
    protected Short read(StringReader reader) throws IllegalArgumentException {
        return reader.parseShort();
    }

    @Override
    protected boolean isLower(Short value) {
        return value < minimum;
    }

    @Override
    protected boolean isHigher(Short value) {
        return maximum < value;
    }

    @Override
    public void suggest(StringReader remaining, ArrayList<String> suggestions) {
        try {
            short value = remaining.parseShort();
            short min = Short.MAX_VALUE;
            short step = (short) (this.step * 3);
            for (short a = step; min > value && a >= 0; a -= this.step) {
                min = (short) (value - a);
            }
            if (isLower(min)) {
                min = minimum;
            }
            short max = Short.MIN_VALUE;
            for (short a = step; max < value && a >= 0; a -= this.step) {
                max = (short) (value + a);
            }
            if (isHigher(max)) {
                max = maximum;
            }
            for (short a = min; a <= max; a += this.step) {
                if (isLower(a) || isHigher(a)) {
                    continue;
                }
                suggestions.add(Short.toString(a));
            }
        } catch (IllegalArgumentException ignore) {
            suggestions.add("['" + minimum + "'-'" + maximum + "']");
        }
    }

}
