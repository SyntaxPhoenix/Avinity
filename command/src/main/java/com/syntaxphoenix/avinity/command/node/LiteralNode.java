package com.syntaxphoenix.avinity.command.node;

import java.util.ArrayList;
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
                ArrayList<String> argumentNames = new ArrayList<>();
                @SuppressWarnings("unchecked")
                Entry<String, Argument<?>>[] entries = arguments.entrySet().toArray(Entry[]::new);
                for (int index = 0; index < entries.length; index++) {
                    for (Entry<String, Argument<?>> entry : entries) {
                        if (!entry.getValue().isInRange(index) || argumentNames.contains(entry.getKey())) {
                            continue;
                        }
                        if (!reader.hasNext() && !entry.getValue().isOptional()) {
                            Argument<?> argument = entry.getValue();
                            throw new IllegalArgumentException("Required argument '" + entry.getKey() + "' (Index " + argument.getStart()
                                + "-" + argument.getEnd() + ") for '" + name + "' is not specified!");
                        }
                        try {
                            Object value = entry.getValue().getType().safeParse(reader);
                            if (value == null) {
                                continue;
                            }
                            argumentNames.add(entry.getKey());
                            builder.withArgument(new ParsedArgument<>(entry.getKey(), value, index));
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
            int cursor = reader.getCursor();
            String name = reader.skipWhitespace().read().toLowerCase();
            if (!literals.containsKey(name)) {
                reader.setCursor(cursor);
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
