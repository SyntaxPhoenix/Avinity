package com.syntaxphoenix.avinity.command.test;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.syntaxphoenix.avinity.command.CommandContext;
import com.syntaxphoenix.avinity.command.CommandManager;
import com.syntaxphoenix.avinity.command.connection.ManagerConnection;
import com.syntaxphoenix.avinity.command.node.Argument;
import com.syntaxphoenix.avinity.command.node.Root;
import com.syntaxphoenix.avinity.command.type.IArgumentType;

public class SuggestTest {
    
//    @Test
    public void suggestManager() {
        CommandManager<BasicSource> manager = new CommandManager<>();
        manager.register(Root.<BasicSource>of("command").build());
        manager.register(Root.<BasicSource>of("arc").build());
        
        ManagerConnection<BasicSource> connection = new ManagerConnection<>(manager);
        BasicSource source = new BasicSource();
        
        CommandContext<BasicSource> context = connection.parse(source, "c");
        ArrayList<String> list = new ArrayList<>();
        connection.suggest(list, context);
        System.out.println("Suggestions: " + list.toString());
    }
    
    @Test
    public void suggestNode() {
        CommandManager<BasicSource> manager = new CommandManager<>();
        manager.register(Root.<BasicSource>of("command").argument("a", Argument.at(0, IArgumentType.STRING, false)).argument("b", Argument.at(1, IArgumentType.BYTE, false)).argument("c", Argument.at(1, IArgumentType.INTEGER, true)).build());
        
        ManagerConnection<BasicSource> connection = new ManagerConnection<>(manager);
        BasicSource source = new BasicSource();
        
        String input = "command hello ";
        CommandContext<BasicSource> context = connection.parse(source, input);
        System.out.println("Input: '" + input + "'");
        ArrayList<String> list = new ArrayList<>();
        connection.suggest(list, context);
        System.out.println("Suggestions: " + list.toString());
    }

}
