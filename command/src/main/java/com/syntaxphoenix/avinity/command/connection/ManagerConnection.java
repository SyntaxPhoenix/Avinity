package com.syntaxphoenix.avinity.command.connection;

import java.util.ArrayList;

import com.syntaxphoenix.avinity.command.CommandContext;
import com.syntaxphoenix.avinity.command.CommandContextBuilder;
import com.syntaxphoenix.avinity.command.CommandManager;
import com.syntaxphoenix.avinity.command.IPermission;
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
        int cursor = reader.getCursor();
        String command = reader.hasNext() ? reader.read() : "";
        RootNode<S> rootNode = manager.getOrGlobal(command);
        if (rootNode == null) {
            reader.setCursor(cursor);
            return;
        }
        rootNode.parse(reader, context);
    }

    @Override
    public void suggest(ArrayList<String> suggestions, CommandContext<S> context) {
        if (context.getNode() == null) {
            String remain = context.getRemaining();
            for (String alias : manager.getAliases()) {
                if (!alias.contains(remain)) {
                    continue;
                }
                IPermission permission = manager.get(alias).getPermission();
                if (permission != null && !context.getSource().hasPermission(permission)) {
                    continue;
                }
                suggestions.add(alias);
            }
            suggestions.sort((s1, s2) -> -Boolean.compare(s1.startsWith(remain), s2.startsWith(remain)));
            return;
        }
        nodeSuggest(context.getNode(), context, suggestions);
    }

}
