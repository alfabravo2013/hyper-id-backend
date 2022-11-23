package com.github.alfabravo2013.hyperidbackend.api;

public record ErrorDto(ErrorMsg error) {

    public static ErrorDto of(String message) {
        return new ErrorDto(new ErrorMsg(message));
    }

    public record ErrorMsg(String message) {
        @Override
        public String toString() {
            return "{\"message\": \"" + message + "\"}";
        }
    }

    @Override
    public String toString() {
        return "{\"error\": " + error.toString() + '}';
    }
}
