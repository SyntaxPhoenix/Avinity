package com.syntaxphoenix.avinity.module;

public class ModuleAlreadyLoadedException extends ModuleException {
    
    private static final long serialVersionUID = -27785356768110605L;

    public ModuleAlreadyLoadedException() {
        super();
    }

    public ModuleAlreadyLoadedException(String message) {
        super(message);
    }

    public ModuleAlreadyLoadedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleAlreadyLoadedException(Throwable cause) {
        super(cause);
    }

    protected ModuleAlreadyLoadedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
