package com.syntaxphoenix.avinity.command.test;

import com.syntaxphoenix.avinity.command.ISource;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;

public final class BasicSource implements ISource {

    @Override
    public boolean hasPermission(String id) {
        return true;
    }

    public void info(String message) {
        System.out.println(message);
    }

    public void error(String message) {
        System.err.println(message);
    }

    public void error(Throwable throwable) {
        System.err.println(Exceptions.stackTraceToString(throwable));
    }

}
