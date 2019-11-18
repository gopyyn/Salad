package com.salad.enums;

import java.time.Duration;

import static java.time.Duration.*;

public enum TimeUnit {
    MILLI_SECOND,
    SECOND,
    MINUTE,
    HOUR;


    public static final Duration getDuration(long time, TimeUnit unit) {
        switch (unit) {
            case SECOND:
                return ofSeconds(time);
            case MINUTE:
                return ofMinutes(time);
            case HOUR:
                return ofHours(time);
            default:
                return ofMillis(time);
        }
    }
}
