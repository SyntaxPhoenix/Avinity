package com.syntaxphoenix.avinity.module;

public class ModuleDescriptionException extends ModuleException {

    private static final long serialVersionUID = -1552016173553632562L;

    public ModuleDescriptionException() {
    }

    public ModuleDescriptionException(final String message) {
        super(message);
    }

    public ModuleDescriptionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ModuleDescriptionException(final Throwable cause) {
        super(cause);
    }

    protected ModuleDescriptionException(final String message, final Throwable cause, final boolean enableSuppression,
        final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
