package com.noahhusby.terrabungee.controller.console;

import com.noahhusby.terrabungee.controller.TerraBungeeController;
import com.noahhusby.terrabungee.controller.command.CommandManager;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class TerraBungeeConsole extends Logger {
    public TerraBungeeConsole() {
        super("TerraBungee", null);
        setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tT %2$S] [%3$s]: %4$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                StringBuilder builder = new StringBuilder();
                if(lr.getLevel() == Level.INFO) builder.append(ConsoleColor.WHITE.toString());
                if(lr.getLevel() == Level.WARNING) builder.append(ConsoleColor.YELLOW.toString());
                if(lr.getLevel() == Level.SEVERE) builder.append(ConsoleColor.RED.toString());
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
            while(( line = reader.readLine( "> " ) ) != null && TerraBungeeController.isTerraBungeeRunning) {
                if(line.equalsIgnoreCase("")) continue;
                if(!CommandManager.getInstance().execute(line)) System.out.println(ConsoleColor.RED.toString() + "Unknown Command! Type `help` for more commands!" +
                        ConsoleColor.RESET);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(TextComponent... components) {
        StringBuilder builder = new StringBuilder();
        for(TextComponent t : components) {
            builder.append(t.color.toString());
            builder.append(t.text);
            builder.append(ConsoleColor.RESET.toString());
        }

        System.out.println(builder.toString());
    }
}
