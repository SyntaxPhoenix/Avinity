package com.syntaxphoenix.avinity.module;

public class ModuleAlreadyLoadedException extends ModuleException {

    private static final long serialVersionUID = -27785356768110605L;

    public ModuleAlreadyLoadedException() {
    }

    public ModuleAlreadyLoadedException(final String message) {
        super(message);
    }

    public ModuleAlreadyLoadedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ModuleAlreadyLoadedException(final Throwable cause) {
        super(cause);
    }

    protected ModuleAlreadyLoadedException(final String message, final Throwable cause, final boolean enableSuppression,
        final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
