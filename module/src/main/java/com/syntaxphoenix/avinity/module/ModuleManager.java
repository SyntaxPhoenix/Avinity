package com.syntaxphoenix.avinity.module;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.syntaxphoenix.avinity.module.event.ModuleDisableEvent;
import com.syntaxphoenix.avinity.module.event.ModuleEnableEvent;
import com.syntaxphoenix.avinity.module.event.ModuleResolveEvent;
import com.syntaxphoenix.avinity.module.event.ModuleUnloadEvent;
import com.syntaxphoenix.avinity.module.extension.handler.ExtensionManager;
import com.syntaxphoenix.avinity.module.util.DescriptionParser;
import com.syntaxphoenix.avinity.module.util.InstanceCreator;
import com.syntaxphoenix.avinity.module.util.graph.DependencyGraph;
import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.utils.general.Status;
import com.syntaxphoenix.syntaxapi.utils.java.Arrays;
import com.syntaxphoenix.syntaxapi.utils.java.Streams;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;
import com.syntaxphoenix.syntaxapi.version.Version;

public class ModuleManager<M extends Module> {

    private final ConcurrentHashMap<String, ModuleWrapper<M>> modules = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ModuleClassLoader> classLoaders = new ConcurrentHashMap<>();

    private final ArrayList<File> moduleRepos = new ArrayList<>();

    private final Version version;
    private final ILogger logger;
    private final EventManager eventManager;

    private final HashSet<Object> injections = new HashSet<>();
    private final Class<M> moduleClass;

    private final Container<File> dataLocation = Container.of();

    private final ExtensionManager<M> extensionManager = new ExtensionManager<>(this);

    public ModuleManager(final Class<M> moduleClass, final Version version) {
        this(moduleClass, null, version);
    }

    public ModuleManager(final Class<M> moduleClass, final EventManager eventManager, final Version version) {
        this.moduleClass = Objects.requireNonNull(moduleClass, "Module class can't be null!");
        this.version = Objects.requireNonNull(version, "System version can't be null!");
        this.eventManager = eventManager;
        this.logger = eventManager != null && eventManager.hasLogger() ? eventManager.getLogger() : null;
    }

    /*
     * Getter
     */

    public final Version getVersion() {
        return version;
    }

    public final ILogger getLogger() {
        return logger;
    }

    public final EventManager getEventManager() {
        return eventManager;
    }

    public final Class<M> getModuleType() {
        return moduleClass;
    }

    public final File getDataLocation() {
        return dataLocation.get();
    }

    /*
     * Injection Management
     */

    public Set<Object> getInjections() {
        return Collections.unmodifiableSet(injections);
    }

    public void addInjection(final Object object) {
        if (object == null) {
            return;
        }
        injections.add(object);
    }

    public void removeInjection(final Object object) {
        if (object == null) {
            return;
        }
        injections.remove(object);
    }

    /*
     * Module Managing
     */

    public ModuleManager<M> setDataLocation(File location) {
        if (dataLocation.isPresent()) {
            removeInjection(dataLocation.get());
        }
        dataLocation.replace(location);
        if (location != null) {
            addInjection(location);
        }
        return this;
    }

    public ArrayList<ModuleWrapper<M>> getModules() {
        return new ArrayList<>(modules.values());
    }

    public ArrayList<ModuleWrapper<M>> getModules(final ModuleState state) {
        final ArrayList<ModuleWrapper<M>> modules = new ArrayList<>();
        for (final ModuleWrapper<M> module : getModules()) {
            if (module.getState() == state) {
                modules.add(module);
            }
        }
        return modules;
    }

    public ArrayList<ModuleWrapper<M>> getModules(final ModuleState... states) {
        final ArrayList<ModuleWrapper<M>> modules = new ArrayList<>();
        for (final ModuleWrapper<M> module : getModules()) {
            for (final ModuleState state : states) {
                if (module.getState() == state) {
                    modules.add(module);
                    break;
                }
            }
        }
        return modules;
    }

    public Optional<ModuleWrapper<M>> getModule(final String id) {
        return Optional.ofNullable(modules.get(id));
    }

    public Optional<ModuleWrapper<M>> getModuleForClass(final Class<?> clazz) {
        final ClassLoader loader = clazz.getClassLoader();
        for (final ModuleWrapper<M> module : modules.values()) {
            if (module.getLoader() == loader) {
                return Optional.of(module);
            }
        }
        return Optional.empty();
    }

