package net.buildtheearth.api.plugin;

import net.buildtheearth.api.util.ConsoleColor;

import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Noah Husby
 */
public class PluginLogger extends Logger {
    public PluginLogger(String pluginName) {
        super(pluginName, null);
        setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            private final String format = "[%1$tT %2$S] [" + pluginName + "]: %4$s %n";

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
}
