package com.syntaxphoenix.avinity.module.extension.processor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;

public final class ExtensionUtils {

    private ExtensionUtils() {}
    
    public static AnnotationMirror getAnnotationMirror(TypeElement element, Class<?> annotation) {
        String annotationName = annotation.getName();
        for(AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if(mirror.getAnnotationType().toString().equals(annotationName)) {
                return mirror;
            }
        }
        return null;
    }
    
}
