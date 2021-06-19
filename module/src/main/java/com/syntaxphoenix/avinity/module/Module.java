package com.syntaxphoenix.avinity.module;

import com.syntaxphoenix.syntaxapi.event.EventManager;

public abstract class Module {
    
    private ModuleWrapper<?> wrapper;

    final void setWrapper(ModuleWrapper<?> wrapper) {
        this.wrapper = wrapper;
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