    public Optional<ModuleClassLoader> getClassLoader(final String id) {
        return Optional.ofNullable(classLoaders.get(id));
    }

    public boolean hasModule(final String id) {
        return modules.containsKey(id);
    }

    public boolean hasModuleState(final String id, final ModuleState state) {
        return hasModule(id) && getModule(id).get().getState() == state;
    }

    public ArrayList<File> getModuleRepos() {
        return new ArrayList<>(moduleRepos);
    }

    protected String idFromFile(final File file) {
        for (final ModuleWrapper<M> module : getModules()) {
            if (module.getFile().equals(file)) {
                return module.getDescription().getId();
            }
        }
        return null;
    }

    /*
     * Module Loading
     */

    public Status loadModules(final File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            final Status status = new Status(0);
            status.done();
            return status;
        }
        final File[] files = folder.listFiles();
        final Status status = new Status(files.length);
        for (final File file : files) {
            if (file.isDirectory()) {
                continue;
            }
            if (!file.getName().endsWith(".jar")) {
                status.skip();
                continue;
            }
            try {
                if (loadModule(file).getState() == ModuleState.UNLOADED) {
                    status.failed();
                    continue;
                }
                status.success();
            } catch (final ModuleException exp) {
                if (logger != null) {
                    logger.log(exp);
                }
                status.failed();
            }
        }
        status.done();
        return status;
    }

    protected ModuleWrapper<M> loadModule(final File file) throws ModuleException {
        final String id = idFromFile(file);
        if (id != null) {
            throw new ModuleAlreadyLoadedException("Module \"" + id + "\" of file \"" + file.getPath() + "\" is already loaded!");
        }
        if (!file.isFile() || !file.getName().endsWith(".jar")) {
            throw new ModuleException("Invalid module \"" + file.getPath() + "\"!");
        }
        ModuleDescription description = null;
        try (JarFile jar = new JarFile(file)) {
            final Enumeration<JarEntry> entries = jar.entries();
            JarEntry extensions = null;
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                if (!"module.json".equalsIgnoreCase(entry.getName())) {
                    if (extensions != null || !"extensions.json".equalsIgnoreCase(entry.getName())) {
                        continue;
                    }
                    extensions = entry;
                    continue;
                }
                description = new DescriptionParser(file, new BufferedReader(new InputStreamReader(jar.getInputStream(entry)))).parse();
            }
            if (extensions != null) {
                extensionManager.loadExtensions(description.getId(), Streams.toString(jar.getInputStream(extensions)));
            }
        } catch (final IOException exp) {
            throw new ModuleException(exp);
        }
        if (description == null) {
            throw new ModuleException("Module doesn't contain a module.json (" + file.getPath() + ")");
        }
        if (modules.containsKey(description.getId())) {
            throw new ModuleAlreadyLoadedException("There is already a module with the id '" + description.getId() + "'");
        }
        final ModuleClassLoader loader = new ModuleClassLoader(this, description, getClass().getClassLoader(), LoadingStrategy.ADM);
        loader.addFile(file);

        final ModuleWrapper<M> wrapper = new ModuleWrapper<>(this, description, file,
            new File(dataLocation.orElse(file.getParentFile()), description.getId()), loader);

        if (!isModuleValid(wrapper)) {
            wrapper.setState(ModuleState.UNLOADED);
            return wrapper;
        }

        extensionManager.injectExtensions(wrapper);

        return wrapper;
    }

    protected boolean isModuleValid(final ModuleWrapper<M> wrapper) {
        final Dependency dependency = wrapper.getDescription().getSystem();
        if (dependency.hasStrictVersion()) {
            return version.isSimilar(dependency.getMinimum());
        }
        if ((dependency.hasMinimum() && version.isLower(dependency.getMinimum()))
            || (dependency.hasMaximum() && version.isHigher(dependency.getMaximum()))) {
            return false;
        }
        return true;
    }

    protected void resolveModules() {
        final ArrayList<ModuleDescription> descriptions = new ArrayList<>();
        for (final ModuleWrapper<M> wrapper : getModules()) {
            descriptions.add(wrapper.getDescription());
        }

        final DependencyGraph graph = new DependencyGraph(descriptions);
        descriptions.clear();
        if (graph.hasDuplicates()) {
            throw new ModuleException("There are some module duplicates: [" + String.join(", ", graph.getDuplicates()) + "]");
        }
        graph.sort();
        if (graph.isCyclic()) {
            throw new ModuleException("Some modules have cyclic dependencies!");
        }
        if (graph.hasNotFound()) {
            throw new ModuleException("There are some dependencies missing: [" + String.join(", ", graph.getNotFound()) + "]");
        }

        final ArrayList<String> sorted = graph.getSorted();
        for (final String id : sorted) {
            final ModuleWrapper<M> wrapper = modules.get(id);
            if (wrapper.getState().isResolved()) {
                continue;
            }
            if (wrapper.getState() != ModuleState.UNLOADED) {
                wrapper.setState(ModuleState.RESOLVED);
            }
            if (eventManager != null) {
                try {
                    eventManager.call(new ModuleResolveEvent(wrapper));
                } catch (final Exception exp) {
                    if (logger != null) {
                        logger.log("Failed to call ModuleResolveEvent for module '" + id + "'!", exp);
                    }
                }
            }
        }
    }

    protected M createModule(final ModuleWrapper<M> wrapper) throws ModuleException {
        final ModuleDescription description = wrapper.getDescription();
        final String classPath = description.getClassPath();

        Class<?> clazz;
        try {
            clazz = wrapper.getLoader().loadClass(classPath);
        } catch (final ClassNotFoundException exp) {
            throw new ModuleException(
                "Error while trying to load module '" + description.getId() + "': The main class '" + classPath + "' doesn't exist!", exp);
        }

        final int modifiers = clazz.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers) || !moduleClass.isAssignableFrom(clazz)) {
            throw new ModuleException(
                "Error while trying to load module '" + description.getId() + "': The main class '" + classPath + "' is not a Module!");
        }

        try {
            return InstanceCreator.create(clazz.asSubclass(moduleClass),
                Arrays.merge(Object[]::new, injections.toArray(), wrapper, eventManager));
        } catch (final Exception exp) {
            throw new ModuleException("Error while trying to load module '" + description.getId() + "': Failed to create module instance!",
                exp);
        }
    }

    public Status enableModules() {
        final ArrayList<ModuleWrapper<M>> wrappers = getModules(ModuleState.RESOLVED, ModuleState.DISABLED);
        final Status status = new Status(wrappers.size());
        for (final ModuleWrapper<M> wrapper : wrappers) {
            final ModuleState state = disableModule(wrapper.getDescription().getId());
            if (state != ModuleState.DISABLED) {
                status.failed();
                continue;
            }
            status.success();
        }
        status.done();
        return status;
    }

    public ModuleState enableModule(final String id) {

        final ModuleWrapper<M> wrapper = modules.get(id);
        if (wrapper == null) {
            throw new ModuleException("Module '" + id + "' doesn't exist!");
        }

        final ModuleDescription description = wrapper.getDescription();
        final ModuleState state = wrapper.getState();
        if (!state.isResolved()) {
            if (logger != null) {
                logger.log("Module '" + description.getId() + "' is not resolved yet!");
            }
            return state;
        }
        if (state == ModuleState.ENABLED) {
            if (logger != null) {
                logger.log("Module '" + description.getId() + "' is already enabled!");
            }
            return state;
        }

        M module = wrapper.getModule();
        if (module == null) {
            try {
                module = createModule(wrapper);
            } catch (final ModuleException exp) {
                wrapper.setState(ModuleState.FAILED);
                if (logger != null) {
                    logger.log(exp);
                }
                return wrapper.getState();
            }
            wrapper.setModule(module);
            module.setWrapper(wrapper);
        }
        try {
            module.enable();
        } catch (final Exception exp) {
            wrapper.setState(ModuleState.FAILED);
            if (logger != null) {
                logger.log(exp);
            }
            return wrapper.getState();
        }

        wrapper.setState(ModuleState.ENABLED);
        if (eventManager != null) {
            try {
                eventManager.call(new ModuleEnableEvent(wrapper));
            } catch (final Exception exp) {
                if (logger != null) {
                    logger.log("Failed to call ModuleEnableEvent for module '" + id + "'!", exp);
                }
            }
        }

        return wrapper.getState();

    }

    public Status disableModules() {
        final ArrayList<ModuleWrapper<M>> wrappers = getModules(ModuleState.ENABLED);
        final Status status = new Status(wrappers.size());
        for (final ModuleWrapper<M> wrapper : wrappers) {
            final ModuleState state = disableModule(wrapper.getDescription().getId());
            if (state != ModuleState.DISABLED) {
                status.failed();
                continue;
            }
            status.success();
        }
        status.done();
        return status;
    }

    public ModuleState disableModule(final String id) {

        final ModuleWrapper<M> wrapper = modules.get(id);
        if (wrapper == null) {
            throw new ModuleException("Module '" + id + "' doesn't exist!");
        }

        final ModuleDescription description = wrapper.getDescription();
        final ModuleState state = wrapper.getState();
        if (!state.isResolved()) {
            if (logger != null) {
                logger.log("Module '" + description.getId() + "' is not resolved yet!");
            }
            return state;
        }
        if (state == ModuleState.DISABLED || state == ModuleState.FAILED) {
            if (logger != null) {
                logger.log("Module '" + description.getId() + "' is already disabled!");
            }
            return state;
        }

        final Module module = wrapper.getModule();
        if (module == null) {
            return state;
        }
        try {
            module.disable();
        } catch (final Exception exp) {
            wrapper.setState(ModuleState.FAILED);
            if (logger != null) {
                logger.log(exp);
            }
            return wrapper.getState();
        }

        wrapper.setState(ModuleState.DISABLED);
        if (eventManager != null) {
            try {
                eventManager.call(new ModuleDisableEvent(wrapper));
            } catch (final Exception exp) {
                if (logger != null) {
                    logger.log("Failed to call ModuleDisableEvent for module '" + id + "'!", exp);
                }
            }
        }

        return wrapper.getState();

    }

    public Status unloadModules() {
        final ArrayList<ModuleWrapper<M>> wrappers = getModules(ModuleState.CREATED, ModuleState.DISABLED, ModuleState.ENABLED,
            ModuleState.FAILED, ModuleState.RESOLVED);
        final Status status = new Status(wrappers.size());
        for (final ModuleWrapper<M> wrapper : wrappers) {
            if (wrapper.getState() == ModuleState.UNLOADED) {
                status.success();
                continue;
            }
            final boolean state = unloadModule(wrapper.getDescription().getId());
            if (!state) {
                status.failed();
                continue;
            }
            status.success();
        }
        status.done();
        return status;
    }

    public boolean unloadModule(final String id) {
        return unloadModule(id, true);
    }

    protected boolean unloadModule(final String id, final boolean unloadDependents) {

        final ModuleWrapper<M> wrapper = modules.get(id);
        if (wrapper == null) {
            return false;
        }

        if (unloadDependents) {
            final ArrayList<String> dependents = getDependents(id);
            while (!dependents.isEmpty()) {
                final String dependent = dependents.remove(0);
                unloadModule(dependent, false);
                dependents.addAll(0, getDependents(dependent));
            }
        }

        final ModuleState state = disableModule(id);
        if (state == ModuleState.ENABLED) {
            return false;
        }

        modules.remove(id, wrapper);
        if (eventManager != null) {
            try {
                eventManager.call(new ModuleUnloadEvent(wrapper));
            } catch (final Exception exp) {
                if (logger != null) {
                    logger.log("Failed to call ModuleUnloadEvent for module '" + id + "'!", exp);
                }
            }
        }

        if (classLoaders.containsKey(id)) {
            final ModuleClassLoader loader = classLoaders.get(id);
            try {
                loader.close();
            } catch (final IOException e) {
                throw new ModuleException("Unable to close ClassLoader of module '" + id + "'!", e);
            }
            classLoaders.remove(id, loader);
            injections.removeIf(object -> object.getClass().getClassLoader() == loader);
        }
        return true;
    }

    protected ArrayList<String> getDependents(final String id) {
        final ArrayList<String> output = new ArrayList<>();
        for (final ModuleWrapper<M> wrapper : getModules()) {
            if (wrapper.getDescription().getId().equals(id)) {
                continue;
            }
            for (final Dependency dependency : wrapper.getDescription().getModuleDependencies()) {
                if (dependency.getId().equals(id)) {
                    output.add(wrapper.getDescription().getId());
                    break;
                }
            }
        }
        return output;
    }

}
