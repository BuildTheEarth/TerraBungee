package net.buildtheearth.terrabungee.controller.console;

import net.buildtheearth.api.TerraBungee;
import net.buildtheearth.api.util.ConsoleColor;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;
import net.buildtheearth.terrabungee.controller.command.CommandManager;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

public class TerraBungeeConsole extends Logger {
    private LineReader reader;

    public TerraBungeeConsole() {
        super("TerraBungee", null);
        setUseParentHandlers(false);

        addHandler(new Handler() {
            private final org.slf4j.Logger delegate = LoggerFactory.getLogger("TerraBungee");

            @Override
            public void publish(LogRecord record) {
                if (!isLoggable(record)) {
                    return;
                }
                String message = record.getMessage();
                Object[] parameters = record.getParameters();
                if (parameters != null && parameters.length > 0) {
                    message = java.text.MessageFormat.format(message, parameters);
                }
                switch (record.getLevel().getName()) {
                    case "SEVERE" -> delegate.error(message, record.getThrown());
                    case "WARNING" -> delegate.warn(message, record.getThrown());
                    default -> delegate.info(message, record.getThrown());
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        });
    }

    public org.jline.reader.LineReader getReader() {
        return reader;
    }

    public void start() {
        try {
            reader = LineReaderBuilder.builder().terminal(TerminalBuilder.terminal()).build();
            String line;
            while ((line = reader.readLine("> ")) != null && TerraBungeeController.getInstance().isRunning()) {
                if (line.equalsIgnoreCase("")) {
                    continue;
                }
                try {
                    if (!CommandManager.getInstance().execute(line)) {
                        warning("Unknown command. Type `help` for more commands.");
                    }
                } catch (Exception e) {
                    TerraBungee.getInstance().getLogger().log(Level.SEVERE, "There was an error executing the command!", e);
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
        if (TerraBungeeController.logger != null && TerraBungeeController.logger.getReader() != null) {
            TerraBungeeController.logger.getReader().printAbove(builder.toString());
        } else {
            System.out.println(builder);
        }
    }
}
