package com.syntaxphoenix.avinity.module.util.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.syntaxphoenix.avinity.module.Dependency;
import com.syntaxphoenix.avinity.module.ModuleDescription;

public class DependencyGraph {

    private static final Function<String, ArrayList<DependencyNode>> FUNCTION = ignore -> new ArrayList<>();

    private final HashMap<String, DependencyNode> nodes = new HashMap<>();
    private final HashMap<String, ArrayList<DependencyNode>> unloaded = new HashMap<>();

    private final HashSet<String> duplicates = new HashSet<>();
    private final HashSet<String> cyclics = new HashSet<>();

    private final HashSet<String> visited = new HashSet<>();
    private final HashSet<String> notFound = new HashSet<>();

    private final ArrayList<String> sorted = new ArrayList<>();

    private final HashSet<String> dontLoad = new HashSet<>();

    private boolean cyclic = false;
    private boolean sortedCalled = false;

    public DependencyGraph(final ArrayList<ModuleDescription> descriptions) {
        for (final ModuleDescription description : descriptions) {
            final String id = description.getId();
            if (nodes.containsKey(id)) {
                duplicates.add(id);
                unloaded.computeIfAbsent(id, FUNCTION).add(new DependencyNode(description));
                continue;
            }
            nodes.put(id, new DependencyNode(description));
        }
        for (final String id : duplicates) {
            unloaded.get(id).add(nodes.remove(id));
        }
    }

    public void sort() {
        if (sortedCalled) {
            return;
        }
        sortedCalled = true;
        dontLoad.add("");
        while (!dontLoad.isEmpty()) {
            dontLoad.clear();
            for (final DependencyNode node : nodes.values()) {
                visit(node);
            }
            for (final String id : dontLoad) {
                unloaded.computeIfAbsent(id, FUNCTION).add(nodes.remove(id));
            }
            sorted.clear();
            visited.clear();
        }
    }

    private void visit(final DependencyNode node) {
        final String id = node.getId();
        if (dontLoad.contains(id)) {
            return;
        }
        if (visited.contains(id)) {
            if (sorted.contains(id)) {
                return;
            }
            cyclic = true;
            cyclics.add(id);
            dontLoad.add(id);
            return;
        }
        for (final Dependency dependency : node.getDependencies()) {
            final String depId = dependency.getId();
            if (!nodes.containsKey(depId)) {
                if (!dependency.isOptional()) {
                    notFound.add(depId);
                    dontLoad.add(id);
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

    public boolean hasUnloaded() {
        return !unloaded.isEmpty();
    }

    public Set<String> getUnloaded() {
        return unloaded.keySet();
    }

    public HashMap<String, MissingType> getMissing(String id) {
        ArrayList<DependencyNode> nodes = unloaded.get(id);
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }
        HashMap<String, MissingType> missing = new HashMap<>();
        for (final DependencyNode node : nodes) {
            for (final Dependency dependency : node.getDependencies()) {
                final String depId = dependency.getId();
                if (notFound.contains(depId)) {
                    missing.put(depId, MissingType.NOT_FOUND);
                    continue;
                }
                if (duplicates.contains(depId)) {
                    missing.put(depId, MissingType.DUPLICATE);
                    continue;
                }
                if (cyclics.contains(depId)) {
                    missing.put(depId, MissingType.CYCLIC);
                    continue;
                }
                if (unloaded.containsKey(depId)) {
                    missing.put(depId, MissingType.MISSING_DEPENDENCIES);
                    continue;
                }
            }
        }
        return missing;
    }
    
    public HashSet<String> getCyclics() {
        return cyclics;
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
