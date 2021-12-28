package com.syntaxphoenix.avinity.command;

public interface ISource {
    
    default boolean hasPermission(IPermission permission) {
        return hasPermission(permission.id());
    }
    
    boolean hasPermission(String id);

}
