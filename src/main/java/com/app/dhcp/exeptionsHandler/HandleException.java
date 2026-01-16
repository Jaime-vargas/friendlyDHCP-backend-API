package com.app.dhcp.exeptionsHandler;

import com.app.dhcp.enums.HttpStatusError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class HandleException extends RuntimeException {
    private final HttpStatus httpStatusCode;
    private final HttpStatusError httpStatusError;

    public HandleException(HttpStatus httpStatusCode, HttpStatusError httpStatusError, String message) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.httpStatusError = httpStatusError;
    }
}
