package com.syntaxphoenix.avinity.module;

import java.io.File;

public class ModuleWrapper<M extends Module> {

    private final File file;
    private final ClassLoader loader;
    private final ModuleDescription description;
    private final ModuleManager<M> manager;

    private M module;
    private ModuleState state = ModuleState.CREATED;

    public ModuleWrapper(ModuleManager<M> manager, ModuleDescription description, File file, ClassLoader loader) {
        this.file = file;
        this.loader = loader;
        this.manager = manager;
        this.description = description;
    }

    final void setState(ModuleState state) {
        this.state = state;
    }

    final void setModule(M module) {
        this.module = module;
    }

    public boolean hasType(Class<? extends Module> clazz) {
        return (clazz != null && module != null) && module.getClass().isAssignableFrom(clazz);
    }

    public ModuleDescription getDescription() {
        return description;
    }

    public File getFile() {
        return file;
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public ModuleManager<M> getManager() {
        return manager;
    }

    public M getModule() {
        return module;
    }

    public ModuleState getState() {
        return state;
    }

}
