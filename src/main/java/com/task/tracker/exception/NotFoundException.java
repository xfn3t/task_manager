package com.task.tracker.exception;

import org.springframework.security.core.AuthenticationException;

public class NotFoundException extends AuthenticationException {
    public NotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NotFoundException(String msg) {
        super(msg);
    }
}
