package net.buildtheearth.terrabungee.controller.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

/**
 * @author Noah Husby
 */
public class LoggerContextUtil {
    private static final LoggerContext global = new LoggerContext();

    public static Logger getLogger(String name) {
        if(LoggerFactory.getLogger(name) instanceof Logger) {
            return (Logger) LoggerFactory.getLogger(name);
        }
        return global.getLogger(name);
    }

    public static void setLevel(String name, Level level) {
        getLogger(name).setLevel(level);
    }
}
