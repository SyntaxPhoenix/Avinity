package com.syntaxphoenix.avinity.module.event;

import com.syntaxphoenix.avinity.module.ModuleWrapper;

public class ModuleUnloadEvent extends ModuleEvent {

    public ModuleUnloadEvent(final ModuleWrapper<?> wrapper) {
        super(wrapper);
    }

}
