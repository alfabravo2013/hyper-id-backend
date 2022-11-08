package com.github.alfabravo2013.hyperidbackend.api;

public record ErrorDto(ErrorMsg error) {

    public static ErrorDto of(String message) {
        return new ErrorDto(new ErrorMsg(message));
    }

    public record ErrorMsg(String message) { }
}
