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
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
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

        for (Element element : roundEnv.getElementsAnnotatedWith(ExtensionPoint.class)) {
            if (element.getKind() != ElementKind.ANNOTATION_TYPE) {
                continue;
            }
            processExtensionPoint(element);
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(Extension.class)) {
            if (element.getKind() != ElementKind.ANNOTATION_TYPE) {
                continue;
            }
            processExtension(element);
        }

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

    private void processExtension(Element element) {
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
        if (ExtensionUtils.getAnnotationMirror(typeElement, ExtensionPoint.class) != null) {
            log(Kind.ERROR, "%s ExtensionPoint can't be a Extension at the same time!");
            return;
        }
        String typeName = type.toString();
        if (extensionMap.containsKey(typeName)) {
            return; // Don't know if that will even happen
        }
        HashSet<String> extensions = extensionMap.computeIfAbsent(typeName, HASHSET_BUILDER);
        for (TypeMirror mirror : typeElement.getInterfaces()) {
            String mirrorName = mirror.toString();
            if (!extensionPointMap.containsKey(mirrorName)) {
                continue;
            }
            extensions.add(mirrorName);
            extensions.addAll(extensionPointMap.get(mirrorName));
        }
    }

    private void processExtensionPoint(Element element) {
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
        if (ExtensionUtils.getAnnotationMirror(typeElement, Extension.class) != null) {
            log(Kind.ERROR, "%s ExtensionPoint can't be a Extension at the same time!", element);
            return;
        }
        String typeName = type.toString();
        if (extensionPointMap.containsKey(typeName)) {
            return; // Already processed
        }
        findPoints(extensionPointMap.computeIfAbsent(typeName, HASHSET_BUILDER), typeElement);
    }

    private void findPoints(HashSet<String> points, TypeElement element) {
        List<? extends TypeMirror> interfaces = element.getInterfaces();
        interfaces.remove(extensionType);
        for (TypeMirror mirror : interfaces) {
            if (!typeHelper.isAssignable(mirror, extensionType)) {
                continue;
            }
            String typeName = mirror.toString();
            points.add(typeName);
            processExtensionPoint(elementHelper.getTypeElement(typeName));
            if (!extensionPointMap.containsKey(typeName)) {
                continue; // Invalid
            }
            points.addAll(extensionPointMap.get(typeName));
        }
    }

    /*
     * Logging
     */

    public void log(Kind type, String message, Object... arguments) {
        processingEnv.getMessager().printMessage(type, String.format(message, arguments));
    }

    public void log(Kind type, Element element, String message, Object... arguments) {
        processingEnv.getMessager().printMessage(type, String.format(message, arguments), element);
    }

}
