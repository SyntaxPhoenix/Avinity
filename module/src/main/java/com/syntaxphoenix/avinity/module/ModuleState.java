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
     * The module has been unloaded
     */
    UNLOADED,

    /*
     * The module was unable to start
     */
    FAILED_START,

    /*
     * The module was unable to stop
     */
    FAILED_STOP,

    /*
     * The module was unable to load
     */
    FAILED_LOAD;

    public static ModuleState parse(final String value) {
        for (final ModuleState state : ModuleState.values()) {
            if (state.name().equalsIgnoreCase(value)) {
                return state;
            }
        }
        return null;
    }

    public boolean isResolved() {
        return this == ENABLED || this == RESOLVED || this == DISABLED || this == FAILED_START || this == FAILED_STOP;
    }

}
