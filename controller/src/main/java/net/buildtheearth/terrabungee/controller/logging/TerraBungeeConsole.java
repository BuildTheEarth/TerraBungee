package net.buildtheearth.terrabungee.controller.logging;

import lombok.Getter;
import net.buildtheearth.terrabungee.common.logging.ConsoleColor;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.command.CommandManager;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TerraBungeeConsole {

    @Getter
    private final Logger logger;

    @Getter
    private LineReader reader;

    public TerraBungeeConsole() {
        logger = LoggerFactory.getLogger(TerraBungeeController.class);
        try {
            reader = LineReaderBuilder.builder().terminal(TerminalBuilder.terminal()).build();
        } catch (IOException e) {
            getLogger().error("Failed to create terminal for TerraBungee.", e);
        }
    }

    public void start() {
        try {
            reader = LineReaderBuilder.builder().terminal(TerminalBuilder.terminal()).build();
            String line;
            while ((line = reader.readLine("terra> ")) != null && TerraBungeeController.getInstance().isRunning()) {
                if (line.equalsIgnoreCase("")) {
                    continue;
                }
                try {
                    if (!CommandManager.getInstance().execute(line)) {
                        System.out.println(ConsoleColor.RED + "Unknown Command! Type `help` for more commands!" +
                                           ConsoleColor.RESET);
                    }
                } catch (Exception e) {
                    getLogger().error("There was an error executing the command!", e);
                    System.out.println();
                }
            }
        } catch (IOException e) {
            getLogger().error("Error while handling console.", e);
        }
    }

    public static void sendMessage(Object... objects) {
        StringBuilder builder = new StringBuilder();
        for (Object o : objects) {
            builder.append(o.toString());
        }

        builder.append(ConsoleColor.RESET);
        System.out.println(builder);
    }
}
