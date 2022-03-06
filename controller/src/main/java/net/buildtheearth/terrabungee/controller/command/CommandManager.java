package net.buildtheearth.terrabungee.controller.command;

import com.google.common.collect.Lists;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.plugin.Command;
import net.buildtheearth.terrabungee.controller.command.controller.HelpCommand;
import net.buildtheearth.terrabungee.controller.command.controller.MigrateCommand;
import net.buildtheearth.terrabungee.controller.command.controller.ReloadCommand;
import net.buildtheearth.terrabungee.controller.command.controller.StopCommand;
import net.buildtheearth.terrabungee.controller.command.controller.VersionCommand;
import net.buildtheearth.terrabungee.controller.command.discord.DiscordConfigureBotCommand;
import net.buildtheearth.terrabungee.controller.command.discord.DiscordConfigureGuildCommand;
import net.buildtheearth.terrabungee.controller.command.discord.DiscordCreateBotCommand;
import net.buildtheearth.terrabungee.controller.command.discord.DiscordCreateGuildCommand;
import net.buildtheearth.terrabungee.controller.command.discord.DiscordEnableBotCommand;
import net.buildtheearth.terrabungee.controller.command.discord.DiscordListBotsCommand;
import net.buildtheearth.terrabungee.controller.command.discord.DiscordListGuildsCommand;
import net.buildtheearth.terrabungee.controller.command.discord.DiscordRefreshSlashCommand;
import net.buildtheearth.terrabungee.controller.command.discord.DiscordRemoveBotCommand;
import net.buildtheearth.terrabungee.controller.command.discord.DiscordRemoveGuildCommand;
import net.buildtheearth.terrabungee.controller.command.instance.AddStaticCommand;
import net.buildtheearth.terrabungee.controller.command.instance.DefaultServerCommand;
import net.buildtheearth.terrabungee.controller.command.instance.ListStaticCommand;
import net.buildtheearth.terrabungee.controller.command.instance.RemoveStaticCommand;
import net.buildtheearth.terrabungee.controller.command.security.ReloadWhitelistCommand;
import net.buildtheearth.terrabungee.controller.command.storage.LoadAllCommand;
import net.buildtheearth.terrabungee.controller.command.storage.SaveAllCommand;
import net.buildtheearth.terrabungee.controller.modules.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CommandManager extends Module {
    private static CommandManager instance = null;

    public static CommandManager getInstance() {
        return instance == null ? instance = new CommandManager() : instance;
    }

    private CommandManager() {
        super("commands");
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
                c.execute(Arrays.copyOfRange(args, 1, args.length));
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
        register(new DiscordCreateBotCommand());
        register(new DiscordListBotsCommand());
        register(new DiscordConfigureBotCommand());
        register(new DiscordEnableBotCommand());
        register(new DiscordRemoveBotCommand());
        register(new DiscordCreateGuildCommand());
        register(new DiscordConfigureGuildCommand());
        register(new DiscordRemoveGuildCommand());
        register(new DiscordListGuildsCommand());
        register(new DiscordRefreshSlashCommand());
        register(new ReloadWhitelistCommand());
        register(new VersionCommand());
    }

    @Override
    public void onDisable() {
        controllerCommands.clear();
    }

    @Override
    public List<String> getRequiredModules() {
        return Lists.newArrayList();
    }
}
