package net.buildtheearth.terrabungee.common.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;

import java.util.Date;

/**
 * @author Noah Husby
 */
public class LoggingFileLayout extends LayoutBase<ILoggingEvent> {

    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("[%1$tT %2$S]", new Date(event.getTimeStamp()), event.getLevel()));
        if (!event.getLoggerName().equals("TerraBungee") && !event.getLoggerName().equals("net.buildtheearth.terrabungee.controller.TerraBungeeController")) {
            builder.append(" [").append(event.getLoggerName()).append("]");
        }
        builder.append(": ").append(event.getFormattedMessage()).append("\n");
        return builder.toString();
    }
}
