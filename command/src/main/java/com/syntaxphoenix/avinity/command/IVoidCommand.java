package com.syntaxphoenix.avinity.command;

@FunctionalInterface
public interface IVoidCommand<S extends ISource> extends ICommand<S> {

    default int execute(CommandContext<S> context) {
        run(context);
        return 0;
    }

    void run(CommandContext<S> context);

}
