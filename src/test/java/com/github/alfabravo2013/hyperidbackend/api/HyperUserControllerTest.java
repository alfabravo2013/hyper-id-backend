package com.github.alfabravo2013.hyperidbackend.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.alfabravo2013.hyperidbackend.api.ErrorDto.LOGIN_ERR;
import static com.github.alfabravo2013.hyperidbackend.api.ErrorDto.REGISTER_ERR;
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
    void whenRegisterShouldReturn200() {
        var credentials = new HyperUserCredentials("user", "password");

        var response = template.postForEntity("/register", credentials, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void whenRegisterTakenUsernameShouldReturn409() {
        var credentials = new HyperUserCredentials("user", "password");

        template.postForEntity("/register", credentials, Void.class);
        var response = template.postForEntity("/register", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).isEqualTo(REGISTER_ERR);
    }

    @Test
    void whenLoginShouldReturnDto() {
        var credentials = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", credentials, Void.class);
        var response = template.postForEntity("/login", credentials, HyperUserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo(credentials.username());
        assertThat(response.getBody().name()).isEmpty();
        assertThat(response.getBody().surname()).isEmpty();
    }

    @Test
    void whenLoginShouldReturnHeaders() {
        var credentials = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", credentials, Void.class);
        var response = template.postForEntity("/login", credentials, HyperUserDto.class);

        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getHeaders().containsKey("accessToken")).isTrue();
        assertThat(response.getHeaders().containsKey("sessionId")).isTrue();
    }

    @Test
    void whenLoginWithBadUsernameShouldReturn401() {
        var registration = new HyperUserCredentials("user", "password");
        var login = new HyperUserCredentials("admin", "password");
        template.postForEntity("/register", registration, Void.class);
        var response = template.postForEntity("/login", login, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).isEqualTo(LOGIN_ERR);
    }

    @Test
    void whenLoginWithBadPasswordShouldReturn401() {
        var registration = new HyperUserCredentials("user", "password");
        var login = new HyperUserCredentials("user", "1234");
        template.postForEntity("/register", registration, Void.class);
        var response = template.postForEntity("/login", login, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).isEqualTo(LOGIN_ERR);
    }
}
