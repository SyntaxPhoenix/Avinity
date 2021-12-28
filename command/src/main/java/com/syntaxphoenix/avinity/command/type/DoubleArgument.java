package com.syntaxphoenix.avinity.command.type;

import java.util.ArrayList;

import com.syntaxphoenix.avinity.command.StringReader;

final class DoubleArgument extends NumberArgument<Double> {

    public DoubleArgument() {
        this(Double.MIN_VALUE);
    }

    public DoubleArgument(double minimum) {
        this(minimum, Double.MAX_VALUE);
    }

    public DoubleArgument(double minimum, double maximum) {
        super(minimum, maximum, DOUBLE_STEP);
    }

    public DoubleArgument(double minimum, double maximum, double step) {
        super(minimum, maximum, step);
    }

    @Override
    protected Double read(StringReader reader) throws IllegalArgumentException {
        return reader.parseDouble();
    }
    
    @Override
    protected boolean isLower(Double value) {
        return value < minimum;
    }
    
    @Override
    protected boolean isHigher(Double value) {
        return maximum < value;
    }

    @Override
    public void suggest(StringReader remaining, ArrayList<String> suggestions) {
        try {
            double value = remaining.parseDouble();
            double min = Double.MAX_VALUE;
            double step = this.step * 3;
            for (double a = step; min > value && a >= 0; a -= this.step) {
                min = value - a;
            }
            if (isLower(min)) {
                min = minimum;
            }
            double max = Double.MIN_VALUE;
            for (double a = step; max < value && a >= 0; a -= this.step) {
                max = value + a;
            }
            if (isHigher(max)) {
                max = maximum;
            }
            for (double a = min; a <= max; a += this.step) {
                if (isLower(a) || isHigher(a)) {
                    continue;
                }
                suggestions.add(Double.toString(a));
            }
        } catch (IllegalArgumentException ignore) {
            suggestions.add("['" + minimum + "'-'" + maximum + "']");
        }
    }
    
}
