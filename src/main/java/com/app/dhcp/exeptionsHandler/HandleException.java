package com.app.dhcp.exeptionsHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class HandleException extends RuntimeException {
    private final int status;
    private final String error;

    public HandleException(int status, String error, String message) {
        super(message);
        this.status = status;
        this.error = error;
    }
}
