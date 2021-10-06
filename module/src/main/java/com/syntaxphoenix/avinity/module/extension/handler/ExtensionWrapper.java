package com.syntaxphoenix.avinity.module.extension.handler;

import com.syntaxphoenix.avinity.module.ModuleWrapper;
import com.syntaxphoenix.avinity.module.extension.IExtension;
import com.syntaxphoenix.avinity.module.util.ClassHelper;
import com.syntaxphoenix.avinity.module.util.InstanceCreator;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public final class ExtensionWrapper {

    private final String id;

    private final Container<Class<? extends IExtension>> extensionClass = Container.of();
    private final Container<IExtension> extension = Container.of();

    private final Container<ModuleWrapper<?>> owner = Container.of();

    public ExtensionWrapper(String id) {
        this.id = id;
    }

    void setOwner(ModuleWrapper<?> owner) {
        this.owner.replace(owner);
    }

    void clear() {
        owner.replace(null);
        extensionClass.replace(null);
        extension.replace(null);
    }

    public ModuleWrapper<?> getOwner() {
        return owner.get();
    }

    public Class<? extends IExtension> getExtension() {
        if (extensionClass.isPresent()) {
            return extensionClass.get();
        }
        if (owner.isEmpty()) {
            return null;
        }
        Class<?> clazz = ClassHelper.getClass(id);
        if (clazz == null) {
            throw new NullPointerException(String.format("Class '%s' doesn't exist!", id));
        }
        if (!IExtension.class.isAssignableFrom(clazz)) {
            throw new IllegalStateException(String.format("Class '%s' doesn't implement IExtension!", id));
        }
        return this.extensionClass.replace(clazz.asSubclass(IExtension.class)).get();
    }

    public IExtension getInstance() {
        if (extension.isPresent()) {
            return extension.get();
        }
        if (extensionClass.isEmpty()) {
            return null;
        }
        try {
            return extension.replace(InstanceCreator.create(extensionClass.get(), owner.get())).get();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to instantiate extension '" + id + "'!", e);
        }
    }

    public String getId() {
        return id;
    }

    public boolean isAssignable(Class<? extends IExtension> clazz) {
        Class<? extends IExtension> current = getExtension();
        return current == null ? false : clazz.isAssignableFrom(current);
    }

}
