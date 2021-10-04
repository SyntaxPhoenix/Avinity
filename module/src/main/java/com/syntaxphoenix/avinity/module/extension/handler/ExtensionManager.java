package com.syntaxphoenix.avinity.module.extension.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.syntaxphoenix.avinity.module.ModuleManager;
import com.syntaxphoenix.avinity.module.ModuleWrapper;
import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;
import com.syntaxphoenix.syntaxapi.json.io.JsonParser;
import com.syntaxphoenix.syntaxapi.json.io.JsonSyntaxException;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;

public class ExtensionManager {

    private static final JsonParser PARSER = new JsonParser();

    private final ArrayList<ExtensionWrapper> extensions = new ArrayList<>();
    private final HashMap<String, ArrayList<ExtensionWrapper>> waiting = new HashMap<>();

    private final ILogger logger;

    public ExtensionManager(ModuleManager<?> moduleManager) {
        this.logger = moduleManager.getLogger();
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
        if(!rootObject.has("extensions", ValueType.OBJECT)) {
            return;
        }
    }

    public void injectExtensions(ModuleWrapper<?> module) {
        String id = module.getId();
        if (!waiting.containsKey(id)) {
            return;
        }
        ArrayList<ExtensionWrapper> wrappers = waiting.remove(id);
        for (ExtensionWrapper wrapper : wrappers) {
            wrapper.setOwner(module);
        }
        extensions.addAll(wrappers);
    }

    public void unloadExtensions(ModuleWrapper<?> wrapper) {

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
