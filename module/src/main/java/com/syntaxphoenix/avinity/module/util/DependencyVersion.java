package com.syntaxphoenix.avinity.module.util;

import com.syntaxphoenix.syntaxapi.version.Version;

public class DependencyVersion extends Version {

    protected int revision;

    public DependencyVersion() {
        this(0);
    }

    public DependencyVersion(final int major) {
        this(major, 0);
    }

    public DependencyVersion(final int major, final int minor) {
        this(major, minor, 0);
    }

    public DependencyVersion(final int major, final int minor, final int patch) {
        this(major, minor, patch, 0);
    }

    public DependencyVersion(final int major, final int minor, final int patch, final int revision) {
        super(major, minor, patch);
        this.revision = revision;
    }

    public int getRevision() {
        return revision;
    }

    @Override
    protected DependencyVersion setMajor(final int major) {
        super.setMajor(major);
        return this;
    }

    @Override
    protected DependencyVersion setMinor(final int minor) {
        super.setMinor(minor);
        return this;
    }

    @Override
    protected DependencyVersion setPatch(final int patch) {
        super.setPatch(patch);
        return this;
    }

    protected DependencyVersion setRevision(final int revision) {
        this.revision = revision;
        return this;
    }

    @Override
    public boolean isHigher(final Version version) {
        if (super.isHigher(version)) {
            return true;
        }
        if (version instanceof DependencyVersion) {
            final DependencyVersion other = (DependencyVersion) version;
            return revision > other.revision;
        }
        return revision > 0;
    }

    @Override
    public boolean isSimilar(final Version version) {
        return super.isSimilar(version) && version instanceof DependencyVersion && revision == ((DependencyVersion) version).revision;
    }

    @Override
    public boolean isLower(final Version version) {
        if (super.isHigher(version)) {
            return true;
        }
        if (version instanceof DependencyVersion) {
            final DependencyVersion other = (DependencyVersion) version;
            return revision < other.revision;
        }
        return false;
    }

    @Override
    public DependencyVersion clone() {
        return new DependencyVersion(getMajor(), getMinor(), getPatch(), revision);
    }

    @Override
    public DependencyVersion update(final int major, final int minor, final int patch) {
        return update(major, minor, patch, 0);
    }

    public DependencyVersion update(final int major, final int minor, final int patch, final int revision) {
        return ((DependencyVersion) super.update(major, minor, patch)).setRevision(this.revision + revision);
    }

    @Override
    protected DependencyVersion init(final int major, final int minor, final int patch) {
        return new DependencyVersion(major, minor, patch);
    }

    @Override
    public DependencyVersionParser getAnalyzer() {
        return DependencyVersionParser.INSTANCE;
    }

    @Override
    public DependencyVersionParser getFormatter() {
        return DependencyVersionParser.INSTANCE;
    }

}