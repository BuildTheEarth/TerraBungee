package net.buildtheearth.terrabungee.controller.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import net.buildtheearth.terrabungee.controller.TerraBungeeController;

public class JLineAppender extends AppenderBase<ILoggingEvent> {
    private final LoggingConsoleLayout layout = new LoggingConsoleLayout();

    @Override
    public void start() {
        layout.start();
        super.start();
    }

    @Override
    public void stop() {
        layout.stop();
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (TerraBungeeController.logger != null && TerraBungeeController.logger.getReader() != null) {
            TerraBungeeController.logger.getReader().printAbove(layout.doLayout(event));
        } else {
            System.out.print(layout.doLayout(event));
        }
    }
}
