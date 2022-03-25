package com.syntaxphoenix.avinity.command.connection;

import java.util.ArrayList;
import java.util.Map.Entry;

import com.syntaxphoenix.avinity.command.CommandContext;
import com.syntaxphoenix.avinity.command.CommandContextBuilder;
import com.syntaxphoenix.avinity.command.ISource;
import com.syntaxphoenix.avinity.command.ParsedArgument;
import com.syntaxphoenix.avinity.command.StringReader;
import com.syntaxphoenix.avinity.command.node.Argument;
import com.syntaxphoenix.avinity.command.node.Node;
import com.syntaxphoenix.syntaxapi.utils.java.Strings;

public abstract class AbstractConnection<S extends ISource> {

    public CommandContext<S> parse(S source, String... arguments) {
        return parse(source, buildArguments(arguments));
    }

    public CommandContext<S> parse(S source, String arguments) {
        if (arguments == null) {
            throw new NullPointerException("'arguments' can't be null!");
        }
        return parse(source, new StringReader(arguments.trim()));
    }

    public CommandContext<S> parse(S source, StringReader reader) {
        CommandContextBuilder<S> context = CommandContextBuilder.of(source);
        parse(reader, context);
        return context.build(reader);
    }

    protected abstract void parse(StringReader reader, CommandContextBuilder<S> context);

    protected String buildArguments(final String[] arguments) {
        if (arguments.length == 0) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (int index = 0; index < arguments.length; index++) {
            builder.append(arguments[index]).append(" ");
        }
        return builder.substring(0, builder.length() - 1);
    }

    /*
     * Suggestions
     */

    public abstract void suggest(ArrayList<String> suggestions, CommandContext<S> context);

    // TODO: Respect permissions

    @SuppressWarnings("unchecked")
    protected void nodeSuggest(Node<S> node, CommandContext<S> context, ArrayList<String> suggestions) {
        if (context.isNewArgument()) {
            return;
        }
        Entry<String, Argument<?>>[] entries = node.getArguments().entrySet().toArray(Entry[]::new);
        int opIdx = -1;
        int rIdx = -1;
        int lastIdx = -1;
        int endIndex = entries.length - 1;
        for (int idx = 0; idx < entries.length; idx++) {
            Entry<String, Argument<?>> entry = entries[idx];
            ParsedArgument<?> argument = context.getArgument(entry.getKey());
            if (argument != null) {
                lastIdx = idx;
                continue;
            }
            for (int index = 0; index < entries.length; index++) {
                if (entry.getValue().isOptional()) {
                    opIdx = idx;
                    continue;
                }
                rIdx = idx;
                endIndex = index;
                break;
            }
            if (rIdx != -1) {
                break;
            }
        }
        String remain = parseRemaining(context.getRemaining());
        if (rIdx == -1) {
            String[] children = node.getChildrenNames();
            for (String child : children) {
                if (!remain.isEmpty() && !remain.startsWith(child)) {
                    continue;
                }
                if (child.contains(" ")) {
                    child = '"' + child + '"';
                }
                suggestions.add(child);
            }
        }
        StringReader reader = new StringReader(readyRead(remain));
        if (rIdx != -1) {
            Argument<?> argument = entries[rIdx].getValue();
            argument.getType().suggest(reader, suggestions);
            reader.setCursor(0);
        }
        if (opIdx != -1) {
            Argument<?> argument = entries[opIdx].getValue();
            if (argument.isInRange(endIndex)) {
                argument.getType().suggest(reader, suggestions);
            }
        }
        if (opIdx == -1 && rIdx == -1 && lastIdx != -1) {
            Argument<?> argument = entries[lastIdx].getValue();
            if (argument.isInRange(endIndex)) {
                argument.getType().suggest(
                    new StringReader(argument.getType().printAbstract(context.getArgument(entries[lastIdx].getKey()).getValue())),
                    suggestions);
            }
        }
    }

    protected String readyRead(String remaining) {
        if (remaining.startsWith("\"") && remaining.endsWith("\"")) {
            return remaining;
        }
        if (remaining.startsWith("'") && remaining.endsWith("'")) {
            return remaining;
        }
        return '"' + remaining + '"';
    }

    protected String parseRemaining(String remaining) {
        remaining = remaining.trim();
        if (remaining.startsWith("\"") && !remaining.endsWith("\"")) {
            remaining = remaining.substring(1);
        } else if (!remaining.startsWith("\"") && remaining.endsWith("\"")) {
            remaining = remaining.substring(0, remaining.length());
        }
        if (remaining.startsWith("'") && !remaining.endsWith("'")) {
            remaining = remaining.substring(1);
        } else if (!remaining.startsWith("'") && remaining.endsWith("'")) {
            remaining = remaining.substring(0, remaining.length());
        }
        if (remaining.endsWith(".") && Strings.isDecimal(remaining + "0")) {
            return remaining + "0";
        }
        return remaining;
    }

}
