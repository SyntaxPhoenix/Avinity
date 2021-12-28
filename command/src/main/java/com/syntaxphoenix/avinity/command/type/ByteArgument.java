package com.syntaxphoenix.avinity.command.type;

import java.util.ArrayList;

import com.syntaxphoenix.avinity.command.StringReader;

final class ByteArgument extends NumberArgument<Byte> {

    public ByteArgument() {
        this(Byte.MIN_VALUE);
    }

    public ByteArgument(byte minimum) {
        this(minimum, Byte.MAX_VALUE);
    }

    public ByteArgument(byte minimum, byte maximum) {
        super(minimum, maximum, BYTE_STEP);
    }

    public ByteArgument(byte minimum, byte maximum, byte step) {
        super(minimum, maximum, step);
    }

    @Override
    protected Byte read(StringReader reader) throws IllegalArgumentException {
        return reader.parseByte();
    }

    @Override
    protected boolean isLower(Byte value) {
        return value < minimum;
    }

    @Override
    protected boolean isHigher(Byte value) {
        return maximum < value;
    }

    @Override
    public void suggest(StringReader remaining, ArrayList<String> suggestions) {
        try {
            byte value = remaining.parseByte();
            byte min = Byte.MAX_VALUE;
            byte step = (byte) (this.step * 3);
            for (byte a = step; min > value && a >= 0; a -= this.step) {
                min = (byte) (value - a);
            }
            if (isLower(min)) {
                min = minimum;
            }
            byte max = Byte.MIN_VALUE;
            for (byte a = step; max < value && a >= 0; a -= this.step) {
                max = (byte) (value + a);
            }
            if (isHigher(max)) {
                max = maximum;
            }
            for (byte a = min; a <= max; a += this.step) {
                if (isLower(a) || isHigher(a)) {
                    continue;
                }
                suggestions.add(Byte.toString(a));
            }
        } catch (IllegalArgumentException ignore) {
            suggestions.add("['" + minimum + "'-'" + maximum + "']");
        }
    }

}
