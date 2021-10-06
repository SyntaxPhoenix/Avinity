package com.syntaxphoenix.avinity.module.extension.handler;

import java.util.Optional;

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

    private boolean invalid = false;

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
        if (owner.isEmpty() || invalid) {
            return null;
        }
        Class<?> clazz = ClassHelper.getClass(id, owner.get().getLoader());
        if (clazz == null) {
            invalid = true;
            throw new NullPointerException(String.format("Class '%s' doesn't exist!", id));
        }
        if (!IExtension.class.isAssignableFrom(clazz)) {
            invalid = true;
            throw new IllegalStateException(String.format("Class '%s' doesn't implement IExtension!", id));
        }
        return this.extensionClass.replace(clazz.asSubclass(IExtension.class)).get();
    }

    public Optional<Class<? extends IExtension>> getExtensionAsOptional() {
        try {
            return Optional.ofNullable(getExtension());
        } catch (NullPointerException | IllegalStateException exp) {
            return Optional.empty();
        }
    }

    public IExtension getInstance() {
        if (extension.isPresent()) {
            return extension.get();
        }
        if (extensionClass.isEmpty() || invalid) {
            return null;
        }
        try {
            return extension.replace(InstanceCreator.create(extensionClass.get(), owner.get())).get();
        } catch (Exception e) {
            invalid = true;
            throw new IllegalStateException("Failed to instantiate extension '" + id + "'!", e);
        }
    }

    public Optional<IExtension> getInstanceAsOptional() {
        try {
            return Optional.ofNullable(getInstance());
        } catch (IllegalStateException exp) {
            return Optional.empty();
        }
    }

    public String getId() {
        return id;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public boolean isAssignable(Class<? extends IExtension> clazz) {
        return isInvalid() ? false : getExtensionAsOptional().map(clazz::isAssignableFrom).orElse(false);
    }

}
