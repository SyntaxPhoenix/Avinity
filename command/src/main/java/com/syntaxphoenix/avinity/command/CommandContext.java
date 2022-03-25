package com.syntaxphoenix.avinity.command;

import java.util.HashMap;

import com.syntaxphoenix.avinity.command.node.Node;
import com.syntaxphoenix.syntaxapi.utils.java.Primitives;

public final class CommandContext<S extends ISource> {

    private final HashMap<String, ParsedArgument<?>> arguments;
    private final IPermission[] permissions;
    private final ICommand<S> command;
    private final Node<S> parentNode;
    private final Node<S> node;
    private final S source;

    private final int parentStart;
    private final String remaining;
    private final String parentRemaining;

    private final boolean parentArgument;
    private final boolean whitespaceStart;

    private final int depth;

    private final Exception exception;

    CommandContext(StringReader reader, S source, Node<S> node, int depth, int argumentStart, Node<S> parentNode, int parentStart,
        ICommand<S> command, Exception exception, IPermission[] permissions, HashMap<String, ParsedArgument<?>> arguments) {
        this.node = node;
        this.depth = depth;
        this.source = source;
        this.command = command;
        this.exception = exception;
        this.arguments = arguments;
        this.parentNode = parentNode;
        this.parentStart = parentStart;
        this.permissions = permissions;
        int cursor = reader.getCursor();
        this.parentArgument = cursor == parentStart;
        this.remaining = reader.skipWhitespace().getRemaining();
        this.whitespaceStart = cursor != reader.getCursor();
        if (parentStart == -1) {
            this.parentRemaining = "";
            return;
        }
        cursor = reader.getCursor();
        reader.setCursor(argumentStart);
        this.parentRemaining = reader.skipWhitespace().readUntil(ch -> !Character.isWhitespace(ch));
        reader.setCursor(cursor);
    }

    public int getDepth() {
        return depth;
    }

    public String getRemaining() {
        return remaining;
    }

    public String getParentRemaining() {
        return parentRemaining;
    }

    public Exception getException() {
        return exception;
    }

    public boolean isAtEnd() {
        if (node == null) {
            return true;
        }
        return depth == node.getArguments().size() && (!remaining.isEmpty() || whitespaceStart);
    }

    public boolean isWhitespaceStart() {
        return whitespaceStart;
    }

    public boolean isParentArgument() {
        return parentArgument && !whitespaceStart;
    }

    public int getParentStart() {
        return parentStart;
    }

    public boolean hasException() {
        return exception != null;
    }

    public boolean isPermitted() {
        return hasPermission() == null;
    }

    public IPermission hasPermission() {
        for (IPermission permission : permissions) {
            if (!source.hasPermission(permission)) {
                return permission;
            }
        }
        return null;
    }

    public boolean has(String name) {
        return arguments.containsKey(name);
    }

    public int getArgumentCount() {
        return arguments.size();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name, Class<T> clazz) {
        final ParsedArgument<?> argument = arguments.get(name);
        if (argument == null) {
            throw new IllegalStateException("Argument '" + name + "' is not specified!");
        }
        Class<?> complex = Primitives.fromPrimitive(clazz);
        final Object value = argument.getValue();
        if (complex.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        throw new IllegalStateException(
            "Argument '" + name + "' has type '" + value.getClass().getSimpleName() + "' and not '" + clazz.getSimpleName() + "'!");
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String name, T fallback) {
        final ParsedArgument<?> argument = arguments.get(name);
        if (argument == null) {
            return fallback;
        }
        Class<?> complex = Primitives.fromPrimitive(fallback.getClass());
        final Object value = argument.getValue();
        if (complex.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        throw new IllegalStateException("Argument '" + name + "' has type '" + value.getClass().getSimpleName() + "' and not '"
            + fallback.getClass().getSimpleName() + "'!");
    }

    public ParsedArgument<?> getArgument(String name) {
        return arguments.get(name);
    }

    public Node<S> getNode() {
        return node;
    }

    public Node<S> getParentNode() {
        return parentNode;
    }

    public ICommand<S> getCommand() {
        return command;
    }

    public boolean hasCommand() {
        return command != null;
    }

    public IPermission[] getPermissions() {
        return permissions;
    }

    public S getSource() {
        return source;
    }

}
