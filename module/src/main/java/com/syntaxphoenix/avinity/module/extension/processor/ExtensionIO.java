package com.syntaxphoenix.avinity.module.extension.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.syntaxphoenix.syntaxapi.json.JsonArray;
import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.io.JsonWriter;
import com.syntaxphoenix.syntaxapi.json.value.JsonString;

public final class ExtensionIO {

    private static final JsonWriter WRITER = new JsonWriter().setPretty(true).setSpaces(true).setIndent(2);

    private ExtensionIO() {}

    public static void writeToWriter(Writer writer, HashMap<String, HashSet<String>> points, HashMap<String, HashSet<String>> extensions) {
        JsonObject object = new JsonObject();
        object.set("points", toJsonObject(points));
        object.set("extensions", toJsonObject(extensions));

        try {
            WRITER.toWriter(object, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JsonObject toJsonObject(HashMap<String, HashSet<String>> values) {
        JsonObject object = new JsonObject();
        for (Entry<String, HashSet<String>> entry : values.entrySet()) {
            JsonArray array = new JsonArray();
            for (String value : entry.getValue()) {
                array.add(new JsonString(value)); // Explicitly use JsonString for performance
            }
            object.set(entry.getKey(), array);
        }
        return object;
    }

}
