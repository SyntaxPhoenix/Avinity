package com.syntaxphoenix.avinity.command;

public enum CommandState {

    SUCCESS,
    PARTIAL,
    FAILED;

    private final ThreadLocal<String[]> aliases = new ThreadLocal<>();

    protected CommandState setAliases(final String... aliases) {
        this.aliases.set(aliases);
        return this;
    }

    public String[] getAliases() {
        return aliases.get();
    }

    public boolean hasConflicts() {
        return aliases.get() != null;
    }

    public void clear() {
        aliases.remove();
    }

}
