package com.syntaxphoenix.avinity.module;

public class ModuleException extends RuntimeException {

    private static final long serialVersionUID = 633928614833678366L;

    public ModuleException() {
    }

    public ModuleException(final String message) {
        super(message);
    }

    public ModuleException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ModuleException(final Throwable cause) {
        super(cause);
    }

    protected ModuleException(final String message, final Throwable cause, final boolean enableSuppression,
        final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
