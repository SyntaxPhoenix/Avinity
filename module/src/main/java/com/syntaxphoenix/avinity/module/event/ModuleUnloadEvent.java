package com.syntaxphoenix.avinity.module.event;

import com.syntaxphoenix.avinity.module.ModuleWrapper;

public class ModuleUnloadEvent extends ModuleEvent {

    public ModuleUnloadEvent(ModuleWrapper<?> wrapper) {
        super(wrapper);
    }
    
}
