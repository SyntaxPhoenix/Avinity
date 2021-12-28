package com.syntaxphoenix.avinity.command.connection;

import java.util.ArrayList;

import com.syntaxphoenix.avinity.command.CommandContext;
import com.syntaxphoenix.avinity.command.CommandContextBuilder;
import com.syntaxphoenix.avinity.command.CommandManager;
import com.syntaxphoenix.avinity.command.ISource;
import com.syntaxphoenix.avinity.command.StringReader;
import com.syntaxphoenix.avinity.command.node.RootNode;

public final class ManagerConnection<S extends ISource> extends AbstractConnection<S> {

    private final CommandManager<S> manager;

    public ManagerConnection(CommandManager<S> manager) {
        this.manager = manager;
    }

    @Override
    protected void parse(StringReader reader, CommandContextBuilder<S> context) {
        String command = reader.hasNext() ? reader.read() : "";
        RootNode<S> rootNode = manager.getOrGlobal(command);
        if (rootNode == null) {
            return;
        }
        rootNode.parse(reader.skipWhitespace(), context);
    }
    
    // TODO: Respect permissions
    
    @Override
    public void suggest(ArrayList<String> suggestions, CommandContext<S> context) {
        if (context.getNode() == null) {
            String remain = context.getRemaining();
            for (String alias : manager.getAliases()) {
                if (!remain.startsWith(alias)) {
                    continue;
                }
                suggestions.add(alias);
            }
            return;
        }
        nodeSuggest(context.getNode(), context, suggestions);
    }

}
