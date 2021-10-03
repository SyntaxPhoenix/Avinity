package com.syntaxphoenix.avinity.module;

import java.io.File;

import com.syntaxphoenix.syntaxapi.event.EventManager;

public abstract class Module {

    private ModuleWrapper<?> wrapper;

    final void setWrapper(final ModuleWrapper<?> wrapper) {
        this.wrapper = wrapper;
    }

    public final String getId() {
        return wrapper.getDescription().getId();
    }

    public final File getDataLocation() {
        return wrapper.getDataLocation();
    }

    public final ModuleWrapper<?> getWrapper() {
        return wrapper;
    }

    public final ModuleManager<?> getModuleManager() {
        return wrapper.getManager();
    }

    public final EventManager getEventManager() {
        return getModuleManager().getEventManager();
    }

    public void enable() throws Exception {}

    public void disable() throws Exception {}

}
