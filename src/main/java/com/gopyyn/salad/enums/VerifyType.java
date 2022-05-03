package com.gopyyn.salad.enums;


import io.cucumber.core.exception.CucumberException;

import static java.lang.String.format;

public enum VerifyType {
    PAGELOAD,
    CLICKABLE,
    NOT_CLICKABLE("!CLICKABLE"),
    VISIBLE,
    INVISIBLE("!VISIBLE"),
    ENABLED,
    DISABLED("!ENABLED");

    private String value;

    VerifyType() {
        this.value = this.name();
    }

    VerifyType(String value) {
        this.value = value;
    }

    public static VerifyType fromValue(String value) {
        value = value.replaceFirst("not ", "!");
        for (VerifyType verifyType : values()) {
            if (verifyType.value.equalsIgnoreCase(value) ||
                    verifyType.name().equalsIgnoreCase(value)) {
                return verifyType;
            }
        }
        throw new CucumberException(format("Invalid operation %s", value));
    }
}
