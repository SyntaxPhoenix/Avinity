package com.syntaxphoenix.avinity.module.extension.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.syntaxphoenix.avinity.module.extension.Extension;
import com.syntaxphoenix.avinity.module.extension.ExtensionPoint;
import com.syntaxphoenix.avinity.module.extension.IExtension;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;

public class ExtensionProcessor extends AbstractProcessor {

    public static final String EXTENSIONS_RESOURCE = "META-INF/extensions.json";

    private static final Function<String, HashSet<String>> HASHSET_BUILDER = (ignore) -> new HashSet<>();

    private final HashMap<String, HashSet<String>> extensionMap = new HashMap<>();
    private final HashMap<String, HashSet<String>> extensionPointMap = new HashMap<>();

    private Types typeHelper;
    private Elements elementHelper;

    private TypeMirror extensionType;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.typeHelper = processingEnv.getTypeUtils();
        this.elementHelper = processingEnv.getElementUtils();
        this.extensionType = elementHelper.getTypeElement(IExtension.class.getName()).asType();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton("*");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        log(Kind.NOTE, "Processing @%s", ExtensionPoint.class.getName());
        for (Element element : roundEnv.getElementsAnnotatedWith(ExtensionPoint.class)) {
            if (element.getKind() == ElementKind.ANNOTATION_TYPE) {
                continue;
            }
            preProcessExtensionPoint(element);
        }

