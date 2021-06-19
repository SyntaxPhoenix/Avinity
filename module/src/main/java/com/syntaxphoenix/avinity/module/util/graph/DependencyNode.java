package com.syntaxphoenix.avinity.module.util.graph;

import com.syntaxphoenix.avinity.module.Dependency;
import com.syntaxphoenix.avinity.module.ModuleDescription;

public class DependencyNode {
    
    private final String id;
    private final Dependency[] dependencies;
    
    public DependencyNode(ModuleDescription description) {
        this.id = description.getId();
        this.dependencies = description.getModuleDependencies().toArray(new Dependency[0]);
    }
    
    public String getId() {
        return id;
    }
    
    public Dependency[] getDependencies() {
        return dependencies;
    }

}
