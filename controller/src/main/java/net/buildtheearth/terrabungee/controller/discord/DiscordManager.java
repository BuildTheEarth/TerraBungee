package net.buildtheearth.terrabungee.controller.discord;

import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.noahhusby.lib.data.JsonUtils;
import com.noahhusby.lib.data.storage.StorageList;
import lombok.Getter;
import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.discord.UserPermission;
import net.buildtheearth.terrabungee.common.TerraBungeeUtil;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.config.ConfigHandler;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordButtonCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.IDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.punishments.PunishmentsDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.setup.SetupDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.util.PingDiscordCommand;
import net.buildtheearth.terrabungee.controller.discord.commands.util.StatusDiscordCommand;
import net.buildtheearth.terrabungee.controller.modules.Module;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.security.auth.login.LoginException;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DiscordManager implements Module {
    private static DiscordManager instance = null;

    public static DiscordManager getInstance() {
        return instance == null ? instance = new DiscordManager() : instance;
    }

    private JDA bot;
    private TextChannel channel;
    private final ExecutorService botThread = TerraBungeeUtil.newSingleThreadExecutor("terrabungee-bot");
    @Getter
    private final StorageList<DiscordConfig> discordConfigs = new StorageList<>(DiscordConfig.class);

    private final Map<String, IDiscordCommand> discordCommands = Maps.newHashMap();

    private DiscordManager() {
        registerCommands(
                new PunishmentsDiscordCommand(),
                new SetupDiscordCommand(),
                new PingDiscordCommand(),
                new StatusDiscordCommand()
        );
    }

    public void loadBot() {
        botThread.submit(() -> {
            try {
                bot = JDABuilder.createDefault(ConfigHandler.botToken)
                        .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                        .enableIntents(GatewayIntent.GUILD_MESSAGES)
                        .addEventListeners(new DiscordListener()).build();
            } catch (LoginException e) {
                TerraBungeeController.logger.warning("Failed to initialize discord bot! Please check the token and try again.");
            }
            TerraBungeeController.getInstance().getGeneralThreads().schedule(() -> {
                Guild g = bot.getGuildById(ConfigHandler.guildID);
                if (g == null) {
                    return;
                }
                boolean adminRole = false;
                for (Role r : g.getRoles()) {
                    if (r.getName().equalsIgnoreCase("TBAdmin")) {
                        adminRole = true;
                    }
                }
                if (!adminRole) {
                    g.createRole().setMentionable(true).setName("TBAdmin").submit();
                }
                updateSlashCommands();
            }, 2, TimeUnit.SECONDS);
        });
    }

    public JDA getBot() {
        return bot;
    }

    public MessageEmbed buildEmbed(Consumer<EmbedBuilder> builder) {
        EmbedBuilder e = new EmbedBuilder();
        e.setTimestamp(new Date().toInstant());
        e.setFooter("TerraBungee by Noah Husby");
        builder.accept(e);
        return e.build();
    }

    public void send(IMessageEmbed emb) {
        if (ConfigHandler.botToken.equalsIgnoreCase("")) {
            return;
        }
        botThread.submit(() -> {
            if (channel == null) {
                Guild g = bot.getGuildById(ConfigHandler.guildID);
                if (g == null) {
                    return;
                }
                channel = g.getTextChannelById(ConfigHandler.channelID);
                if (channel == null) {
                    return;
                }
            }

            channel.sendMessage(buildEmbed(emb::build)).submit();
        });
    }

    public DiscordConfig getConfigByGuild(Guild guild) {
        for (DiscordConfig config : getDiscordConfigs()) {
            if (guild.getIdLong() == config.getGuildId()) {
                return config;
            }
        }
        return null;
    }

    public void registerCommand(IDiscordCommand command) {
        discordCommands.put(command.getName(), command);
    }

    public void registerCommands(IDiscordCommand... commands) {
        for(IDiscordCommand command : commands) {
            registerCommand(command);
        }
    }

    public void updateSlashCommands() {
        try {
            CommandListUpdateAction slashCommands = getBot().getGuildById(ConfigHandler.guildID).updateCommands();
            for(IDiscordCommand command : discordCommands.values()) {
                CommandData commandData = new CommandData(command.getName(), command.getDescription());
                command.configureData(commandData);
                slashCommands.addCommands(commandData);
            }
            slashCommands.queue();
        } catch (NullPointerException ignored) {
            TerraBungee.getInstance().getLogger().warning("Failed to update slash commands for discord!");
        }
    }

    public void executeSlashCommand(String name, UserPermission permission, User user, OffsetDateTime executionTime, SlashCommandEvent event) {
        IDiscordCommand command = discordCommands.get(name);
        if(command != null) {
            command.execute(user, permission, executionTime, event);
        }
    }

    public void executeButtonCommand(ButtonClickEvent event) {
        JsonObject data = TerraBungeeUtil.parse(event.getComponentId());
        IDiscordCommand command = discordCommands.get(data.get("name").getAsString());
        if(command instanceof IDiscordButtonCommand) {
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
        if (ConfigHandler.botToken.equalsIgnoreCase("")) {
            return;
        }
        loadBot();
    }

    @Override
    public void onDisable() {
        if(bot != null) {
            bot.shutdown();
        }
    }

    @Override
    public String getModuleName() {
        return "Discord";
    }
}

