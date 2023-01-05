package com.syntaxphoenix.avinity.command.node;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.syntaxphoenix.avinity.command.CommandContextBuilder;
import com.syntaxphoenix.avinity.command.ICommand;
import com.syntaxphoenix.avinity.command.IPermission;
import com.syntaxphoenix.avinity.command.ISource;
import com.syntaxphoenix.avinity.command.StringReader;

public abstract class Node<S extends ISource> {

    protected final LinkedHashMap<String, Node<S>> children = new LinkedHashMap<>();
    protected final LinkedHashMap<String, LiteralNode<S>> literals = new LinkedHashMap<>();

    protected final Map<String, Argument<?>> arguments;

    protected final String name;

    protected ICommand<S> command;
    protected IPermission permission;

    public Node(String name) {
        this(name, new HashMap<>());
    }

    public Node(String name, Map<String, Argument<?>> arguments) {
        if (Objects.requireNonNull(name).isBlank()) {
            throw new IllegalArgumentException("Name can't be blank!");
        }
        this.name = name.toLowerCase();
        Integer required = null;
        for(Argument<?> argument : arguments.values()) {
            if(argument.isOptional()) {
               continue; 
            }
            if(required == null) {
                required = argument.getStart();
                continue;
            }
            if(required == argument.getStart()) {
                throw new IllegalArgumentException("Required arguments can't have the same start index!");
            }
        }
        this.arguments = Collections.unmodifiableMap(Objects.requireNonNull(arguments));
    }

    public String getName() {
        return name;
    }

    public String[] getChildrenNames() {
        return children.keySet().toArray(String[]::new);
    }
    
    public boolean hasChild(String name) {
        return children.containsKey(name);
    }
    
    public Node<S> getChild(String name) {
        return children.get(name);
    }

    @SuppressWarnings("unchecked")
    public Node<S>[] getChildren() {
        return children.values().toArray(Node[]::new);
    }

    public Map<String, Argument<?>> getArguments() {
        return arguments;
    }

    public void setCommand(ICommand<S> command) {
        this.command = command;
    }

    public ICommand<S> getCommand() {
        return command;
    }

    public void setPermission(IPermission permission) {
        this.permission = permission;
    }

    public IPermission getPermission() {
        return permission;
    }

    public void add(Node<S> node) {
        if (node instanceof RootNode) {
            return;
        }
        final Node<S> child = children.get(node.getName());
        if (child != null) {
            if (child.command == null) {
                child.command = node.command;
            }
            for (Node<S> target : node.getChildren()) {
                child.add(target);
            }
            return;
        }
        children.put(node.getName(), node);
        if (node instanceof LiteralNode) {
            literals.put(node.getName(), (LiteralNode<S>) node);
            return;
        }
    }

    public void remove(String name) {
        children.remove(name);
        literals.remove(name);
    }

    public abstract void parse(StringReader reader, CommandContextBuilder<S> builder);

}
