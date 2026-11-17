package net.buildtheearth.terrabungee.controller.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import net.buildtheearth.api.util.ConsoleColor;

import java.util.Date;

public class LoggingConsoleLayout extends LayoutBase<ILoggingEvent> {
    @Override
    public String doLayout(ILoggingEvent event) {
        ConsoleColor color = switch (event.getLevel().levelInt) {
            case Level.ERROR_INT -> ConsoleColor.RED;
            case Level.WARN_INT -> ConsoleColor.YELLOW;
            default -> ConsoleColor.WHITE;
        };
        String output = String.format("[%1$tT %2$S] [%3$s]: %4$s%n",
                new Date(event.getTimeStamp()),
                event.getLevel(),
                event.getLoggerName(),
                event.getFormattedMessage());
        if (event.getThrowableProxy() != null) {
            output += ThrowableProxyUtil.asString(event.getThrowableProxy()) + System.lineSeparator();
        }
        return color + output + ConsoleColor.RESET;
    }
}
