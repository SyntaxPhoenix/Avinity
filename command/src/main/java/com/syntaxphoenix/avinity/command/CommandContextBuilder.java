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
    private ICommand<S> command;

    private CommandContextBuilder(S source) {
        this.source = source;
    }
    
    public void withNode(Node<S> node) {
        this.node = node;
    }

    public void withCommand(ICommand<S> command) {
        this.command = command;
    }

    public <T> void withArgument(ParsedArgument<?> argument) {
        System.out.println(argument.getName() + " / " + argument.getIndex());
        arguments.put(argument.getName(), argument);
    }

    public <T> void withArgument(String name, T object, int index) {
        arguments.put(name, new ParsedArgument<T>(name, object, index));
    }

    public void withPermission(IPermission permission) {
        permissions.add(permission);
    }

    public void withException(Exception exception) {
        this.exception = exception;
    }

    public CommandContext<S> build(StringReader reader) {
        return new CommandContext<>(reader, source, node, command, exception, permissions.toArray(IPermission[]::new), arguments);
    }
    
    public static <S extends ISource> CommandContextBuilder<S> of(S source) {
        return new CommandContextBuilder<>(source);
    }

}
