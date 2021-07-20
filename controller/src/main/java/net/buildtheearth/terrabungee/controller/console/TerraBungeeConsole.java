package net.buildtheearth.terrabungee.controller.console;

import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.command.CommandManager;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TerraBungeeConsole extends Logger {
    public TerraBungeeConsole() {
        super("TerraBungee", null);
        setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tT %2$S]: %4$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                StringBuilder builder = new StringBuilder();
                if (lr.getLevel() == Level.INFO) {
                    builder.append(ConsoleColor.WHITE.toString());
                }
                if (lr.getLevel() == Level.WARNING) {
                    builder.append(ConsoleColor.YELLOW.toString());
                }
                if (lr.getLevel() == Level.SEVERE) {
                    builder.append(ConsoleColor.RED.toString());
                }
                builder.append(String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        Thread.currentThread().getName(),
                        lr.getMessage()));
                builder.append(ConsoleColor.RESET.toString());
                return builder.toString();
            }
        });

        addHandler(handler);

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
                        System.out.println(ConsoleColor.RED.toString() + "Unknown Command! Type `help` for more commands!" +
                                           ConsoleColor.RESET);
                    }
                } catch (Exception e) {
                    TerraBungee.getInstance().getLogger().log(Level.SEVERE, "There was an error executing the command!", e);
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
        System.out.println(builder.toString());
    }
}
