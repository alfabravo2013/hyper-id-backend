package com.github.alfabravo2013.hyperidbackend.exceptions;

public class FailedAuthException extends RuntimeException {
    public static final String MESSAGE = "Username or password don't match any known.";

    public FailedAuthException() {
        super(MESSAGE);
    }
}
