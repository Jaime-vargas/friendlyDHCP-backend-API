package com.app.dhcp.exeptionsHandler;

import com.app.dhcp.enums.HttpStatusError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class HandleException extends RuntimeException {
    private final int httpStatusCode;
    private final HttpStatus httpStatus;

    public HandleException(int httpStatusCode, HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
    }
}
