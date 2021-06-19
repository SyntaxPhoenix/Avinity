package com.syntaxphoenix.avinity.module;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ModuleClassLoader extends URLClassLoader {

    private static final String JAVA_PACKAGE = "java.";
    private static final String MODULE_PACKAGE = "com.syntaxphoenix.framework.module.";

    private final LoadingStrategy strategy;
    private final ModuleDescription description;

    private final ModuleManager<?> manager;

    public ModuleClassLoader(ModuleManager<?> manager, ModuleDescription description, ClassLoader parent, LoadingStrategy strategy) {
        super(new URL[0], parent);

        this.manager = manager;
        this.description = description;
        this.strategy = strategy;
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    public void addFile(File file) {
        try {
            addURL(file.getCanonicalFile().toURI().toURL());
        } catch (IOException exp) {
            // For now just ignore
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            if (name.startsWith(JAVA_PACKAGE)) {
                return findSystemClass(name);
            }
            if (name.startsWith(MODULE_PACKAGE)) {
                return getParent().loadClass(name);
            }
            Class<?> loaded = findLoadedClass(name);
            if (loaded != null) {
                return loaded;
            }
            for (LoadingStrategy.ClassSource source : strategy.getSources()) {
                Class<?> clazz = null;
                try {
                    switch (source) {
                    case APPLICATION:
                        clazz = super.loadClass(name);
                        break;
                    case DEPENDENCY:
                        clazz = loadClassFromDependencies(name);
                        break;
                    case MODULE:
                        clazz = findClass(name);
                        break;
                    }
                } catch (ClassNotFoundException ignore) {
                }
                if (clazz != null) {
                    return clazz;
                }
            }
            throw new ClassNotFoundException(name);
        }
    }

    @Override
    public URL getResource(String name) {
        for (LoadingStrategy.ClassSource source : strategy.getSources()) {
            URL url = null;
            switch (source) {
            case APPLICATION:
                url = super.getResource(name);
                break;
            case DEPENDENCY:
                url = findResourceFromDependencies(name);
                break;
            case MODULE:
                url = findResource(name);
                break;
            }
            if (url != null) {
                return url;
            }
        }
        return null;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        ArrayList<URL> resources = new ArrayList<>();
        for (LoadingStrategy.ClassSource source : strategy.getSources()) {
            switch (source) {
            case APPLICATION:
                if (getParent() != null) {
                    resources.addAll(Collections.list(getParent().getResources(name)));
                }
                break;
            case DEPENDENCY:
                resources.addAll(findResourcesFromDependencies(name));
                break;
            case MODULE:
                resources.addAll(Collections.list(findResources(name)));
                break;
            }
        }
        return Collections.enumeration(resources);
    }

    protected Class<?> loadClassFromDependencies(String className) {
        ArrayList<Dependency> dependencies = description.getModuleDependencies();
        for (Dependency dependency : dependencies) {
            Optional<ModuleClassLoader> option = manager.getClassLoader(dependency.getId());
            if (!option.isPresent() && dependency.isOptional()) {
                continue;
            }
            try {
                return option.orElseThrow(() -> new ModuleDependencyException(
                    "[" + description.getId() + "] Dependency \"" + dependency.getId() + "\" is non existent!")).loadClass(className);
            } catch (ClassNotFoundException exp) {
            }
        }
        return null;
    }

    protected URL findResourceFromDependencies(String name) {
        List<Dependency> dependencies = description.getModuleDependencies();
        for (Dependency dependency : dependencies) {
            Optional<ModuleClassLoader> option = manager.getClassLoader(dependency.getId());
            if (!option.isPresent() && dependency.isOptional()) {
                continue;
            }
            URL url = option.orElseThrow(() -> new ModuleDependencyException(
                "[" + description.getId() + "] Dependency \"" + dependency.getId() + "\" is non existent!")).findResource(name);
            if (Objects.nonNull(url)) {
                return url;
            }
        }
        return null;
    }

    protected Collection<URL> findResourcesFromDependencies(String name) throws IOException {
        List<URL> results = new ArrayList<>();
        List<Dependency> dependencies = description.getModuleDependencies();
        for (Dependency dependency : dependencies) {
            Optional<ModuleClassLoader> option = manager.getClassLoader(dependency.getId());
            if (!option.isPresent() && dependency.isOptional()) {
                continue;
            }
            results
                .addAll(
                    Collections.list(option
                        .orElseThrow(() -> new ModuleDependencyException(
                            "[" + description.getId() + "] Dependency \"" + dependency.getId() + "\" is non existent!"))
                        .findResources(name)));
        }
        return results;
    }

}