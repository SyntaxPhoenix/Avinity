package com.syntaxphoenix.avinity.command.type;

import java.util.ArrayList;

import com.syntaxphoenix.avinity.command.StringReader;

final class LongArgument extends NumberArgument<Long> {

    public LongArgument() {
        this(Long.MIN_VALUE);
    }

    public LongArgument(long minimum) {
        this(minimum, Long.MAX_VALUE);
    }

    public LongArgument(long minimum, long maximum) {
        super(minimum, maximum, LONG_STEP);
    }

    public LongArgument(long minimum, long maximum, long step) {
        super(minimum, maximum, step);
    }

    @Override
    public Long read(StringReader reader) throws IllegalArgumentException {
        return reader.parseLong();
    }
    
    @Override
    protected boolean isLower(Long value) {
        return value < minimum;
    }
    
    @Override
    protected boolean isHigher(Long value) {
        return maximum < value;
    }

    @Override
    public void suggest(StringReader remaining, ArrayList<String> suggestions) {
        try {
            long value = remaining.parseLong();
            long min = Long.MAX_VALUE;
            long step = this.step * 3;
            for (long a = step; min > value && a >= 0; a -= this.step) {
                min = value - a;
            }
            if (isLower(min)) {
                min = minimum;
            }
            long max = Long.MIN_VALUE;
            for (long a = step; max < value && a >= 0; a -= this.step) {
                max = value + a;
            }
            if (isHigher(max)) {
                max = maximum;
            }
            for (long a = min; a <= max; a += this.step) {
                if (isLower(a) || isHigher(a)) {
                    continue;
                }
                suggestions.add(Long.toString(a));
            }
        } catch (IllegalArgumentException ignore) {
            suggestions.add("['" + minimum + "'-'" + maximum + "']");
        }
    }

}
