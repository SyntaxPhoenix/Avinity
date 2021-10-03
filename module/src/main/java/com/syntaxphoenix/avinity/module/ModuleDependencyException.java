package com.syntaxphoenix.avinity.module;

public class ModuleDependencyException extends ModuleException {

    private static final long serialVersionUID = 1887271634311681211L;

    public ModuleDependencyException() {
    }

    public ModuleDependencyException(final String message) {
        super(message);
    }

    public ModuleDependencyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ModuleDependencyException(final Throwable cause) {
        super(cause);
    }

    protected ModuleDependencyException(final String message, final Throwable cause, final boolean enableSuppression,
        final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
