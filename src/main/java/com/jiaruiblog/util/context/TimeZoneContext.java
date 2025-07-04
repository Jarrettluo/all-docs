package com.jiaruiblog.util.context;

public class TimeZoneContext {
    private static final ThreadLocal<String> timeZoneHolder = new ThreadLocal<>();

    public static void setTimeZone(String timezone) {
        timeZoneHolder.set(timezone);
    }

    public static String getTimeZone() {
        return timeZoneHolder.get();
    }

    public static void clear() {
        timeZoneHolder.remove();
    }
}