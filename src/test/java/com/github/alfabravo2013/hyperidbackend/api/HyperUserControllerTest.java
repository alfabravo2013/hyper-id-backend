package com.github.alfabravo2013.hyperidbackend.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class HyperUserControllerTest {

    @Autowired
    private TestRestTemplate template;

    @BeforeEach
    void cleanUp() {
        template.delete("/account");
    }

    @Test
    void whenRegisterShouldReturnOk() {
        var credentials = new HyperUserCredentials("user", "password");

        var response = template.postForEntity("/register", credentials, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void whenRegisterTakenUsernameShouldReturnConflict() {
        var credentials = new HyperUserCredentials("user", "password");

        var response = template.postForEntity("/register", credentials, Void.class);
        response = template.postForEntity("/register", credentials, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
