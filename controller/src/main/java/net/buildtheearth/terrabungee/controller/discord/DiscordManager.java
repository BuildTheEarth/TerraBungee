package net.buildtheearth.terrabungee.controller.discord;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.noahhusby.lib.data.storage.StorageHashMap;
import com.noahhusby.lib.data.storage.events.EventListener;
import com.noahhusby.lib.data.storage.events.transfer.StorageLoadEvent;
import lombok.Getter;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.discord.UserPermission;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordButtonCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.punishments.PunishmentsDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.setup.SetupDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.util.ListDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.util.MsgDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.util.PingDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.util.ReviewDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.util.StatusDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.embeds.ControllerStoppedEmbed;
import net.buildtheearth.terrabungee.controller.modules.Module;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DiscordManager extends Module {
    private static DiscordManager instance = null;

    public static DiscordManager getInstance() {
        return instance == null ? instance = new DiscordManager() : instance;
    }

    private final Map<String, IDiscordCommand> discordCommands = Maps.newHashMap();

    @Getter
    private final StorageHashMap<Long, GuildConfig> guildConfigs = new StorageHashMap<>(GuildConfig.class);

    @Getter
    private final StorageHashMap<Integer, BotConfig> botConfigs = new StorageHashMap<>(BotConfig.class);

    private DiscordManager() {
        super("Discord");
        registerCommands(
                new PunishmentsDiscordCommand(),
                new SetupDiscordCommand(),
                new PingDiscordCommand(),
                new StatusDiscordCommand(),
                new ReviewDiscordCommand(),
                new ListDiscordCommand(),
                new MsgDiscordCommand()
        );
        botConfigs.events().register(new EventListener<BotConfig>() {
            @Override
            public void onLoad(StorageLoadEvent<BotConfig> event) {
                startBots();
                TerraBungeeController.getInstance().getGeneralThreads().schedule(() -> updateSlashCommands(), 10, TimeUnit.SECONDS);
            }
        });
    }

    public void startBots() {
        for (BotConfig config : botConfigs.values()) {
            try {
                config.initBot();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopBots() {
        for (BotConfig config : botConfigs.values()) {
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
        try {
            for (GuildConfig guildConfig : guildConfigs.values()) {
                if (guildConfig.isConfigured() && guildConfig.getNotificationTextChannel() != null) {
                    guildConfig.getNotificationTextChannel().sendMessageEmbeds(buildEmbed(emb::build)).submit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void updateSlashCommands(BotConfig config) {
        for (GuildConfig guildConfig : getGuildsFromBot(config)) {
            updateSlashCommands(guildConfig);
        }
    }

    public void updateSlashCommands(GuildConfig config) {
        try {
            CommandListUpdateAction slashCommands = config.getGuild().updateCommands();
            for (IDiscordCommand command : discordCommands.values()) {
                CommandData commandData = new CommandData(command.getName(), command.getDescription());
                command.configureData(commandData);
                slashCommands.addCommands(commandData);
            }
            slashCommands.queue();
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            TerraBungee.getInstance().getLogger().warning("Failed to update slash commands for discord!");
        }
    }

    public void updateSlashCommands() {
        for (GuildConfig guildConfig : guildConfigs.values()) {
            updateSlashCommands(guildConfig);
        }
    }

    public List<GuildConfig> getGuildsFromBot(BotConfig config) {
        List<GuildConfig> tempGuilds = Lists.newArrayList();
        for (GuildConfig guildConfig : guildConfigs.values()) {
            if (guildConfig.getBotId() == config.getId()) {
                tempGuilds.add(guildConfig);
            }
        }
        return tempGuilds;
    }

    public void executeSlashCommand(String name, UserPermission permission, User user, OffsetDateTime executionTime, SlashCommandEvent event) {
        //TODO: Replace this bullshit
        if (event.getMember() == null) {
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

    public BotConfig createBot(String name) {
        int id = botConfigs.size();
        while (botConfigs.containsKey(id)) {
            id++;
        }
        BotConfig config = new BotConfig(id);
        config.setName(name);
        botConfigs.put(id, config);
        botConfigs.saveAsync();
        return config;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        send(new ControllerStoppedEmbed());
        stopBots();
    }

    @Override
    public String getModuleName() {
        return "Discord";
    }
}

