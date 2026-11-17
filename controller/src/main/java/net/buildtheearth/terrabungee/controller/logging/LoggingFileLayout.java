package net.buildtheearth.terrabungee.controller.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.LayoutBase;

import java.util.Date;

public class LoggingFileLayout extends LayoutBase<ILoggingEvent> {
    @Override
    public String doLayout(ILoggingEvent event) {
        String output = String.format("[%1$tT %2$S] [%3$s]: %4$s%n",
                new Date(event.getTimeStamp()),
                event.getLevel(),
                event.getLoggerName(),
                event.getFormattedMessage());
        if (event.getThrowableProxy() != null) {
            output += ThrowableProxyUtil.asString(event.getThrowableProxy()) + System.lineSeparator();
        }
        return output;
    }
}
