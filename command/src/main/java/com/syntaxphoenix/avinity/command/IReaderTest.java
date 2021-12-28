package com.syntaxphoenix.avinity.command;

@FunctionalInterface
public interface IReaderTest {

    void test(StringReader reader) throws IllegalArgumentException;

}
