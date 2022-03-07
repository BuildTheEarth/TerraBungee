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

    public TerraBungeeConsole() {
        logger = LoggerFactory.getLogger(TerraBungeeController.class);
    }

    public void start() {
        try {
            LineReader reader = LineReaderBuilder.builder().terminal(TerminalBuilder.terminal()).build();
            String line;
            while ((line = reader.readLine("> ")) != null && TerraBungeeController.getInstance().isRunning()) {
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
                    e.printStackTrace();
                    System.out.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
