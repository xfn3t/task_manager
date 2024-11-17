package com.task.tracker.exception;

import java.io.IOException;

public class ExistsException extends IOException {
    public ExistsException(String message) {
        super(message + " exists exception");
    }

    public ExistsException() {
        super("Entity exists exception");
    }
}
