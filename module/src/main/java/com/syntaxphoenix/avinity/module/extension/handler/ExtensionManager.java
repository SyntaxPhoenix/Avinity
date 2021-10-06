package com.syntaxphoenix.avinity.module.extension.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.syntaxphoenix.avinity.module.Module;
import com.syntaxphoenix.avinity.module.ModuleManager;
import com.syntaxphoenix.avinity.module.ModuleWrapper;
import com.syntaxphoenix.avinity.module.extension.IExtension;
import com.syntaxphoenix.syntaxapi.json.JsonEntry;
import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;
import com.syntaxphoenix.syntaxapi.json.io.JsonParser;
import com.syntaxphoenix.syntaxapi.json.io.JsonSyntaxException;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;

public class ExtensionManager<M extends Module> {

    private static final JsonParser PARSER = new JsonParser();

    private final ArrayList<ExtensionWrapper> extensions = new ArrayList<>();
    private final HashMap<String, ArrayList<ExtensionWrapper>> waiting = new HashMap<>();

    private final ModuleManager<M> moduleManager;
    private final ILogger logger;

    public ExtensionManager(ModuleManager<M> moduleManager) {
        this.moduleManager = moduleManager;
        this.logger = moduleManager.getLogger();
    }

    /*
     * Extension Loading
     */

    public void loadSystemExtensions(String data) {
        loadExtensions("system", data);
        injectExtensions(moduleManager.getSystem());
    }

    public void loadExtensions(String module, String data) {
        JsonValue<?> rootElement = null;
        try {
            rootElement = PARSER.fromString(data);
        } catch (IOException | JsonSyntaxException e) {
            log(LogTypeId.ERROR, String.format("Failed to parse extensions.json of '%s'!", module));
            log(LogTypeId.ERROR, e);
            return;
        }
        if (rootElement == null || !rootElement.hasType(ValueType.OBJECT)) {
            log(LogTypeId.ERROR, String.format("The extensions.json of '%s' is invalid!", module));
            log(LogTypeId.ERROR, String.format("Found %s(%s) expected Type(OBJECT)", rootElement == null ? "Value" : "Type",
                (rootElement == null ? null : rootElement.getType())));
            return;
        }
        JsonObject rootObject = (JsonObject) rootElement;
        if (!rootObject.has("extensions", ValueType.OBJECT)) {
            return;
        }
        ArrayList<ExtensionWrapper> wrappers = new ArrayList<>();
        JsonObject object = (JsonObject) rootObject.get("extensions");
        for (JsonEntry<?> entry : object) {
            if (!entry.getType().isType(ValueType.ARRAY)) {
                continue;
            }
            wrappers.add(new ExtensionWrapper(entry.getKey()));
        }
        if (wrappers.isEmpty()) {
            return;
        }
        waiting.put(module, wrappers);
    }

    public void injectExtensions(ModuleWrapper<?> module) {
        String id = module.getId();
        if (!waiting.containsKey(id)) {
            return;
        }
        ArrayList<ExtensionWrapper> wrappers = waiting.remove(id);
        for (ExtensionWrapper wrapper : wrappers) {
            wrapper.setOwner(module);
            wrapper.getExtension();
        }
        extensions.addAll(wrappers);
    }

    public void unloadExtensions(ModuleWrapper<?> module) {
        int size = extensions.size();
        for (int index = 0; index < size; index++) {
            ExtensionWrapper wrapper = extensions.get(index);
            if (wrapper.getOwner() != module) {
                continue;
            }
            extensions.remove(wrapper);
            wrapper.clear();
            index--;
        }
    }

    /*
     * Extension Helper
     */

    public <V extends IExtension> List<V> getExtensions(Class<V> clazz) {
        ArrayList<V> output = new ArrayList<>();
        int size = extensions.size();
        for (int index = 0; index < size; index++) {
            ExtensionWrapper wrapper = extensions.get(index);
            if (!wrapper.isAssignable(clazz)) {
                continue;
            }
            try {
                IExtension extension = wrapper.getInstance();
                if (!clazz.isInstance(extension)) {
                    continue;
                }
                output.add(clazz.cast(extension));
            } catch (IllegalStateException exp) {
                log(LogTypeId.WARNING, exp);
            }
        }
        return output;
    }

    public <V extends IExtension> List<V> getExtensionsOf(String moduleId, Class<V> clazz) {
        if (moduleId == null) {
            return getExtensions(clazz);
        }
        if (moduleId.equals("system")) {
            return getSystemExtensions(clazz);
        }
        Optional<ModuleWrapper<M>> option = moduleManager.getModule(moduleId);
        if (option.isEmpty()) {
            return Collections.emptyList();
        }
        ModuleWrapper<M> module = option.get();
        ArrayList<V> output = new ArrayList<>();
        int size = extensions.size();
        for (int index = 0; index < size; index++) {
            ExtensionWrapper wrapper = extensions.get(index);
            if (module != wrapper.getOwner() || !wrapper.isAssignable(clazz)) {
                continue;
            }
            try {
                IExtension extension = wrapper.getInstance();
                if (!clazz.isInstance(extension)) {
                    continue;
                }
                output.add(clazz.cast(extension));
            } catch (IllegalStateException exp) {
                log(LogTypeId.WARNING, exp);
            }
        }
        return output;
    }

    public <V extends IExtension> List<V> getSystemExtensions(Class<V> clazz) {
        ModuleWrapper<M> module = moduleManager.getSystem();
        ArrayList<V> output = new ArrayList<>();
        int size = extensions.size();
        for (int index = 0; index < size; index++) {
            ExtensionWrapper wrapper = extensions.get(index);
            if (module != wrapper.getOwner() || !wrapper.isAssignable(clazz)) {
                continue;
            }
            try {
                IExtension extension = wrapper.getInstance();
                if (!clazz.isInstance(extension)) {
                    continue;
                }
                output.add(clazz.cast(extension));
            } catch (IllegalStateException exp) {
                log(LogTypeId.WARNING, exp);
            }
        }
        return output;
    }

    /*
     * Logging
     */

    private void log(LogTypeId type, String message) {
        if (logger == null) {
            return;
        }
        logger.log(type, message);
    }

    private void log(LogTypeId type, Throwable throwable) {
        if (logger == null) {
            return;
        }
        logger.log(type, throwable);
    }

}
