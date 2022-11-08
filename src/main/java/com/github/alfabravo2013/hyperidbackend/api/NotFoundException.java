package com.github.alfabravo2013.hyperidbackend.api;

public class NotFoundException extends RuntimeException {
    public static final String MESSAGE = "User not found.";

    public NotFoundException() {
        super(MESSAGE);
    }
}
