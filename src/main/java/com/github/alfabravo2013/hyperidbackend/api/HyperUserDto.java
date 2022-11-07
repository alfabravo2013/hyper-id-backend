package com.github.alfabravo2013.hyperidbackend.api;

public record HyperUserDto(String username, String name, String surname) {

    public static HyperUserDto of(HyperUser user) {
        return new HyperUserDto(user.getUsername(), user.getName(), user.getSurname());
    }
}
