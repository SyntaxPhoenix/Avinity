package com.syntaxphoenix.avinity.module;

public enum ModuleState {

    /*
     * We know about the module
     */
    CREATED,

    /*
     * We know about the module and it's dependencies
     */
    RESOLVED,

    /*
     * The module has been enabled
     */
    ENABLED,

    /*
     * The module has been disabled
     */
    DISABLED,

    /*
     * The module was unable to start
     */
    FAILED,

    /*
     * The module was unable to load
     */
    UNLOADED;

    public static ModuleState parse(String value) {
        for (ModuleState state : ModuleState.values()) {
            if (state.name().equalsIgnoreCase(value)) {
                return state;
            }
        }
        return null;
    }
    
    public boolean isResolved() {
        return this == ENABLED || this == RESOLVED || this == DISABLED || this == FAILED;
    }

}
