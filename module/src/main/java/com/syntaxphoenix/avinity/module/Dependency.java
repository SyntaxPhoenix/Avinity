package com.syntaxphoenix.avinity.module;

import com.syntaxphoenix.avinity.module.util.DependencyVersion;
import com.syntaxphoenix.avinity.module.util.DependencyVersionParser;

public class Dependency {

    private final String id;

    private final DependencyVersion minimum;
    private final DependencyVersion maximum;

    private final boolean optional;

    public Dependency(final String dependency) {
        final int index = dependency.indexOf('#');
        String tempId;
        if (index == -1) {
            tempId = dependency;
            minimum = maximum = null;
        } else {
            tempId = dependency.substring(0, index);
            if (dependency.length() > index + 1) {
                final DependencyVersion[] support = analyzeSupport(dependency.substring(index + 1));
                minimum = support[0];
                maximum = support[1];
            } else {
                minimum = maximum = null;
            }
        }
        id = (optional = tempId.endsWith("?")) ? tempId.substring(0, tempId.length() - 1) : tempId;
    }

    private DependencyVersion[] analyzeSupport(final String support) {
        final DependencyVersion[] versions = new DependencyVersion[2];
        int index = support.indexOf('+');
        if (index != -1) {
            versions[0] = DependencyVersionParser.INSTANCE.analyze(support.substring(0, index));
            return versions;
        }
        index = support.indexOf('-');
        if (index != -1) {
            versions[0] = DependencyVersionParser.INSTANCE.analyze(support.substring(0, index));
            versions[1] = DependencyVersionParser.INSTANCE.analyze(support.substring(index + 1));
            return versions;
        }
        versions[0] = versions[1] = DependencyVersionParser.INSTANCE.analyze(support);
        return versions;
    }

    public String getId() {
        return id;
    }

    public boolean isOptional() {
        return optional;
    }

    public DependencyVersion getMaximum() {
        return maximum;
    }

    public DependencyVersion getMinimum() {
        return minimum;
    }

    public boolean hasMaximum() {
        return maximum == null;
    }

    public boolean hasMinimum() {
        return minimum == null;
    }

    public boolean hasStrictVersion() {
        return hasMinimum() && hasMaximum() && minimum == maximum;
    }

}
