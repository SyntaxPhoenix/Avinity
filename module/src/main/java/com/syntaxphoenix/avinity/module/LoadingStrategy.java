package com.syntaxphoenix.avinity.module;

public enum LoadingStrategy {

    ADM(ClassSource.APPLICATION, ClassSource.DEPENDENCY, ClassSource.MODULE),
    AMD(ClassSource.APPLICATION, ClassSource.MODULE, ClassSource.DEPENDENCY),
    DAM(ClassSource.DEPENDENCY, ClassSource.APPLICATION, ClassSource.MODULE),
    DMA(ClassSource.DEPENDENCY, ClassSource.MODULE, ClassSource.APPLICATION),
    MAD(ClassSource.MODULE, ClassSource.APPLICATION, ClassSource.DEPENDENCY),
    MDA(ClassSource.MODULE, ClassSource.DEPENDENCY, ClassSource.APPLICATION);

    private final ClassSource[] sources;

    private LoadingStrategy(ClassSource... sources) {
        this.sources = sources;
    }

    public ClassSource[] getSources() {
        return sources;
    }

    public static enum ClassSource {
        APPLICATION,
        MODULE,
        DEPENDENCY;
    }

}
