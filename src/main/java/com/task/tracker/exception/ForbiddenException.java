package com.task.tracker.exception;

import java.io.IOException;

public class ForbiddenException extends IOException {
    public ForbiddenException(String message) {
        super("Forbidden exception: " + message);
    }

    public ForbiddenException() {
        super("Forbidden exception");
    }
}
