package com.github.alfabravo2013.hyperidbackend.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HyperUserDtoTest {

    @Test
    void shouldHaveDetailsAsInEntity() {
        var user = new HyperUser();
        user.setUsername("username");
        user.setPassword("password");
        user.setName("first name");
        user.setSurname("last name");

        var dto = HyperUserDto.of(user);

        assertThat(dto.username()).isEqualTo(user.getUsername());
        assertThat(dto.name()).isEqualTo(user.getName());
        assertThat(dto.surname()).isEqualTo(user.getSurname());
    }

    @Test
    void shouldNotHavePassword() {
        var user = new HyperUser();
        var dto = HyperUserDto.of(user);

        assertThat(dto).hasOnlyFields("username", "name", "surname");
    }
}
