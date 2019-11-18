package com.salad.enums;

import cucumber.runtime.CucumberException;

import static java.lang.String.format;

/**
 *
 * @author pthomas3
 */
public enum MatchType {
    
    EQUALS("=="),
    NOT_EQUALS("!="),
    CONTAINS("contains"),
    NOT_CONTAINS("!contains"),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_OR_EQUAL_TO(">="),
    LESS_THAN_OR_EQUAL_TO("<="),
    ANY("any"),
    ALL("all");

    private final String value;

    MatchType(String value) {
       this.value = value;
    }

    public static MatchType fromValue(String value) {
        for (MatchType matchType : values()) {
            if (matchType.value.equalsIgnoreCase(value) ||
                    matchType.name().equalsIgnoreCase(value)) {
                return matchType;
            }
        }
        throw new CucumberException(format("Invalid operator %s", value));
    }

    public String getValue() {
        return value;
    }
}


