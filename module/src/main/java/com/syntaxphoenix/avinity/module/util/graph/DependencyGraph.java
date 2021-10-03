package com.syntaxphoenix.avinity.module.util.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.syntaxphoenix.avinity.module.Dependency;
import com.syntaxphoenix.avinity.module.ModuleDescription;

public class DependencyGraph {

    private final HashMap<String, DependencyNode> nodes = new HashMap<>();

    private final HashSet<String> duplicates = new HashSet<>();

    private final HashSet<String> visited = new HashSet<>();
    private final HashSet<String> notFound = new HashSet<>();

    private final ArrayList<String> sorted = new ArrayList<>();

    private boolean cyclic = false;
    private boolean sortedCalled = false;

    public DependencyGraph(final ArrayList<ModuleDescription> descriptions) {
        for (final ModuleDescription description : descriptions) {
            final String id = description.getId();
            if (nodes.containsKey(id)) {
                duplicates.add(id);
                continue;
            }
            nodes.put(id, new DependencyNode(description));
        }
    }

    public void sort() {
        if (sortedCalled) {
            return;
        }
        sortedCalled = true;
        for (final DependencyNode node : nodes.values()) {
            visit(node);
        }
    }

    private void visit(final DependencyNode node) {
        final String id = node.getId();
        if (visited.contains(id)) {
            if (sorted.contains(id)) {
                return;
            }
            cyclic = true;
            return;
        }
        for (final Dependency dependency : node.getDependencies()) {
            final String depId = dependency.getId();
            if (!nodes.containsKey(depId)) {
                if (!dependency.isOptional()) {
                    notFound.add(depId);
                    return;
                }
                continue;
            }
            visit(nodes.get(depId));
        }
        sorted.add(id);
    }

    public boolean isSorted() {
        return sortedCalled;
    }

    public boolean isCyclic() {
        return cyclic;
    }

    public boolean hasDuplicates() {
        return !duplicates.isEmpty();
    }

    public boolean hasNotFound() {
        return !notFound.isEmpty();
    }

    public HashSet<String> getDuplicates() {
        return duplicates;
    }

    public HashSet<String> getNotFound() {
        return notFound;
    }

    public ArrayList<String> getSorted() {
        return sorted;
    }

}
