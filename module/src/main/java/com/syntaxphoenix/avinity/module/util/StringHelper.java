package com.syntaxphoenix.avinity.module.util;

import java.util.HashMap;
import java.util.Map.Entry;

import com.syntaxphoenix.avinity.module.util.graph.MissingType;

public final class StringHelper {

    private StringHelper() {}

    public static String toString(HashMap<String, MissingType> missing) {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, MissingType> entry : missing.entrySet()) {
            builder.append(entry.getKey());
            switch (entry.getValue()) {
            case CYCLIC:
                builder.append(" is cyclic");
                break;
            case DUPLICATE:
                builder.append(" is duplicated");
                break;
            case MISSING_DEPENDENCIES:
                builder.append(" has missing dependencies");
                break;
            case NOT_FOUND:
                builder.append(" is missing");
                break;
            }
            builder.append(", ");
        }
        return builder.substring(0, builder.length() - 2);
    }

}
