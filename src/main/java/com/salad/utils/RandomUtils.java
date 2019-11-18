package com.salad.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

public class RandomUtils {
    public static final int DEFAULT_MIN = 2;
    public static final int DEFAULT_MAX = 6;
    public static final Random random = ThreadLocalRandom.current();

    private RandomUtils(){}

    public static String string() {
        return RandomStringUtils.randomAlphabetic(number(DEFAULT_MIN, DEFAULT_MAX));
    }

    public static String string(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public static String numeric() {
        return randomNumeric(number(DEFAULT_MIN, DEFAULT_MAX));
    }

    public static String numeric(int count) {
        return randomNumeric(count);
    }

    public static String alphanumeric() {
        return randomAlphanumeric(number(DEFAULT_MIN, DEFAULT_MAX));
    }

    public static String alphanumeric(int count) {
        return randomAlphanumeric(count);
    }

    public static int number() {
        return random.nextInt();
    }

    public static int number(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static float decimal() {
        return random.nextFloat();
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
