package net.buildtheearth.terrabungee.controller.discord;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.noahhusby.lib.data.storage.StorageHashMap;
import com.noahhusby.lib.data.storage.StorageList;
import lombok.Getter;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.discord.UserPermission;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.controller.config.ConfigHandler;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordButtonCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.punishments.PunishmentsDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.setup.SetupDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.util.PingDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.util.StatusDiscordCommand;
import net.buildtheearth.terrabungee.controller.modules.Module;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class DiscordManager implements Module {
    private static DiscordManager instance = null;

    public static DiscordManager getInstance() {
        return instance == null ? instance = new DiscordManager() : instance;
    }

    private final ExecutorService botThread = TerraBungeeUtil.newSingleThreadExecutor("terrabungee-bot");

    private final Map<String, IDiscordCommand> discordCommands = Maps.newHashMap();

    @Getter
    private final StorageHashMap<Long, GuildConfig> guildConfigs = new StorageHashMap<>(Long.class, GuildConfig.class);

    @Getter
    private final StorageHashMap<Integer, BotConfig> botConfigs = new StorageHashMap<>(Integer.class, BotConfig.class);

    private DiscordManager() {
        registerCommands(
                new PunishmentsDiscordCommand(),
                new SetupDiscordCommand(),
                new PingDiscordCommand(),
                new StatusDiscordCommand()
        );
        botConfigs.onLoadEvent(() -> botThread.submit(() -> {
            startBots();
            updateSlashCommands();
        }));
    }

    public void startBots() {
        for(BotConfig config : botConfigs.values()) {
            config.initBot();
        }
    }

    public void stopBots() {
        for(BotConfig config : botConfigs.values()) {
            config.shutdown();
        }
    }

    public MessageEmbed buildEmbed(Consumer<EmbedBuilder> builder) {
        EmbedBuilder e = new EmbedBuilder();
        e.setTimestamp(new Date().toInstant());
        e.setFooter("TerraBungee by Noah Husby");
        builder.accept(e);
        return e.build();
    }

    public void send(IMessageEmbed emb) {
        botThread.submit(() -> {
            for(GuildConfig guildConfig : guildConfigs.values()) {
                if(guildConfig.getNotificationChannel() != null) {
                    guildConfig.getNotificationChannel().sendMessage(buildEmbed(emb::build)).submit();
                }
            }
        });
    }

    public GuildConfig getConfigByGuild(Guild guild) {
        return guildConfigs.get(guild.getIdLong());
    }

    public void registerCommand(IDiscordCommand command) {
        discordCommands.put(command.getName(), command);
    }

    public void registerCommands(IDiscordCommand... commands) {
        for (IDiscordCommand command : commands) {
            registerCommand(command);
        }
    }

    public void updateSlashCommands() {
        for(GuildConfig guildConfig : guildConfigs.values()) {
            try {
                CommandListUpdateAction slashCommands = guildConfig.getGuild().updateCommands();
                for (IDiscordCommand command : discordCommands.values()) {
                    CommandData commandData = new CommandData(command.getName(), command.getDescription());
                    command.configureData(commandData);
                    slashCommands.addCommands(commandData);
                }
                slashCommands.queue();
            } catch (NullPointerException ignored) {
                TerraBungee.getInstance().getLogger().warning("Failed to update slash commands for discord!");
            }
        }
    }

    public void executeSlashCommand(String name, UserPermission permission, User user, OffsetDateTime executionTime, SlashCommandEvent event) {
        //TODO: Replace this bullshit
        if (event.getMember() == null) {
            return;
        }
        boolean tempPerms = false;
        for (Role r : event.getMember().getRoles()) {
            if (r.getName().equalsIgnoreCase("moderator") || r.getName().equalsIgnoreCase("administrator") || r.getName().equalsIgnoreCase("owner")) {
                tempPerms = true;
            }
        }
        if (!tempPerms) {
            event.reply("You don't have permission to run this command!").setEphemeral(true).submit();
            return;
        }
        IDiscordCommand command = discordCommands.get(name);
        if (command != null) {
            command.execute(user, permission, executionTime, event);
        }
    }

    public void executeButtonCommand(ButtonClickEvent event) {
        //TODO: Replace this bullshit
        if (event.getMember() == null) {
            return;
        }
        boolean tempPerms = false;
        for (Role r : event.getMember().getRoles()) {
            if (r.getName().equalsIgnoreCase("moderator") || r.getName().equalsIgnoreCase("administrator") || r.getName().equalsIgnoreCase("owner")) {
                tempPerms = true;
            }
        }
        if (!tempPerms) {
            event.reply("You don't have permission to run this command!").setEphemeral(true).submit();
            return;
        }
        JsonObject data = TerraBungeeUtil.parse(event.getComponentId());
        IDiscordCommand command = discordCommands.get(data.get("name").getAsString());
        if (command instanceof IDiscordButtonCommand) {
            ((IDiscordButtonCommand) command).onButtonEvent(data, event);
        }
    }

    public Button generateButtonInteraction(IDiscordCommand command, ButtonStyle style, JsonObject data, String label) {
        data.addProperty("name", command.getName());
        String dataString = TerraBungeeUtil.GSON.toJson(data);
        return Button.of(style, dataString, label);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        stopBots();
    }

    @Override
    public String getModuleName() {
        return "Discord";
    }
}

