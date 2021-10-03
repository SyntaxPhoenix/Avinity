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

    public ModuleClassLoader(final ModuleManager<?> manager, final ModuleDescription description, final ClassLoader parent,
        final LoadingStrategy strategy) {
        super(new URL[0], parent);

        this.manager = manager;
        this.description = description;
        this.strategy = strategy;
    }

    @Override
    public void addURL(final URL url) {
        super.addURL(url);
    }

    public void addFile(final File file) {
        try {
            addURL(file.getCanonicalFile().toURI().toURL());
        } catch (final IOException exp) {
            // For now just ignore
        }
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            if (name.startsWith(JAVA_PACKAGE)) {
                return findSystemClass(name);
            }
            if (name.startsWith(MODULE_PACKAGE)) {
                return getParent().loadClass(name);
            }
            final Class<?> loaded = findLoadedClass(name);
            if (loaded != null) {
                return loaded;
            }
            for (final LoadingStrategy.ClassSource source : strategy.getSources()) {
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
                } catch (final ClassNotFoundException ignore) {
                }
                if (clazz != null) {
                    return clazz;
                }
            }
            throw new ClassNotFoundException(name);
        }
    }

    @Override
    public URL getResource(final String name) {
        for (final LoadingStrategy.ClassSource source : strategy.getSources()) {
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
    public Enumeration<URL> getResources(final String name) throws IOException {
        final ArrayList<URL> resources = new ArrayList<>();
        for (final LoadingStrategy.ClassSource source : strategy.getSources()) {
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

    protected Class<?> loadClassFromDependencies(final String className) {
        final ArrayList<Dependency> dependencies = description.getModuleDependencies();
        for (final Dependency dependency : dependencies) {
            final Optional<ModuleClassLoader> option = manager.getClassLoader(dependency.getId());
            if (!option.isPresent() && dependency.isOptional()) {
                continue;
            }
            try {
                return option.orElseThrow(() -> new ModuleDependencyException(
                    "[" + description.getId() + "] Dependency \"" + dependency.getId() + "\" is non existent!")).loadClass(className);
            } catch (final ClassNotFoundException exp) {
            }
        }
        return null;
    }

    protected URL findResourceFromDependencies(final String name) {
        final List<Dependency> dependencies = description.getModuleDependencies();
        for (final Dependency dependency : dependencies) {
            final Optional<ModuleClassLoader> option = manager.getClassLoader(dependency.getId());
            if (!option.isPresent() && dependency.isOptional()) {
                continue;
            }
            final URL url = option.orElseThrow(() -> new ModuleDependencyException(
                "[" + description.getId() + "] Dependency \"" + dependency.getId() + "\" is non existent!")).findResource(name);
            if (Objects.nonNull(url)) {
                return url;
            }
        }
        return null;
    }

    protected Collection<URL> findResourcesFromDependencies(final String name) throws IOException {
        final List<URL> results = new ArrayList<>();
        final List<Dependency> dependencies = description.getModuleDependencies();
        for (final Dependency dependency : dependencies) {
            final Optional<ModuleClassLoader> option = manager.getClassLoader(dependency.getId());
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