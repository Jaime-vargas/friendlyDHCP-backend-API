package com.app.dhcp.exeption;

import com.app.dhcp.enums.HttpStatusError;
import com.app.dhcp.exeptionsHandler.HandleException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FormatException extends HandleException {

    public FormatException(String message) {
        super(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT, message);
    }
}
