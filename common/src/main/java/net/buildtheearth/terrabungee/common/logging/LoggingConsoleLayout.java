package net.buildtheearth.terrabungee.common.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import com.google.common.collect.Maps;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author Noah Husby
 */
public class LoggingConsoleLayout extends LayoutBase<ILoggingEvent> {

    private static final Map<Level, ConsoleColor> levelColors = Maps.newHashMap();
    private static final DateFormat df;

    static {
        levelColors.put(Level.DEBUG, ConsoleColor.WHITE);
        levelColors.put(Level.INFO, ConsoleColor.WHITE);
        levelColors.put(Level.WARN, ConsoleColor.YELLOW);
        levelColors.put(Level.ERROR, ConsoleColor.RED);
        df = new SimpleDateFormat("HH:mm:ss.SSS");
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        event.getLoggerName();
        StringBuilder builder = new StringBuilder(levelColors.get(event.getLevel()).toString());
        builder.append(String.format("[%1$tT %2$S]", new Date(event.getTimeStamp()), event.getLevel()));
        if (!event.getLoggerName().equals("TerraBungee")) {
            builder.append(" [").append(event.getLoggerName()).append("]");
        }
        builder.append(": ").append(event.getFormattedMessage()).append(ConsoleColor.RESET).append("\n");
        return builder.toString();
    }
}
