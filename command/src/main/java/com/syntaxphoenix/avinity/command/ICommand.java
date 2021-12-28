package com.syntaxphoenix.avinity.command;

@FunctionalInterface
public interface ICommand<S extends ISource> {
    
    int execute(CommandContext<S> context);

}
