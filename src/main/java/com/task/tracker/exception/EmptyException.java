package com.task.tracker.exception;

import java.io.IOException;

public class EmptyException extends IOException {
    public EmptyException(String message) {
        super("Entity empty: " + message);
    }

    public EmptyException() {
        super("This entity is empty");
    }
}
