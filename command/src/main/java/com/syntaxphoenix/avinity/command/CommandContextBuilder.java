package com.syntaxphoenix.avinity.command;

import java.util.HashMap;
import java.util.HashSet;

import com.syntaxphoenix.avinity.command.node.Node;

public class CommandContextBuilder<S extends ISource> {

    private final HashMap<String, ParsedArgument<?>> arguments = new HashMap<>();
    private final HashSet<IPermission> permissions = new HashSet<>();
    private final S source;

    private Exception exception;

    private Node<S> node;
    private Node<S> parentNode;
    private ICommand<S> command;
    private int parentStart = -1;
    private int start = 0;

    private int depth = 0;

    private CommandContextBuilder(S source) {
        this.source = source;
    }

    public void withNode(Node<S> node, int start) {
        this.depth = 0;
        this.parentStart = start == 0 ? -1 : start;
        this.parentNode = this.node;
        this.node = node;
    }

    public void withStart(int start) {
        this.start = start;
    }

    public void withCommand(ICommand<S> command) {
        this.command = command;
    }

    public <T> void withArgument(ParsedArgument<?> argument) {
        arguments.put(argument.getName(), argument);
        this.depth += 1;
    }

    public <T> void withArgument(String name, T object, int index) {
        arguments.put(name, new ParsedArgument<T>(name, object, index));
        this.depth += 1;
    }

    public void withPermission(IPermission permission) {
        permissions.add(permission);
    }

    public void withException(Exception exception) {
        this.exception = exception;
    }

    public CommandContext<S> build(StringReader reader) {
        return new CommandContext<>(reader, source, node, depth, start, parentNode, parentStart, command, exception,
            permissions.toArray(IPermission[]::new), arguments);
    }

    public static <S extends ISource> CommandContextBuilder<S> of(S source) {
        return new CommandContextBuilder<>(source);
    }

}
