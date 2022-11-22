package com.github.alfabravo2013.hyperidbackend.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record HyperUserUpdateDto(
        @NotBlank(message = "Field 'name' is empty")
        String name,
        @NotNull(message = "Field 'surname' is missing")
        String surname
) {
}
