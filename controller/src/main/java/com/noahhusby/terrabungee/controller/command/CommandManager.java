package com.noahhusby.terrabungee.controller.command;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private static CommandManager instance = null;

    public static CommandManager getInstance() {
        if(instance == null) instance = new CommandManager();
        return instance;
    }

    private CommandManager() {
        registerCommand(new StopCommand());
        registerCommand(new AddStaticCommand());
        registerCommand(new RemoveStaticCommand());
        registerCommand(new ListStaticCommand());
        registerCommand(new DefaultServerCommand());
    }

    private final List<ICommand> registeredCommands = new ArrayList<>();

    private void registerCommand(ICommand command) {
        registeredCommands.add(command);
    }

    public boolean execute(String input) {
        String[] args = input.split(" ");
        for(ICommand c : registeredCommands) {
            if(c.getName().equalsIgnoreCase(args[0])) {
                c.execute(args);
                return true;
            }
        }

        return false;
    }
}
