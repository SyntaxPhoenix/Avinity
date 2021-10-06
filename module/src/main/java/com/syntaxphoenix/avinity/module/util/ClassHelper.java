package com.syntaxphoenix.avinity.module.util;

import static com.syntaxphoenix.syntaxapi.reflection.ClassCache.CLASSES;

public final class ClassHelper {

    private ClassHelper() {}

    public static Class<?> getClass(String classPath, ClassLoader loader) {
        if (CLASSES.containsKey(classPath)) {
            return CLASSES.get(classPath);
        }
        try {
            Class<?> clazz = Class.forName(classPath, true, loader);
            if (clazz != null) {
                CLASSES.put(classPath, clazz);
                return clazz;
            }
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static void uncache(String clazz) {
        CLASSES.remove(clazz);
    }

    public static void uncache(Class<?> clazz) {
        uncache(clazz.getName());
    }
}
