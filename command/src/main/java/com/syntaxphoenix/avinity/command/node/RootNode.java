package com.syntaxphoenix.avinity.command.node;

import java.util.LinkedHashMap;

import com.syntaxphoenix.avinity.command.ISource;

public final class RootNode<S extends ISource> extends LiteralNode<S> {

    RootNode(String name, LinkedHashMap<String, Argument<?>> arguments) {
        super(name, arguments);
    }

}
