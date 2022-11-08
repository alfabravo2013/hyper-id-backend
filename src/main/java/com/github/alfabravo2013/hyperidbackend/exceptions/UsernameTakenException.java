package com.github.alfabravo2013.hyperidbackend.exceptions;

public class UsernameTakenException extends RuntimeException {
    public static final String MESSAGE = "This username already used by someone else.";

    public UsernameTakenException() {
        super(MESSAGE);
    }
}
