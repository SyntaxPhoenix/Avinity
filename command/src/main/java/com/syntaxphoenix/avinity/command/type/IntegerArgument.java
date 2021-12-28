package com.syntaxphoenix.avinity.command.type;

import java.util.ArrayList;

import com.syntaxphoenix.avinity.command.StringReader;

final class IntegerArgument extends NumberArgument<Integer> {

    public IntegerArgument() {
        this(Integer.MIN_VALUE);
    }

    public IntegerArgument(int minimum) {
        this(minimum, Integer.MAX_VALUE);
    }

    public IntegerArgument(int minimum, int maximum) {
        super(minimum, maximum, INTEGER_STEP);
    }

    public IntegerArgument(int minimum, int maximum, int step) {
        super(minimum, maximum, step);
    }

    @Override
    public Integer read(StringReader reader) throws IllegalArgumentException {
        return reader.parseInt();
    }
    
    @Override
    protected boolean isLower(Integer value) {
        return value < minimum;
    }
    
    @Override
    protected boolean isHigher(Integer value) {
        return maximum < value;
    }

    @Override
    public void suggest(StringReader remaining, ArrayList<String> suggestions) {
        try {
            int value = remaining.parseInt();
            int min = Integer.MAX_VALUE;
            int step = this.step * 3;
            for (int a = step; min > value && a >= 0; a -= this.step) {
                min = value - a;
            }
            if (isLower(min)) {
                min = minimum;
            }
            int max = Integer.MIN_VALUE;
            for (int a = step; max < value && a >= 0; a -= this.step) {
                max = value + a;
            }
            if (isHigher(max)) {
                max = maximum;
            }
            for (int a = min; a <= max; a += this.step) {
                if (isLower(a) || isHigher(a)) {
                    continue;
                }
                suggestions.add(Integer.toString(a));
            }
        } catch (IllegalArgumentException ignore) {
            System.out.println(ignore.getMessage());
            suggestions.add("['" + minimum + "'-'" + maximum + "']");
        }
    }

}
