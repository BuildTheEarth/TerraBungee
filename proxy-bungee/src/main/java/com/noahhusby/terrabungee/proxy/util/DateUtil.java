package com.noahhusby.terrabungee.proxy.util;

import lombok.experimental.UtilityClass;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Noah Husby
 */
@UtilityClass
public class DateUtil {
    private static final Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?", Pattern.CASE_INSENSITIVE);

    public static long parseDateDiff(String time, boolean future) throws Exception {
        return parseDateDiff(time, future, false);
    }

    public static long parseDateDiff(String time, boolean future, boolean emptyEpoch) throws Exception {
        final Matcher m = timePattern.matcher(time);
        int days = 0;
        int hours = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                if (m.group(1) != null && !m.group(1).isEmpty()) {
                    days = Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty()) {
                    hours = Integer.parseInt(m.group(2));
                }
                break;
            }
        }
        if (!found) {
            throw new Exception();
        }
        final Calendar c = new GregorianCalendar();

        if (emptyEpoch) {
            c.setTimeInMillis(0);
        }

        if (days > 0) {
            c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
        }
        if (hours > 0) {
            c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
        }
        final Calendar max = new GregorianCalendar();
        max.add(Calendar.YEAR, 10);
        if (c.after(max)) {
            return max.getTimeInMillis();
        }
        return c.getTimeInMillis();
    }

    public static String getExpandedTimeMessage(long millis) {
        StringBuilder builder = new StringBuilder();
        long days = TimeUnit.MILLISECONDS.toDays(millis) % 365;
        if (days > 0) {
            builder.append(days).append("d").append(" ");
        }
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        if (hours > 0) {
            builder.append(hours).append("h").append(" ");
        }
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        if (minutes > 0) {
            builder.append(minutes).append("m").append(" ");
        }
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        if (seconds > 0) {
            builder.append(seconds).append("s").append(" ");
        }
        return builder.toString().trim();
    }
}
