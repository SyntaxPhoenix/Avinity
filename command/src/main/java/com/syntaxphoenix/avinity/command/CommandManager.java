package com.syntaxphoenix.avinity.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.syntaxphoenix.avinity.command.node.RootNode;

public final class CommandManager<S extends ISource> {

    private final ConcurrentHashMap<String, RootNode<S>> commands = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Alias> aliasMap = new ConcurrentHashMap<>();
    private final Set<String> aliases = Collections.synchronizedSet(new HashSet<>());

    private String global;

    public CommandState register(final RootNode<S> node, final String... aliasArray) {
        CommandState.PARTIAL.clear();
        if (commands.containsKey(node.getName())) {
            return CommandState.FAILED;
        }
        commands.put(node.getName(), node);
        final Alias alias = new Alias(node.getName(), aliasArray);
        if (aliases.contains(node.getName())) {
            aliasMap.get(node.getName()).getAliases().remove(node.getName());
        }
        aliasMap.put(node.getName(), alias); // Main Name always overwrites alias
        aliases.add(node.getName());
        final ArrayList<String> conflicts = new ArrayList<>();
        final String[] actualAliases = alias.getAliases().toArray(String[]::new);
        for (final String current : actualAliases) {
            if (aliases.contains(current)) {
                conflicts.add(current);
                alias.getAliases().remove(current);
                continue;
            }
            aliases.add(current);
            this.aliasMap.put(current, alias);
        }
        return conflicts.isEmpty() ? CommandState.SUCCESS : CommandState.PARTIAL.setAliases(conflicts.toArray(String[]::new));
    }

    public boolean unregister(String name) {
        if (!commands.containsKey(name)) {
            return false;
        }
        commands.remove(name);
        aliases.remove(name);
        for (String alias : aliasMap.remove(name).getAliases()) {
            aliasMap.remove(alias);
            aliases.remove(alias);
        }
        return true;
    }

    public RootNode<S> get(String alias) {
        if (alias == null || alias.isBlank()) {
            return null;
        }
        Alias aliasObj = aliasMap.get(alias.toLowerCase());
        return aliasObj == null ? null : commands.get(aliasObj.getName());
    }

    public RootNode<S> getOrGlobal(String alias) {
        RootNode<S> node = get(alias);
        return node == null ? get(global) : node;
    }
    
    public String[] getAliases() {
        return aliases.toArray(String[]::new);
    }

    public boolean hasName(String name) {
        return commands.containsKey(name);
    }

    public boolean hasAlias(String alias) {
        return aliasMap.containsKey(alias);
    }

    public void setGlobal(String global) {
        this.global = global;
    }

    public String getGlobal() {
        return global;
    }

    public boolean hasGlobal() {
        return global != null;
    }

}
