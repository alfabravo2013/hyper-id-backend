package com.github.alfabravo2013.hyperidbackend.api;

import javax.validation.constraints.NotBlank;

public record HyperUserCredentials(
        @NotBlank(message = "Field 'username' cannot be empty")
        String username,
        @NotBlank(message = "Field 'password' cannot be empty")
        String password
) { }
