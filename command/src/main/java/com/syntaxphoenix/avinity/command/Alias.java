package com.syntaxphoenix.avinity.command;

import java.util.HashSet;

final class Alias {

    private final String name;
    private final HashSet<String> aliases;

    public Alias(String name, String[] aliases) {
        this.name = name.toLowerCase();
        HashSet<String> aliasSet = new HashSet<>();
        for (String alias : aliases) {
            if (alias == null || alias.isBlank()) {
                continue;
            }
            aliasSet.add(alias.toLowerCase());
        }
        aliasSet.remove(name);
        this.aliases = aliasSet;
    }

    public String getName() {
        return name;
    }

    public HashSet<String> getAliases() {
        return aliases;
    }

    public boolean isAlias(String alias) {
        return name.equals(alias) || aliases.contains(alias);
    }

}
