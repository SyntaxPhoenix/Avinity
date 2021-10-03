package com.syntaxphoenix.avinity.module;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import com.syntaxphoenix.avinity.module.util.DependencyVersion;

public class ModuleDescription {

    private final String id;
    private final String classPath;
    private final DependencyVersion version;
    private final Dependency system;
    private final ArrayList<Dependency> moduleDependencies = new ArrayList<>();
    private final ArrayList<Dependency> pluginDependencies = new ArrayList<>();

    private final String description;
    private final ArrayList<String> authors = new ArrayList<>();

    public ModuleDescription(final File file, final String id, final String classPath, final DependencyVersion version,
        final Dependency system, final Dependency[] moduleDependencies, final Dependency[] pluginDependencies, final String description,
        final String[] authors) {
        this.id = id;
        this.classPath = classPath;
        this.version = version;
        this.system = system;
        Collections.addAll(this.moduleDependencies, moduleDependencies);
        Collections.addAll(this.pluginDependencies, pluginDependencies);
        this.description = description;
        Collections.addAll(this.authors, authors);
    }

    public ArrayList<Dependency> getModuleDependencies() {
        return new ArrayList<>(moduleDependencies);
    }

    public ArrayList<Dependency> getPluginDependencies() {
        return new ArrayList<>(pluginDependencies);
    }

    public ArrayList<String> getAuthors() {
        return new ArrayList<>(authors);
    }

    public String getId() {
        return id;
    }

    public String getClassPath() {
        return classPath;
    }

    public String getDescription() {
        return description;
    }

    public DependencyVersion getVersion() {
        return version;
    }

    public Dependency getSystem() {
        return system;
    }

}
