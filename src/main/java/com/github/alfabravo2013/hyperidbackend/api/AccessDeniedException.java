package com.github.alfabravo2013.hyperidbackend.api;

public class AccessDeniedException extends RuntimeException {
    public static final String MESSAGE = "You don't have access rights. Please authorize.";

    public AccessDeniedException() {
        super(MESSAGE);
    }
}
