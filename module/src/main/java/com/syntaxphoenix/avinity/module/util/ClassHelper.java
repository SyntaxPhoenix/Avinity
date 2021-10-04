package com.syntaxphoenix.avinity.module.util;

import static com.syntaxphoenix.syntaxapi.reflection.ClassCache.CLASSES;

import java.util.Optional;

import com.syntaxphoenix.syntaxapi.reflection.ClassCache;


public final class ClassHelper {

    private ClassHelper() {}

    public static Class<?> getClass(String classPath) {
        return ClassCache.getClass(classPath);
    }

    public static Optional<Class<?>> getOptionalClass(String classPath) {
        return ClassCache.getOptionalClass(classPath);
    }

    public static void uncacheAll() {
        CLASSES.clear();
    }
    
    public static void uncache(String clazz) {
        CLASSES.remove(clazz);
    }
    
    public static void uncache(Class<?> clazz) {
        uncache(clazz.getName());
    }
}
