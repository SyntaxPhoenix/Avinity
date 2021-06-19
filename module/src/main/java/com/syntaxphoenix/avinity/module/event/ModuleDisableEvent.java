package com.syntaxphoenix.avinity.module.event;

import com.syntaxphoenix.avinity.module.ModuleWrapper;

public class ModuleDisableEvent extends ModuleEvent {

    public ModuleDisableEvent(ModuleWrapper<?> wrapper) {
        super(wrapper);
    }
    
}
