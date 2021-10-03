package com.syntaxphoenix.avinity.module.event;

import com.syntaxphoenix.avinity.module.ModuleWrapper;

public class ModuleEnableEvent extends ModuleEvent {

    public ModuleEnableEvent(final ModuleWrapper<?> wrapper) {
        super(wrapper);
    }

}
