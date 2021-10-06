package com.syntaxphoenix.avinity.module.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.stream.Collectors;

public final class IOHelper {

    private IOHelper() {}

    public static String toString(Reader reader) throws IOException {
        try (BufferedReader buffer = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader)) {
            return buffer.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

}
