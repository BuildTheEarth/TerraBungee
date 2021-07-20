package net.buildtheearth.terrabungee.controller.command;

import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.terrabungee.controller.command.controller.HelpCommand;
import net.buildtheearth.terrabungee.controller.command.controller.MigrateCommand;
import net.buildtheearth.terrabungee.controller.command.controller.ReloadCommand;
import net.buildtheearth.terrabungee.controller.command.controller.StopCommand;
import net.buildtheearth.terrabungee.controller.command.instance.AddStaticCommand;
import net.buildtheearth.terrabungee.controller.command.instance.DefaultServerCommand;
import net.buildtheearth.terrabungee.controller.command.instance.ListStaticCommand;
import net.buildtheearth.terrabungee.controller.command.instance.RemoveStaticCommand;
import net.buildtheearth.terrabungee.controller.command.storage.LoadAllCommand;
import net.buildtheearth.terrabungee.controller.command.storage.SaveAllCommand;
import net.buildtheearth.terrabungee.controller.modules.Module;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements Module {
    private static CommandManager instance = null;

    public static CommandManager getInstance() {
        return instance == null ? instance = new CommandManager() : instance;
    }

    private CommandManager() {
    }

    private final List<Command> controllerCommands = new ArrayList<>();

    private void register(Command command) {
        controllerCommands.add(command);
    }

    public List<Command> getCommands() {
        return controllerCommands;
    }

    public boolean execute(String input) {
        if (TerraBungee.getInstance().getPluginManager().dispatchCommand(input)) {
            return true;
        }

        String[] args = input.split(" ");

        for (Command c : controllerCommands) {
            if (c.getName().equalsIgnoreCase(args[0])) {
                c.execute(args);
                return true;
            }
        }

        return false;
    }

    @Override
    public void onEnable() {
        register(new HelpCommand());
        register(new StopCommand());
        register(new ReloadCommand());
        register(new MigrateCommand());
        register(new AddStaticCommand());
        register(new RemoveStaticCommand());
        register(new ListStaticCommand());
        register(new DefaultServerCommand());
        register(new TestCommand());
        register(new SaveAllCommand());
        register(new LoadAllCommand());
    }

    @Override
    public void onDisable() {
        controllerCommands.clear();
    }

    @Override
    public String getModuleName() {
        return "Commands";
    }
}
