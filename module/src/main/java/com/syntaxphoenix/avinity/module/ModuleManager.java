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

import com.syntaxphoenix.avinity.module.event.*;
import com.syntaxphoenix.avinity.module.util.DescriptionParser;
import com.syntaxphoenix.avinity.module.util.InstanceCreator;
import com.syntaxphoenix.avinity.module.util.graph.DependencyGraph;
import com.syntaxphoenix.syntaxapi.event.EventManager;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.utils.general.Status;
import com.syntaxphoenix.syntaxapi.utils.java.Arrays;
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

    public ModuleManager(Class<M> moduleClass, Version version) {
        this(moduleClass, null, version);
    }

    public ModuleManager(Class<M> moduleClass, EventManager eventManager, Version version) {
        this.moduleClass = Objects.requireNonNull(moduleClass, "Module class can't be null!");
        this.version = Objects.requireNonNull(version, "System version can't be null!");
        this.eventManager = eventManager;
        this.logger = (eventManager != null && eventManager.hasLogger()) ? eventManager.getLogger() : null;
    }

    /*
     * Getter
     */

    public final Version getVersion() {
        return version;
    }

    public final EventManager getEventManager() {
        return eventManager;
    }
    
    public final Class<M> getModuleType() {
        return moduleClass;
    }

    /*
     * Injection Management
     */

    public Set<Object> getInjections() {
        return Collections.unmodifiableSet(injections);
    }

    public void addInjection(Object object) {
        if (object == null) {
            return;
        }
        injections.add(object);
    }

    public void removeInjection(Object object) {
        if (object == null) {
            return;
        }
        injections.remove(object);
    }

    /*
     * Module Managing
     */

    public ArrayList<ModuleWrapper<M>> getModules() {
        return new ArrayList<>(modules.values());
    }

    public ArrayList<ModuleWrapper<M>> getModules(ModuleState state) {
        ArrayList<ModuleWrapper<M>> modules = new ArrayList<>();
        for (ModuleWrapper<M> module : getModules()) {
            if (module.getState() == state) {
                modules.add(module);
            }
        }
        return modules;
    }

    public ArrayList<ModuleWrapper<M>> getModules(ModuleState... states) {
        ArrayList<ModuleWrapper<M>> modules = new ArrayList<>();
        for (ModuleWrapper<M> module : getModules()) {
            for (ModuleState state : states) {
                if (module.getState() == state) {
                    modules.add(module);
                    break;
                }
            }
        }
        return modules;
    }

    public Optional<ModuleWrapper<M>> getModule(String id) {
        return Optional.ofNullable(modules.get(id));
    }

    public Optional<ModuleWrapper<M>> getModuleForClass(Class<?> clazz) {
        ClassLoader loader = clazz.getClassLoader();
        for (ModuleWrapper<M> module : modules.values()) {
            if (module.getLoader() == loader) {
                return Optional.of(module);
            }
        }
        return Optional.empty();
    }

    public Optional<ModuleClassLoader> getClassLoader(String id) {
        return Optional.ofNullable(classLoaders.get(id));
    }

    public boolean hasModule(String id) {
        return modules.containsKey(id);
    }

    public boolean hasModuleState(String id, ModuleState state) {
        return hasModule(id) && getModule(id).get().getState() == state;
    }

    public ArrayList<File> getModuleRepos() {
        return new ArrayList<>(moduleRepos);
    }

    protected String idFromFile(File file) {
        for (ModuleWrapper<M> module : getModules()) {
            if (module.getFile().equals(file)) {
                return module.getDescription().getId();
            }
        }
        return null;
    }

    /*
     * Module Loading
     */

    public Status loadModules(File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            Status status = new Status(0);
            status.done();
            return status;
        }
        File[] files = folder.listFiles();
        Status status = new Status(files.length);
        for (File file : files) {
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
            } catch (ModuleException exp) {
                if (logger != null) {
                    logger.log(exp);
                }
                status.failed();
            }
        }
        status.done();
        return status;
    }

    protected ModuleWrapper<M> loadModule(File file) throws ModuleException {
        String id = idFromFile(file);
        if (id != null) {
            throw new ModuleAlreadyLoadedException("Module \"" + id + "\" of file \"" + file.getPath() + "\" is already loaded!");
        }
        if (!file.isFile() || !file.getName().endsWith(".jar")) {
            throw new ModuleException("Invalid module \"" + file.getPath() + "\"!");
        }
        ModuleDescription description = null;
        try (JarFile jar = new JarFile(file)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().equalsIgnoreCase("module.json")) {
                    continue;
                }
                description = new DescriptionParser(file, new BufferedReader(new InputStreamReader(jar.getInputStream(entry)))).parse();
            }
        } catch (IOException exp) {
            throw new ModuleException(exp);
        }
        if (description == null) {
            throw new ModuleException("Module doesn't contain a module.json (" + file.getPath() + ")");
        }
        if (modules.containsKey(description.getId())) {
            throw new ModuleAlreadyLoadedException("There is already a module with the id '" + description.getId() + "'");
        }
        ModuleClassLoader loader = new ModuleClassLoader(this, description, getClass().getClassLoader(), LoadingStrategy.ADM);
        loader.addFile(file);

        ModuleWrapper<M> wrapper = new ModuleWrapper<>(this, description, file, loader);

        if (!isModuleValid(wrapper)) {
            wrapper.setState(ModuleState.UNLOADED);
        }

        return wrapper;
    }

    protected boolean isModuleValid(ModuleWrapper<M> wrapper) {
        Dependency dependency = wrapper.getDescription().getSystem();
        if (dependency.hasStrictVersion()) {
            return version.isSimilar(dependency.getMinimum());
        }
        if (dependency.hasMinimum() && version.isLower(dependency.getMinimum())) {
            return false;
        }
        if (dependency.hasMaximum() && version.isHigher(dependency.getMaximum())) {
            return false;
        }
        return true;
    }

    protected void resolveModules() {
        ArrayList<ModuleDescription> descriptions = new ArrayList<>();
        for (ModuleWrapper<M> wrapper : getModules()) {
            descriptions.add(wrapper.getDescription());
        }

        DependencyGraph graph = new DependencyGraph(descriptions);
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

        ArrayList<String> sorted = graph.getSorted();
        for (String id : sorted) {
            ModuleWrapper<M> wrapper = modules.get(id);
            if (wrapper.getState().isResolved()) {
                continue;
            }
            if (wrapper.getState() != ModuleState.UNLOADED) {
                wrapper.setState(ModuleState.RESOLVED);
            }
            if (eventManager != null) {
                try {
                    eventManager.call(new ModuleResolveEvent(wrapper));
                } catch (Exception exp) {
                    if (logger != null) {
                        logger.log("Failed to call ModuleResolveEvent for module '" + id + "'!", exp);
                    }
                }
            }
        }
    }

    protected M createModule(final ModuleWrapper<M> wrapper) throws ModuleException {
        ModuleDescription description = wrapper.getDescription();
        String classPath = description.getClassPath();

        Class<?> clazz;
        try {
            clazz = wrapper.getLoader().loadClass(classPath);
        } catch (ClassNotFoundException exp) {
            throw new ModuleException(
                "Error while trying to load module '" + description.getId() + "': The main class '" + classPath + "' doesn't exist!", exp);
        }

        int modifiers = clazz.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers) || !moduleClass.isAssignableFrom(clazz)) {
            throw new ModuleException(
                "Error while trying to load module '" + description.getId() + "': The main class '" + classPath + "' is not a Module!");
        }

        try {
            return InstanceCreator.create(clazz.asSubclass(moduleClass),
                Arrays.merge(Object[]::new, injections.toArray(), wrapper, eventManager));
        } catch (Exception exp) {
            throw new ModuleException("Error while trying to load module '" + description.getId() + "': Failed to create module instance!",
                exp);
        }
    }

    public Status enableModules() {
        ArrayList<ModuleWrapper<M>> wrappers = getModules(ModuleState.RESOLVED, ModuleState.DISABLED);
        Status status = new Status(wrappers.size());
        for (ModuleWrapper<M> wrapper : wrappers) {
            ModuleState state = disableModule(wrapper.getDescription().getId());
            if (state != ModuleState.DISABLED) {
                status.failed();
                continue;
            }
            status.success();
        }
        status.done();
        return status;
    }

    public ModuleState enableModule(String id) {

        ModuleWrapper<M> wrapper = modules.get(id);
        if (wrapper == null) {
            throw new ModuleException("Module '" + id + "' doesn't exist!");
        }

        ModuleDescription description = wrapper.getDescription();
        ModuleState state = wrapper.getState();
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
            } catch (ModuleException exp) {
                wrapper.setState(ModuleState.FAILED);
                if (logger != null) {
                    logger.log(exp);
                }
                return wrapper.getState();
            }
        }
        try {
            module.enable();
        } catch (Exception exp) {
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
            } catch (Exception exp) {
                if (logger != null) {
                    logger.log("Failed to call ModuleEnableEvent for module '" + id + "'!", exp);
                }
            }
        }

        return wrapper.getState();

    }

    public Status disableModules() {
        ArrayList<ModuleWrapper<M>> wrappers = getModules(ModuleState.ENABLED);
        Status status = new Status(wrappers.size());
        for (ModuleWrapper<M> wrapper : wrappers) {
            ModuleState state = disableModule(wrapper.getDescription().getId());
            if (state != ModuleState.DISABLED) {
                status.failed();
                continue;
            }
            status.success();
        }
        status.done();
        return status;
    }

    public ModuleState disableModule(String id) {

        ModuleWrapper<M> wrapper = modules.get(id);
        if (wrapper == null) {
            throw new ModuleException("Module '" + id + "' doesn't exist!");
        }

        ModuleDescription description = wrapper.getDescription();
        ModuleState state = wrapper.getState();
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

        Module module = wrapper.getModule();
        if (module == null) {
            return state;
        }
        try {
            module.disable();
        } catch (Exception exp) {
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
            } catch (Exception exp) {
                if (logger != null) {
                    logger.log("Failed to call ModuleDisableEvent for module '" + id + "'!", exp);
                }
            }
        }

        return wrapper.getState();

    }

    public boolean unloadModule(String id) {
        return unloadModule(id, true);
    }

    protected boolean unloadModule(String id, boolean unloadDependents) {

        ModuleWrapper<M> wrapper = modules.get(id);
        if (wrapper == null) {
            return false;
        }

        if (unloadDependents) {
            ArrayList<String> dependents = getDependents(id);
            while (!dependents.isEmpty()) {
                String dependent = dependents.remove(0);
                unloadModule(dependent, false);
                dependents.addAll(0, getDependents(dependent));
            }
        }

        ModuleState state = disableModule(id);
        if (state == ModuleState.ENABLED) {
            return false;
        }

        modules.remove(id, wrapper);
        if (eventManager != null) {
            try {
                eventManager.call(new ModuleUnloadEvent(wrapper));
            } catch (Exception exp) {
                if (logger != null) {
                    logger.log("Failed to call ModuleUnloadEvent for module '" + id + "'!", exp);
                }
            }
        }

        if (classLoaders.containsKey(id)) {
            ModuleClassLoader loader = classLoaders.get(id);
            try {
                loader.close();
            } catch (IOException e) {
                throw new ModuleException("Unable to close ClassLoader of module '" + id + "'!", e);
            }
            classLoaders.remove(id, loader);
            injections.removeIf(object -> object.getClass().getClassLoader() == loader);
        }
        return true;
    }

    protected ArrayList<String> getDependents(String id) {
        ArrayList<String> output = new ArrayList<>();
        for (ModuleWrapper<M> wrapper : getModules()) {
            if (wrapper.getDescription().getId().equals(id)) {
                continue;
            }
            for (Dependency dependency : wrapper.getDescription().getModuleDependencies()) {
                if (dependency.getId().equals(id)) {
                    output.add(wrapper.getDescription().getId());
                    break;
                }
            }
        }
        return output;
    }

}