        log(Kind.NOTE, "Processing nested @%s", ExtensionPoint.class.getName());
        for (TypeElement typeElement : annotations) {
            if (getAnnotationMirror(typeElement, ExtensionPoint.class) == null) {
                continue;
            }
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                preProcessExtensionPoint(element);
            }
        }

        log(Kind.NOTE, "Processing @%s", Extension.class.getName());
        for (Element element : roundEnv.getElementsAnnotatedWith(Extension.class)) {
            if (element.getKind() == ElementKind.ANNOTATION_TYPE) {
                continue;
            }
            preProcessExtension(element);
        }

        log(Kind.NOTE, "Processing nested @%s", Extension.class.getName());
        for (TypeElement typeElement : annotations) {
            if (getAnnotationMirror(typeElement, Extension.class) == null) {
                continue;
            }
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                preProcessExtension(element);
            }
        }

        log(Kind.NOTE, "Saving ExtensionPoints (%s) and Extensions (%s) to file", extensionPointMap.size(), extensionMap.size());
        try {
            FileObject file = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", EXTENSIONS_RESOURCE);
            try (Writer writer = file.openWriter()) {
                ExtensionIO.writeToWriter(writer, extensionPointMap, extensionMap);
            }
        } catch (IOException e) {
            log(Kind.ERROR, Exceptions.stackTraceToString(e));
        }

        return false;
    }

    private void preProcessExtension(Element element) {
        if (element instanceof TypeElement) {
            TypeElement typeElement = (TypeElement) element;
            if (getAnnotationMirror(typeElement, ExtensionPoint.class) != null
                && isInterfaceNested(typeElement.getInterfaces(), extensionType)) {
                log(Kind.ERROR, "%s ExtensionPoint can't be a Extension at the same time!");
                return;
            }
        }
        processExtension(element);
    }

    private void processExtension(Element element) {
        log(Kind.NOTE, "Processing Extension '%s'", element.asType().toString());
        if (!(element instanceof TypeElement)) {
            log(Kind.ERROR, "Extension annotation is only available for classes");
            return;
        }
        TypeMirror type = element.asType();
        if (!typeHelper.isAssignable(type, extensionType)) {
            log(Kind.ERROR, "%s is not an Extension (doesn't implement IExtension)", element);
            return;
        }
        TypeElement typeElement = (TypeElement) element;
        if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
            log(Kind.WARNING, "%s is an abstract class and can't be a Extension", typeElement);
            return;
        }
        String typeName = type.toString();
        if (extensionMap.containsKey(typeName)) {
            return; // Don't know if that will even happen
        }
        log(Kind.NOTE, "Collecting ExtensionPoints for ", typeName);
        HashSet<String> extensions = extensionMap.computeIfAbsent(typeName, HASHSET_BUILDER);
        findPoints(typeElement, extensions);
    }

    private void preProcessExtensionPoint(Element element) {
        if (element instanceof TypeElement) {
            TypeElement typeElement = (TypeElement) element;
            if (!isInterfaceNested(typeElement.getInterfaces(), extensionType)) {
                return;
            }
        }
        processExtensionPoint(element);
    }

    private void processExtensionPoint(Element element) {
        log(Kind.NOTE, "Processing ExtensionPoint '%s'", element.asType().toString());
        if (!(element instanceof TypeElement)) {
            log(Kind.ERROR, "ExtensionPoint annotation is only available for classes");
            return;
        }
        TypeMirror type = element.asType();
        if (!typeHelper.isAssignable(type, extensionType)) {
            log(Kind.ERROR, "%s is not an ExtensionPoint (doesn't implement IExtension)", element);
            return;
        }
        TypeElement typeElement = (TypeElement) element;
        if (getAnnotationMirror(typeElement, Extension.class) != null) {
            log(Kind.ERROR, "%s ExtensionPoint can't be a Extension at the same time!", element);
            return;
        }
        String typeName = type.toString();
        if (extensionPointMap.containsKey(typeName)) {
            return; // Already processed
        }
        log(Kind.NOTE, "Searching for sub points in '%s'", typeName);
        HashSet<String> extensions = extensionPointMap.computeIfAbsent(typeName, HASHSET_BUILDER);
        findPoints(typeElement, extensions);
    }

    private void findPoints(TypeElement element, HashSet<String> extensions) {
        addNestedInterfaces(element.getInterfaces(), extensionType, extensions);
        Element superElement = typeHelper.asElement(element.getSuperclass());
        if (!(superElement instanceof TypeElement)) {
            return;
        }
        TypeElement typeElement = (TypeElement) superElement;
        if (!isInterfaceNested(typeElement.getInterfaces(), extensionType)) {
            return;
        }
        extensions.add(element.getSuperclass().toString());
        addNestedInterfaces(typeElement.getInterfaces(), extensionType, extensions);
    }

    /*
     * Logging
     */

    public void log(Kind kind, String message, Object... arguments) {
        String out = String.format(message, arguments);
        processingEnv.getMessager().printMessage(kind, out);
        if (kind == Kind.ERROR) {
            System.out.println("[ERROR] " + out);
            return;
        }
        if (kind == Kind.WARNING) {
            System.out.println("[WARNING] " + out);
            return;
        }
        System.out.println("[INFO] " + out);
    }

    /*
     * Helper
     */

    public boolean isInterfaceNested(List<? extends TypeMirror> list, TypeMirror searched) {
        for (TypeMirror mirror : list) {
            if (mirror == searched) {
                return true;
            }
            Element element = typeHelper.asElement(mirror);
            if (!(element instanceof TypeElement) || !isInterfaceNested(((TypeElement) element).getInterfaces(), searched)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public void addNestedInterfaces(List<? extends TypeMirror> list, TypeMirror searched, HashSet<String> set) {
        for (TypeMirror mirror : list) {
            if (mirror == searched) {
                continue;
            }
            String type = mirror.toString();
            if (extensionPointMap.containsKey(type)) {
                set.addAll(extensionPointMap.get(type));
                continue; // Already processed, no need to do again
            }
            Element element = typeHelper.asElement(mirror);
            if (!(element instanceof TypeElement)) {
                continue;
            }
            TypeElement typeElement = (TypeElement) element;
            if (!isInterfaceNested(typeElement.getInterfaces(), searched)) {
                continue;
            }
            set.add(mirror.toString());
        }
    }

    public AnnotationMirror getAnnotationMirror(TypeElement element, Class<?> annotation) {
        String annotationName = annotation.getName();
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            if (mirror.getAnnotationType().toString().equals(annotationName)) {
                return mirror;
            }
        }
        return null;
    }

}
