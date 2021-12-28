package com.syntaxphoenix.avinity.command.node;

import com.syntaxphoenix.avinity.command.ISource;

public final class Literal<S extends ISource> extends NodeBuilder<Literal<S>, S> {

    private Literal(String name) {
        super(name);
    }

    @Override
    protected Literal<S> instance() {
        return this;
    }

    public LiteralNode<S> build() {
        LiteralNode<S> node = new LiteralNode<>(name, arguments);
        for (NodeBuilder<?, S> builder : nodes) {
            node.add(builder.build());
        }
        node.command = command;
        node.permission = permission;
        return node;
    }

    public static <S extends ISource> Literal<S> of(String name) {
        return new Literal<>(name);
    }

}
