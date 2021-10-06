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

    public DescriptionParser(final File file, final BufferedReader reader) {
        this.file = file;
        this.reader = reader;
    }

    public ModuleDescription parse() throws ModuleDescriptionException {
        if (description != null) {
            return description;
        }

        JsonObject data;
        try {
            final JsonValue<?> raw = PARSER.fromString(IOHelper.toString(reader));
            if (!raw.hasType(ValueType.OBJECT)) {
                throw new ModuleDescriptionException("Invalid module info; Info has to be an json object!");
            }
            data = (JsonObject) raw;
            reader.close();
        } catch (final IOException ioe) {
            throw new ModuleDescriptionException(ioe);
        } catch (final JsonSyntaxException exp) {
            throw new ModuleDescriptionException("Malformed module info!", exp);
        }

        final String id = getAs(data, "id", ValueType.STRING, String.class);
        final String classPath = getAs(data, "main", ValueType.STRING, String.class);
        final String rawVersion = getAs(data, "version", ValueType.STRING, String.class);

        final JsonObject rawDependencies = getOr(data, "dependencies", ValueType.OBJECT, JsonObject::new);
        final JsonArray rawPluginDeps = getOr(rawDependencies, "plugin", ValueType.ARRAY, JsonArray::new);
        final JsonArray rawModuleDeps = getOr(rawDependencies, "module", ValueType.ARRAY, JsonArray::new);

        final String rawSystem = getAsOrDefault(data, "system", ValueType.STRING, "system");
        final String description = getAsOrDefault(data, "description", ValueType.STRING, "");
        final JsonArray rawAuthors = getOr(data, "authors", ValueType.ARRAY, JsonArray::new);

        final DependencyVersion version = DependencyVersionParser.INSTANCE.analyze(rawVersion);
        final Dependency system = new Dependency(rawSystem);

        final String[] authors = asStringArray(rawAuthors);
        final String[] pluginDepsArray = asStringArray(rawPluginDeps);
        final String[] moduleDepsArray = asStringArray(rawModuleDeps);

        final Dependency[] pluginDeps = parseDependencies(pluginDepsArray);
        final Dependency[] moduleDeps = parseDependencies(moduleDepsArray);

        return this.description = new ModuleDescription(file, id, classPath, version, system, moduleDeps, pluginDeps, description, authors);
    }

    @SuppressWarnings("unchecked")
    private <E> E getAsOrDefault(final JsonObject object, final String path, final ValueType type, final E fallback) {
        return object.has(path, type) ? (E) object.get(path).getValue() : fallback;
    }

    private <E> E getAs(final JsonObject object, final String path, final ValueType type, final Class<E> sample) {
        return sample.cast(get(object, path, type).getValue());
    }

    @SuppressWarnings("unchecked")
    private <E extends JsonValue<?>> E getOr(final JsonObject object, final String path, final ValueType type, final Supplier<E> fallback) {
        return object.has(path, type) ? (E) object.get(path) : fallback.get();
    }

    private JsonValue<?> get(final JsonObject object, final String path, final ValueType type) {
        if (!object.has(path, type)) {
            throw new ModuleDescriptionException("Module info is missing field '" + path + "'!");
        }
        return object.get(path);
    }

    private Dependency[] parseDependencies(final String[] dependencies) {
        if (dependencies.length == 0) {
            return new Dependency[0];
        }
        final ArrayList<Dependency> list = new ArrayList<>();
        for (final String dependency : dependencies) {
            try {
                list.add(new Dependency(dependency));
            } catch (final IllegalArgumentException exception) {
                throw new ModuleDescriptionException("Module description contains malformed dependency", exception);
            }
        }
        return list.toArray(new Dependency[list.size()]);
    }

    private String[] asStringArray(final JsonArray data) {
        final String[] output = new String[data.size()];
        for (int index = 0; index < output.length; index++) {
            output[index] = data.get(index).getValue().toString();
        }
        return output;
    }

}
