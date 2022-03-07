package net.buildtheearth.terrabungee.controller.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import net.buildtheearth.terrabungee.common.logging.LoggingConsoleLayout;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;

/**
 * @author Noah Husby
 */
public class JLineAppender extends AppenderBase<ILoggingEvent> {

    private final Layout<ILoggingEvent> layout = new LoggingConsoleLayout();

    @Override
    public void start() {
        super.start();
        layout.start();
    }

    @Override
    public void stop() {
        layout.stop();
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent event) {
        TerraBungeeController.getConsole().getReader().printAbove(layout.doLayout(event));
    }
}
