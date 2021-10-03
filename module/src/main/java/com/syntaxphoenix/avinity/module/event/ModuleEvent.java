package com.syntaxphoenix.avinity.module.event;

import com.syntaxphoenix.avinity.module.ModuleWrapper;
import com.syntaxphoenix.syntaxapi.event.Event;

public abstract class ModuleEvent extends Event {

    private final ModuleWrapper<?> wrapper;

    public ModuleEvent(final ModuleWrapper<?> wrapper) {
        this.wrapper = wrapper;
    }

    public final ModuleWrapper<?> getWrapper() {
        return wrapper;
    }

}
