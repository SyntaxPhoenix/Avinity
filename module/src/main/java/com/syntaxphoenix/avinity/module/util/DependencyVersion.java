package com.syntaxphoenix.avinity.module.util;

import com.syntaxphoenix.syntaxapi.version.Version;

public class DependencyVersion extends Version {

    protected int revision;

    public DependencyVersion() {
        this(0);
    }

    public DependencyVersion(int major) {
        this(major, 0);
    }

    public DependencyVersion(int major, int minor) {
        this(major, minor, 0);
    }

    public DependencyVersion(int major, int minor, int patch) {
        this(major, minor, patch, 0);
    }

    public DependencyVersion(int major, int minor, int patch, int revision) {
        super(major, minor, patch);
        this.revision = revision;
    }

    public int getRevision() {
        return revision;
    }

    @Override
    protected DependencyVersion setMajor(int major) {
        super.setMajor(major);
        return this;
    }

    @Override
    protected DependencyVersion setMinor(int minor) {
        super.setMinor(minor);
        return this;
    }

    @Override
    protected DependencyVersion setPatch(int patch) {
        super.setPatch(patch);
        return this;
    }

    protected DependencyVersion setRevision(int revision) {
        this.revision = revision;
        return this;
    }

    @Override
    public boolean isHigher(Version version) {
        if (super.isHigher(version)) {
            return true;
        }
        if (version instanceof DependencyVersion) {
            DependencyVersion other = (DependencyVersion) version;
            return revision > other.revision;
        } else {
            return revision > 0;
        }
    }

    @Override
    public boolean isSimilar(Version version) {
        return super.isSimilar(version) && ((version instanceof DependencyVersion) && revision == ((DependencyVersion) version).revision);
    }

    @Override
    public boolean isLower(Version version) {
        if (super.isHigher(version)) {
            return true;
        }
        if (version instanceof DependencyVersion) {
            DependencyVersion other = (DependencyVersion) version;
            return revision < other.revision;
        }
        return false;
    }

    @Override
    public DependencyVersion clone() {
        return new DependencyVersion(getMajor(), getMinor(), getPatch(), revision);
    }

    @Override
    public DependencyVersion update(int major, int minor, int patch) {
        return update(major, minor, patch, 0);
    }

    public DependencyVersion update(int major, int minor, int patch, int revision) {
        return ((DependencyVersion) super.update(major, minor, patch)).setRevision(this.revision + revision);
    }

    @Override
    protected DependencyVersion init(int major, int minor, int patch) {
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