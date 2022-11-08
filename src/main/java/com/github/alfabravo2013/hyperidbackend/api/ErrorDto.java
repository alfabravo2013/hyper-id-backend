package com.github.alfabravo2013.hyperidbackend.api;

public record ErrorDto(ErrorMsg error) {
    public static final String REGISTER_ERR = "This username already used by someone else.";
    public static final String LOGIN_ERR = "Username or password don't match any known.";

    public static ErrorDto of(String message) {
        return new ErrorDto(new ErrorMsg(message));
    }

    public record ErrorMsg(String message) { }
}
