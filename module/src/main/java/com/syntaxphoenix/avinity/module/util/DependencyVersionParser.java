package com.syntaxphoenix.avinity.module.util;

import com.syntaxphoenix.syntaxapi.utils.java.Strings;
import com.syntaxphoenix.syntaxapi.version.Version;
import com.syntaxphoenix.syntaxapi.version.VersionAnalyzer;
import com.syntaxphoenix.syntaxapi.version.VersionFormatter;

public class DependencyVersionParser implements VersionAnalyzer, VersionFormatter {

    public static final DependencyVersionParser INSTANCE = new DependencyVersionParser();

    private DependencyVersionParser() {}

    @Override
    public DependencyVersion analyze(final String formatted) {
        final int[] version = {
            0,
            0,
            0,
            0
        };
        final String[] parts = formatted.contains(".") ? formatted.split("\\.")
            : new String[] {
                formatted
            };
        if (parts.length > version.length) {
            throw new IllegalArgumentException("Version got more parts than supported!");
        }
        int length = 0;
        for (int index = 0; index < version.length; index++, length++) {
            if (parts.length <= index) {
                break;
            }
            if (!Strings.isNumeric(parts[index])) {
                throw new IllegalArgumentException("Version contains non numeric part at position " + length);
            }
            try {
                version[index] = Integer.parseInt(parts[index]);
            } catch (final NumberFormatException exception) {
                throw new IllegalArgumentException("Version contains non parseable number at position " + length);
            }
            length += parts[index].length();
        }
        return new DependencyVersion(version[0], version[1], version[2], version[3]);
    }

    @Override
    public String format(final Version version) {
        final StringBuilder builder = new StringBuilder();
        builder.append(version.getMajor());
        builder.append('.');
        builder.append(version.getMinor());
        builder.append(".");
        builder.append(version.getPatch());
        if (version instanceof DependencyVersion) {
            final DependencyVersion dependency = (DependencyVersion) version;
            if (dependency.getRevision() != 0) {
                builder.append('.');
                builder.append(dependency.getRevision());
            }
        }
        return builder.toString();
    }

}
