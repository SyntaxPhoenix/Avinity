package com.syntaxphoenix.avinity.command.node;

import com.syntaxphoenix.avinity.command.ISource;

public final class Root<S extends ISource> extends NodeBuilder<Root<S>, S> {

    private Root(String name) {
        super(name);
    }

    @Override
    protected Root<S> instance() {
        return this;
    }

    public RootNode<S> build() {
        RootNode<S> node = new RootNode<S>(name, arguments);
        for (NodeBuilder<?, S> builder : nodes) {
            node.add(builder.build());
        }
        node.command = command;
        node.permission = permission;
        return node;
    }

    public static <S extends ISource> Root<S> of(String name) {
        return new Root<>(name);
    }

}
