package com.syntaxphoenix.avinity.module.event;

import com.syntaxphoenix.avinity.module.ModuleWrapper;

public class ModuleResolveEvent extends ModuleEvent {

    public ModuleResolveEvent(final ModuleWrapper<?> wrapper) {
        super(wrapper);
    }

}
