package com.app.dhcp.enums;

import lombok.Getter;

@Getter
public enum FormatExceptionEnum {
    // NAME
    FORMAT_EXCEPTION("Wrong resource format");

    final String message;

    FormatExceptionEnum(String message) {
        this.message = message;
    }
}
