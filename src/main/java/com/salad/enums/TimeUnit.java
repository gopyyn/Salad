package com.salad.enums;

import java.time.Duration;

import static java.time.Duration.*;

public enum TimeUnit {
    MILLI_SECONDS,
    SECONDS,
    MINUTES,
    HOURS;


    public static final Duration getDuration(long time, TimeUnit unit) {
        switch (unit) {
            case SECONDS:
                return ofSeconds(time);
            case MINUTES:
                return ofMinutes(time);
            case HOURS:
                return ofHours(time);
            default:
                return ofMillis(time);
        }
    }
}
