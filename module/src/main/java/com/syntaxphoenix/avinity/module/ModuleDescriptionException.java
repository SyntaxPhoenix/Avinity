package com.syntaxphoenix.avinity.module;

public class ModuleDescriptionException extends ModuleException {

    private static final long serialVersionUID = -1552016173553632562L;

    public ModuleDescriptionException() {
        super();
    }

    public ModuleDescriptionException(String message) {
        super(message);
    }

    public ModuleDescriptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleDescriptionException(Throwable cause) {
        super(cause);
    }

    protected ModuleDescriptionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
