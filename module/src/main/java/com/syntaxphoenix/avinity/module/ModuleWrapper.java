package com.syntaxphoenix.avinity.module;

import java.io.File;

public final class ModuleWrapper<M extends Module> {

    private final File file;
    private final ClassLoader loader;
    private final ModuleDescription description;
    private final ModuleManager<M> manager;
    
    private final File dataLocation;

    private M module;
    private ModuleState state = ModuleState.CREATED;

    public ModuleWrapper(final ModuleManager<M> manager, final ModuleDescription description, final File file, final File dataLocation, final ClassLoader loader) {
        this.file = file;
        this.loader = loader;
        this.manager = manager;
        this.description = description;
        this.dataLocation = dataLocation;
    }

    final void setState(final ModuleState state) {
        this.state = state;
    }

    final void setModule(final M module) {
        this.module = module;
    }
    
    public boolean isFromModule(final Class<?> clazz) {
        return loader == clazz.getClassLoader();
    }

    public boolean hasType(final Class<? extends Module> clazz) {
        return clazz != null && module != null && module.getClass().isAssignableFrom(clazz);
    }
    
    public String getId() {
        return description.getId();
        }
    
    public File getDataLocation() {
        return dataLocation;
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
