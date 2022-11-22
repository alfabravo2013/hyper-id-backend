package com.github.alfabravo2013.hyperidbackend.api;

import javax.validation.constraints.NotEmpty;

public record HyperUserCredentials(
        @NotEmpty(message = "Field 'username' cannot be empty")
        String username,
        @NotEmpty(message = "Field 'password' cannot be empty")
        String password
) { }
