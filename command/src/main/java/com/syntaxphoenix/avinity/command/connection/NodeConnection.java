package com.syntaxphoenix.avinity.command.connection;

import java.util.ArrayList;
import java.util.Objects;

import com.syntaxphoenix.avinity.command.CommandContext;
import com.syntaxphoenix.avinity.command.CommandContextBuilder;
import com.syntaxphoenix.avinity.command.ISource;
import com.syntaxphoenix.avinity.command.StringReader;
import com.syntaxphoenix.avinity.command.node.Node;

public final class NodeConnection<S extends ISource> extends AbstractConnection<S> {

    private final Node<S> node;

    public NodeConnection(Node<S> node) {
        this.node = Objects.requireNonNull(node);
    }
    
    public Node<S> getNode() {
        return node;
    }

    @Override
    protected void parse(StringReader reader, CommandContextBuilder<S> context) {
        node.parse(reader, context);
    }

    @Override
    public void suggest(ArrayList<String> suggestions, CommandContext<S> context) {
        nodeSuggest(context.getNode(), context, suggestions);
    }

}
