package com.syntaxphoenix.avinity.command.node;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.syntaxphoenix.avinity.command.CommandContextBuilder;
import com.syntaxphoenix.avinity.command.ISource;
import com.syntaxphoenix.avinity.command.ParsedArgument;
import com.syntaxphoenix.avinity.command.StringReader;

class LiteralNode<S extends ISource> extends Node<S> {

    LiteralNode(String name, LinkedHashMap<String, Argument<?>> arguments) {
        super(name, arguments);
    }

    @Override
    public void parse(StringReader reader, CommandContextBuilder<S> builder) {
        builder.withNode(this);
        int start = reader.getCursor();
        try {
            if (!arguments.isEmpty()) {
                HashMap<String, ParsedArgument<?>> map = new HashMap<>();
                @SuppressWarnings("unchecked")
                Entry<String, Argument<?>>[] entries = arguments.entrySet().toArray(Entry[]::new);
                for (int index = 0; index < entries.length; index++) {
                    for (Entry<String, Argument<?>> entry : entries) {
                        if (!entry.getValue().isInRange(index) || map.containsKey(entry.getKey())) {
                            continue;
                        }
                        if (!reader.hasNext() && !entry.getValue().isOptional()) {
                            Argument<?> argument = entry.getValue();
                            throw new IllegalArgumentException("Required argument '" + entry.getKey() + "' (Index "
                                + argument.getStart() + "-" + argument.getEnd() + ") for '" + name + "' is not specified!");
                        }
                        try {
                            Object value = entry.getValue().getType().safeParse(reader);
                            if (value == null) {
                                continue;
                            }
                            map.put(entry.getKey(), new ParsedArgument<>(entry.getKey(), value, index));
                            reader.skipWhitespace();
                        } catch (IllegalArgumentException exception) {
                            Argument<?> argument = entry.getValue();
                            if (!argument.isOptional()) {
                                throw new IllegalArgumentException("Required argument '" + entry.getKey() + "' (Index "
                                    + argument.getStart() + "-" + argument.getEnd() + ") for '" + name + "' could not be parsed!",
                                    exception);
                            }
                        }
                    }
                }
                for (Entry<String, Argument<?>> entry : entries) {
                    Argument<?> argument = entry.getValue();
                    if (argument.isOptional()) {
                        continue;
                    }
                    if (!map.containsKey(entry.getKey())) {
                        throw new IllegalArgumentException("Required argument '" + entry.getKey() + "' (Index " + argument.getStart() + "-"
                            + argument.getEnd() + ") for '" + name + "' is not given!");
                    }
                }
                for (String key : map.keySet()) {
                    builder.withArgument(map.get(key));
                }
            }
            if (permission != null) {
                builder.withPermission(permission);
            }
            if (command != null) {
                builder.withCommand(command);
            }
            if (!reader.hasNext()) {
                return;
            }
            String name = reader.read().toLowerCase();
            if (!literals.containsKey(name)) {
                return;
            }
            literals.get(name).parse(reader, builder);
        } catch (IllegalArgumentException exception) {
            builder.withException(exception);
            reader.setCursor(start);
            return;
        }
    }

}
