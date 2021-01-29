package com.noahhusby.terrabungee.controller.command;

import com.noahhusby.terrabungee.controller.command.controller.HelpCommand;
import com.noahhusby.terrabungee.controller.command.controller.MigrateCommand;
import com.noahhusby.terrabungee.controller.command.controller.ReloadCommand;
import com.noahhusby.terrabungee.controller.command.controller.StopCommand;
import com.noahhusby.terrabungee.controller.command.instance.AddStaticCommand;
import com.noahhusby.terrabungee.controller.command.instance.DefaultServerCommand;
import com.noahhusby.terrabungee.controller.command.instance.ListStaticCommand;
import com.noahhusby.terrabungee.controller.command.instance.RemoveStaticCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private static CommandManager instance = null;

    public static CommandManager getInstance() {
        if(instance == null) instance = new CommandManager();
        return instance;
    }

    private CommandManager() {
        register(new HelpCommand());
        register(new StopCommand());
        register(new ReloadCommand());
        register(new MigrateCommand());
        register(new AddStaticCommand());
        register(new RemoveStaticCommand());
        register(new ListStaticCommand());
        register(new DefaultServerCommand());
        register(new TestCommand());
    }

    private final List<ICommand> commands = new ArrayList<>();

    private void register(ICommand command) {
        commands.add(command);
    }

    public List<ICommand> getCommands() {
        return commands;
    }

    public boolean execute(String input) {
        String[] args = input.split(" ");
        for(ICommand c : commands) {
            if(c.getName().equalsIgnoreCase(args[0])) {
                c.execute(args);
                return true;
            }
        }

        return false;
    }
}
