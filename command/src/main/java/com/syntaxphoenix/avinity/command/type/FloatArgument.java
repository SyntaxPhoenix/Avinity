package com.syntaxphoenix.avinity.command.type;

import java.util.ArrayList;

import com.syntaxphoenix.avinity.command.StringReader;

final class FloatArgument extends NumberArgument<Float> {

    public FloatArgument() {
        this(Float.MIN_VALUE);
    }

    public FloatArgument(float minimum) {
        this(minimum, Float.MAX_VALUE);
    }

    public FloatArgument(float minimum, float maximum) {
        super(minimum, maximum, FLOAT_STEP);
    }

    public FloatArgument(float minimum, float maximum, float step) {
        super(minimum, maximum, step);
    }

    @Override
    protected Float read(StringReader reader) throws IllegalArgumentException {
        return reader.parseFloat();
    }
    
    @Override
    protected boolean isLower(Float value) {
        return value < minimum;
    }
    
    @Override
    protected boolean isHigher(Float value) {
        return maximum < value;
    }

    @Override
    public void suggest(StringReader remaining, ArrayList<String> suggestions) {
        try {
            float value = remaining.parseFloat();
            float min = Float.MAX_VALUE;
            float step = this.step * 3;
            for (float a = step; min > value && a >= 0; a -= this.step) {
                min = value - a;
            }
            if (isLower(min)) {
                min = minimum;
            }
            float max = Float.MIN_VALUE;
            for (float a = step; max < value && a >= 0; a -= this.step) {
                max = value + a;
            }
            if (isHigher(max)) {
                max = maximum;
            }
            for (float a = min; a <= max; a += this.step) {
                if (isLower(a) || isHigher(a)) {
                    continue;
                }
                suggestions.add(Float.toString(a));
            }
        } catch (IllegalArgumentException ignore) {
            suggestions.add("['" + minimum + "'-'" + maximum + "']");
        }
    }
    
}
