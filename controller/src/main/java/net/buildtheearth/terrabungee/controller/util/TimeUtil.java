package net.buildtheearth.terrabungee.controller.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Noah Husby
 */
@UtilityClass
public class TimeUtil {
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a yyyy/MM/dd");

    public static String toReadableTime(LocalDateTime date) {
        return date.format(timeFormat);
    }
}
