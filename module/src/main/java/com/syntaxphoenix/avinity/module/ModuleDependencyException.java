package com.syntaxphoenix.avinity.module;

public class ModuleDependencyException extends ModuleException {

    private static final long serialVersionUID = 1887271634311681211L;

    public ModuleDependencyException() {
        super();
    }

    public ModuleDependencyException(String message) {
        super(message);
    }

    public ModuleDependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleDependencyException(Throwable cause) {
        super(cause);
    }

    protected ModuleDependencyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
