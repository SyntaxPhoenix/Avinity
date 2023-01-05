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
        return parse(source, new StringReader(arguments));
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

    @SuppressWarnings("unchecked")
    protected void nodeSuggest(Node<S> node, CommandContext<S> context, ArrayList<String> suggestions) {
        if (node.getPermission() != null && !context.getSource().hasPermission(node.getPermission()) || context.isAtEnd()) {
            return;
        }
        Entry<String, Argument<?>>[] entries = node.getArguments().entrySet().toArray(Entry[]::new);
        boolean required = false;
        for (int idx = 0; idx < entries.length; idx++) {
            Entry<String, Argument<?>> entry = entries[idx];
            ParsedArgument<?> argument = context.getArgument(entry.getKey());
            if (argument != null || entry.getValue().isOptional()) {
                continue;
            }
            required = true;
        }
        String remain = parseRemaining(context.isParentArgument() ? context.getParentRemaining() : context.getRemaining());
        if (!required) {
            String[] children = node.getChildrenNames();
            for (String child : children) {
                if (!remain.isEmpty() && !child.startsWith(remain)) {
                    continue;
                }
                if (child.contains(" ")) {
                    child = '"' + child + '"';
                }
                suggestions.add(child);
            }
        }
        StringReader reader = new StringReader(readyRead(remain));
        for (int index = 0; index < entries.length; index++) {
            boolean suggested = false;
            for (Entry<String, Argument<?>> entry : entries) {
                Argument<?> argument = entry.getValue();
                if (!argument.isInRange(index)) {
                    continue;
                }
                ParsedArgument<?> parsedArgument = context.getArgument(entry.getKey());
                if(parsedArgument != null) {
                    continue;
                }
                argument.getType().suggest(reader, suggestions);
                reader.setCursor(0);
                suggested = true;
            }
            if(suggested) {
                break;
            }
        }
    }

    protected String readyRead(String remaining) {
        if (remaining.isEmpty()) {
            return remaining;
        }
        if (remaining.startsWith("\"") && remaining.endsWith("\"")) {
            return remaining;
        }
        if (remaining.startsWith("'") && remaining.endsWith("'")) {
            return remaining;
        }
        return '"' + remaining + '"';
    }

    protected String parseRemaining(String remaining) {
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
        if(!(remaining.startsWith("'") || remaining.startsWith("\""))) {
            remaining = remaining.trim();
        }
        if (remaining.endsWith(".") && Strings.isDecimal(remaining + "0")) {
            return remaining + "0";
        }
        return remaining;
    }

}
