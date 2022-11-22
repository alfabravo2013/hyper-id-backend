package com.github.alfabravo2013.hyperidbackend.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public record HyperUserCredentials(
        @NotEmpty(message = "Field 'username' cannot be empty")
        @NotBlank(message = "Field 'username' cannot be blank")
        String username,
        @NotEmpty(message = "Field 'password' cannot be empty")
        @NotBlank(message = "Field 'password' cannot be blank")
        String password
) { }
