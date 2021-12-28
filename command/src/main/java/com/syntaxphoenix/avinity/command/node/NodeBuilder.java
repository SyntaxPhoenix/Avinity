package com.syntaxphoenix.avinity.command.node;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.syntaxphoenix.avinity.command.ICommand;
import com.syntaxphoenix.avinity.command.IPermission;
import com.syntaxphoenix.avinity.command.ISource;
import com.syntaxphoenix.avinity.command.IVoidCommand;

public abstract class NodeBuilder<T, S extends ISource> {

    protected final String name;
    
    protected final ArrayList<NodeBuilder<?, S>> nodes = new ArrayList<>();
    protected final LinkedHashMap<String, Argument<?>> arguments = new LinkedHashMap<>();
    
    protected ICommand<S> command;
    protected IPermission permission;
    
    public NodeBuilder(String name) {
        this.name = name;
    }

    protected abstract T instance();

    public T argument(String name, Argument<?> argument) {
        arguments.put(name, argument);
        return instance();
    }

    public T append(NodeBuilder<?, S> builder) {
        if (nodes.contains(builder)) {
            return instance();
        }
        nodes.add(builder);
        return instance();
    }

    public T execute(ICommand<S> command) {
        this.command = command;
        return instance();
    }

    public T execute(IVoidCommand<S> command) {
        this.command = command;
        return instance();
    }

    public T permission(IPermission permission) {
        this.permission = permission;
        return instance();
    }

    public abstract Node<S> build();

}
