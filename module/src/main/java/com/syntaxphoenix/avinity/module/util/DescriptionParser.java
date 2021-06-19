package com.syntaxphoenix.avinity.module.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Supplier;

import com.syntaxphoenix.avinity.module.Dependency;
import com.syntaxphoenix.avinity.module.ModuleDescription;
import com.syntaxphoenix.avinity.module.ModuleDescriptionException;
import com.syntaxphoenix.syntaxapi.json.JsonArray;
import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;
import com.syntaxphoenix.syntaxapi.json.io.JsonParser;
import com.syntaxphoenix.syntaxapi.json.io.JsonSyntaxException;

public class DescriptionParser {

    private static final JsonParser PARSER = new JsonParser();

    private final File file;
    private final BufferedReader reader;

    private ModuleDescription description;

    public DescriptionParser(File file, BufferedReader reader) {
        this.file = file;
        this.reader = reader;
    }

    public ModuleDescription parse() throws ModuleDescriptionException {
        if (description != null) {
            return description;
        }

        JsonObject data;
        try {
            JsonValue<?> raw = PARSER.fromReader(reader);
            if (!raw.hasType(ValueType.OBJECT)) {
                throw new ModuleDescriptionException("Invalid module info; Info has to be an json object!");
            }
            data = (JsonObject) raw;
            reader.close();
        } catch (IOException ioe) {
            throw new ModuleDescriptionException(ioe);
        } catch (JsonSyntaxException exp) {
            throw new ModuleDescriptionException("Malformed module info!", exp);
        }

        String id = getAs(data, "id", ValueType.STRING, String.class);
        String classPath = getAs(data, "main", ValueType.STRING, String.class);
        String rawVersion = getAs(data, "version", ValueType.STRING, String.class);

        JsonObject rawDependencies = getOr(data, "dependencies", ValueType.OBJECT, JsonObject::new);
        JsonArray rawPluginDeps = getOr(rawDependencies, "plugin", ValueType.ARRAY, JsonArray::new);
        JsonArray rawModuleDeps = getOr(rawDependencies, "module", ValueType.ARRAY, JsonArray::new);

        String rawSystem = getAsOrDefault(data, "description", ValueType.STRING, "system");
        String description = getAsOrDefault(data, "description", ValueType.STRING, "");
        JsonArray rawAuthors = getOr(data, "authors", ValueType.ARRAY, JsonArray::new);

        DependencyVersion version = DependencyVersionParser.INSTANCE.analyze(rawVersion);
        Dependency system = new Dependency(rawSystem);

        String[] authors = asStringArray(rawAuthors);
        String[] pluginDepsArray = asStringArray(rawPluginDeps);
        String[] moduleDepsArray = asStringArray(rawModuleDeps);

        Dependency[] pluginDeps = parseDependencies(pluginDepsArray);
        Dependency[] moduleDeps = parseDependencies(moduleDepsArray);

        return this.description = new ModuleDescription(file, id, classPath, version, system, moduleDeps, pluginDeps, description, authors);
    }

    @SuppressWarnings("unchecked")
    private <E> E getAsOrDefault(JsonObject object, String path, ValueType type, E fallback) {
        return object.has(path, type) ? (E) object.get(path).getValue() : fallback;
    }

    private <E> E getAs(JsonObject object, String path, ValueType type, Class<E> sample) {
        return sample.cast(get(object, path, type).getValue());
    }

    @SuppressWarnings("unchecked")
    private <E extends JsonValue<?>> E getOr(JsonObject object, String path, ValueType type, Supplier<E> fallback) {
        return object.has(path, type) ? (E) object.get(path) : fallback.get();
    }

    private JsonValue<?> get(JsonObject object, String path, ValueType type) {
        if (!object.has(path, type)) {
            throw new ModuleDescriptionException("Module info is missing field '" + path + "'!");
        }
        return object.get(path);
    }

    private Dependency[] parseDependencies(String[] dependencies) {
        if (dependencies.length == 0) {
            return new Dependency[0];
        }
        ArrayList<Dependency> list = new ArrayList<>();
        for (String dependency : dependencies) {
            try {
                list.add(new Dependency(dependency));
            } catch (IllegalArgumentException exception) {
                throw new ModuleDescriptionException("Module description contains malformed dependency", exception);
            }
        }
        return list.toArray(new Dependency[list.size()]);
    }

    private String[] asStringArray(JsonArray data) {
        String[] output = new String[data.size()];
        for (int index = 0; index < output.length; index++) {
            output[index] = data.get(index).getValue().toString();
        }
        return output;
    }

}
