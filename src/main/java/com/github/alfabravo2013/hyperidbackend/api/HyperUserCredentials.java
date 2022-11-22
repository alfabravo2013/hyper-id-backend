package com.github.alfabravo2013.hyperidbackend.api;

import javax.validation.constraints.NotBlank;

public record HyperUserCredentials(
        @NotBlank(message = "Field 'username' is empty")
        String username,
        @NotBlank(message = "Field 'password' is empty")
        String password
) { }
