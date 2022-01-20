package com.syntaxphoenix.avinity.command.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import com.syntaxphoenix.avinity.command.CommandContext;
import com.syntaxphoenix.avinity.command.StringReader;
import com.syntaxphoenix.avinity.command.connection.NodeConnection;
import com.syntaxphoenix.avinity.command.node.Argument;
import com.syntaxphoenix.avinity.command.node.Literal;
import com.syntaxphoenix.avinity.command.node.Root;
import com.syntaxphoenix.avinity.command.node.RootNode;
import com.syntaxphoenix.avinity.command.type.IArgumentType;

public final class ParseTest {

    private final NodeConnection<BasicSource> connection = new NodeConnection<>(buildCommand());
    private final BasicSource basicSource = new BasicSource();

    private RootNode<BasicSource> buildCommand() {
        return Root.<BasicSource>of("0")
            .append(Literal.<BasicSource>of("a2").argument("1", Argument.at(0, IArgumentType.STRING, false))
                .argument("2", Argument.at(1, IArgumentType.STRING, false)).execute(this::execute))
            .append(Literal.<BasicSource>of("a3").argument("1", Argument.at(0, IArgumentType.STRING, false))
                .argument("2", Argument.at(1, IArgumentType.STRING, false)).argument("3", Argument.at(2, IArgumentType.INTEGER, false))
                .execute(this::execute))
            .append(Literal.<BasicSource>of("n2").argument("1", Argument.at(0, IArgumentType.INTEGER, false))
                .argument("2", Argument.at(1, IArgumentType.LONG, false)).execute(this::execute))
            .build();
    }

    private void execute(CommandContext<BasicSource> context) {

    }

    /*
     * 
     */

    private final LinkedHashMap<String, Integer> commands = new LinkedHashMap<>() {
        private static final long serialVersionUID = 1L;
        {
            for (String child : connection.getNode().getChildrenNames()) {
                put(child, connection.getNode().getChild(child).getArguments().size());
            }
        }
    };

    private final LinkedHashMap<String, Integer> arguments = new LinkedHashMap<>() {
        private static final long serialVersionUID = 1L;
        {
            add("");
            add("add");
            add("add bed");
            add("add bed 3");
            add("10 20 30");
        }

        private void add(String value) {
            put(value, value.isEmpty() ? 0 : value.split(" ").length);
        }
    };

    @TestFactory
    public DynamicTest[] testParse() {
        DynamicTest[] tests = new DynamicTest[arguments.size() * commands.size()];
        int index = 0;
        for (String command : commands.keySet()) {
            for (String argument : arguments.keySet()) {
                tests[index++] = DynamicTest.dynamicTest(command + "-(" + argument + ")", () -> runTest(command, argument));
            }
        }
        return tests;
    }

    private void runTest(String command, String argument) {
        CommandContext<BasicSource> context = connection.parse(basicSource, command + " " + argument);
        String[] values = argument.split(" ");
        Map<String, Argument<?>> arguments = connection.getNode().getChild(command).getArguments();
        int start = 1;
        for (int index = 0; index < values.length; index++, start++) {
            Argument<?> arg = arguments.get("" + start);
            if (arg == null) {
                break;
            }
            StringReader reader = new StringReader(values[index]);
            Object value;
            try {
                value = arg.getType().safeParse(reader);
            } catch (IllegalArgumentException iae) {
                Assertions.fail("Unable to parse Argument", iae);
                continue;
            }
            assertEquals(value, context.getArgument("" + start).getValue());
        }
        if (context.getArgumentCount() >= commands.get(command)) {
            assertTrue(context.hasCommand());
            assertTrue(!context.hasException());
        }
    }

}
