package com.github.alfabravo2013.hyperidbackend.api;

import com.github.alfabravo2013.hyperidbackend.exceptions.AccessDeniedException;
import com.github.alfabravo2013.hyperidbackend.exceptions.FailedAuthException;
import com.github.alfabravo2013.hyperidbackend.exceptions.UsernameTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;

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
        assertThat(response.getBody().error().message()).isEqualTo(UsernameTakenException.MESSAGE);
    }

    @Test
    void whenRegisterWithEmptyUsernameShouldReturn400() {
        var credentials = new HyperUserCredentials("", "password");

        var response = template.postForEntity("/register", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'username' is empty");
    }

    @Test
    void whenRegisterWithBlankUsernameShouldReturn400() {
        var credentials = new HyperUserCredentials("     ", "password");

        var response = template.postForEntity("/register", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'username' is empty");
    }

    @Test
    void whenRegisterWithMissingUsernameShouldReturn400() {
        var credentials = Map.of("password", "12345");

        var response = template.postForEntity("/register", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'username' is empty");
    }

    @Test
    void whenRegisterWithEmptyPasswordShouldReturn400() {
        var credentials = new HyperUserCredentials("username", "");

        var response = template.postForEntity("/register", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'password' is empty");
    }

    @Test
    void whenRegisterWithBlankPasswordShouldReturn400() {
        var credentials = new HyperUserCredentials("username", "     ");

        var response = template.postForEntity("/register", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'password' is empty");
    }

    @Test
    void whenRegisterWithMissingPasswordShouldReturn400() {
        var credentials = Map.of("username", "alfa");

        var response = template.postForEntity("/register", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'password' is empty");
    }

    @Test
    void whenRegisterWithExtraFieldShouldReturn400() {
        var credentials = Map.of(
                "username", "alfa", "password", "1234", "extra_field", "some");

        var response = template.postForEntity("/register", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("Unknown field: extra_field");
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
    void whenLoginShouldReturnNonEmptyHeaders() {
        var credentials = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", credentials, Void.class);
        var response = template.postForEntity("/login", credentials, HyperUserDto.class);

        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getHeaders().getFirst("accessToken")).isNotEmpty();
        assertThat(response.getHeaders().getFirst("sessionId")).isNotEmpty();
    }

    @Test
    void whenLoginWithEmptyUsernameShouldReturn400() {
        var credentials = new HyperUserCredentials("", "password");

        var response = template.postForEntity("/login", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'username' is empty");
    }

    @Test
    void whenLoginWithBlankUsernameShouldReturn400() {
        var credentials = new HyperUserCredentials("     ", "password");

        var response = template.postForEntity("/login", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'username' is empty");
    }

    @Test
    void whenLoginWithMissingUsernameShouldReturn400() {
        var credentials = Map.of("password", "1234");

        var response = template.postForEntity("/login", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'username' is empty");
    }

    @Test
    void whenLoginWithEmptyPasswordShouldReturn400() {
        var credentials = new HyperUserCredentials("user", "");

        var response = template.postForEntity("/login", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'password' is empty");
    }

    @Test
    void whenLoginWithBlankPasswordShouldReturn400() {
        var credentials = new HyperUserCredentials("user", "     ");

        var response = template.postForEntity("/login", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'password' is empty");
    }

    @Test
    void whenLoginWithMissingPasswordShouldReturn400() {
        var credentials = Map.of("username", "user");

        var response = template.postForEntity("/login", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'password' is empty");
    }

    @Test
    void whenLoginWithExtraFieldShouldReturn400() {
        var credentials = Map.of("username", "user", "extra", "value");

        var response = template.postForEntity("/login", credentials, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("Unknown field: extra");
    }

    @Test
    void whenLoginWithBadUsernameShouldReturn401() {
        var registration = new HyperUserCredentials("user", "password");
        var login = new HyperUserCredentials("admin", "password");
        template.postForEntity("/register", registration, Void.class);
        var response = template.postForEntity("/login", login, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).isEqualTo(FailedAuthException.MESSAGE);
    }

    @Test
    void whenLoginWithBadPasswordShouldReturn401() {
        var registration = new HyperUserCredentials("user", "password");
        var login = new HyperUserCredentials("user", "1234");
        template.postForEntity("/register", registration, Void.class);
        var response = template.postForEntity("/login", login, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).isEqualTo(FailedAuthException.MESSAGE);
    }

    @Test
    void whenLoginRepeatedlyShouldReturnUniqueAccessTokens() {
        var credentials = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", credentials, Void.class);

        var token1 = template.postForEntity("/login", credentials, HyperUserDto.class)
                .getHeaders()
                .getFirst("accessToken");
        var token2 = template.postForEntity("/login", credentials, HyperUserDto.class)
                .getHeaders()
                .getFirst("accessToken");

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void whenLoginShouldProduceCookies() {
        var credentials = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", credentials, Void.class);

        var cookie = template.postForEntity("/login", credentials, HyperUserDto.class)
                .getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        assertThat(cookie).isNotNull().isNotEmpty();
    }

    @Test
    void whenGetAccountWithTokenShouldReturnAccount() {
        var credentials = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", credentials, Void.class);
        var response = template.postForEntity("/login", credentials, HyperUserDto.class);
        var token = response
                .getHeaders()
                .getFirst("accessToken");

        var headers = new HttpHeaders();
        headers.add("Authorization", token);
        var request = new HttpEntity<Void>(headers);
        var account = template.exchange("/account", HttpMethod.GET, request, HyperUserDto.class);

        assertThat(account.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(account.getBody()).isNotNull();
        assertThat(account.getBody().username()).isEqualTo(credentials.username());
    }

    @Test
    void whenGetAccountWithBadTokenShouldReturn403() {
        var registration = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", registration, Void.class);
        var headers = new HttpHeaders();
        headers.add("Authorization", "token");
        var request = new HttpEntity<Void>(headers);
        var response = template.exchange("/account", HttpMethod.GET, request, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).isEqualTo(AccessDeniedException.MESSAGE);
    }

    @Test
    void whenGetAccountWithMissingTokenShouldReturn400() {
        var registration = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", registration, Void.class);
        var headers = new HttpHeaders();
        headers.add("NoAuth", "token");
        var request = new HttpEntity<Void>(headers);
        var response = template.exchange("/account", HttpMethod.GET, request, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'Authorization' is empty");
    }

    @Test
    void whenUpdateWithMissingTokenShouldReturn400() {
        var registration = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", registration, Void.class);

        var update = new HyperUserUpdateDto("alfa", "bravo");
        var headers = new HttpHeaders();
        headers.add("NoAuth", "token");
        var request = new HttpEntity<>(update, headers);
        var response = template.exchange("/account", HttpMethod.PUT, request, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'Authorization' is empty");
    }

    @Test
    void whenUpdateWithBadTokenShouldReturn403() {
        var registration = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", registration, Void.class);

        var update = new HyperUserUpdateDto("alfa", "bravo");
        var headers = new HttpHeaders();
        headers.add("Authorization", "token");
        var request = new HttpEntity<>(update, headers);
        var response = template.exchange("/account", HttpMethod.PUT, request, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains(AccessDeniedException.MESSAGE);
    }

    @Test
    void whenUpdateWithEmptyNameShouldReturn400() {
        var registration = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", registration, Void.class);

        var token = template.postForEntity("/login", registration, Void.class)
                .getHeaders()
                .getFirst("accessToken");
        var update = new HyperUserUpdateDto("", "bravo");
        var headers = new HttpHeaders();
        headers.add("Authorization", token);
        var request = new HttpEntity<>(update, headers);
        var response = template.exchange("/account", HttpMethod.PUT, request, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'name' is empty");
    }

    @Test
    void whenUpdateWithMissingNameShouldReturn400() {
        var registration = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", registration, Void.class);

        var token = template.postForEntity("/login", registration, Void.class)
                .getHeaders()
                .getFirst("accessToken");
        var update = Map.of("surname", "bravo");
        var headers = new HttpHeaders();
        headers.add("Authorization", token);
        var request = new HttpEntity<>(update, headers);
        var response = template.exchange("/account", HttpMethod.PUT, request, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'name' is empty");
    }

    @Test
    void whenUpdateWithEmptySurnameShouldReturn200() {
        var registration = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", registration, Void.class);

        var token = template.postForEntity("/login", registration, Void.class)
                .getHeaders()
                .getFirst("accessToken");
        var update = new HyperUserUpdateDto("alfa", "");
        var headers = new HttpHeaders();
        headers.add("Authorization", token);
        var request = new HttpEntity<>(update, headers);
        var response = template.exchange("/account", HttpMethod.PUT, request, HyperUserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo(update.name());
        assertThat(response.getBody().surname()).isEqualTo(update.surname());
    }

    @Test
    void whenUpdateWithMissingSurnameShouldReturn400() {
        var registration = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", registration, Void.class);

        var token = template.postForEntity("/login", registration, Void.class)
                .getHeaders()
                .getFirst("accessToken");
        var update = Map.of("name", "alfa");
        var headers = new HttpHeaders();
        headers.add("Authorization", token);
        var request = new HttpEntity<>(update, headers);
        var response = template.exchange("/account", HttpMethod.PUT, request, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("'surname' is missing");
    }

    @Test
    void whenUpdateWithExtraFieldShouldReturn400() {
        var registration = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", registration, Void.class);

        var token = template.postForEntity("/login", registration, Void.class)
                .getHeaders()
                .getFirst("accessToken");
        var update = Map.of("name", "alfa", "surname", "bravo", "extra", "value");
        var headers = new HttpHeaders();
        headers.add("Authorization", token);
        var request = new HttpEntity<>(update, headers);
        var response = template.exchange("/account", HttpMethod.PUT, request, ErrorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error().message()).contains("Unknown field: extra");
    }

    @Test
    void whenUpdateShouldReturnUpdatedUserDetails() {
        var registration = new HyperUserCredentials("user", "password");
        template.postForEntity("/register", registration, Void.class);

        var token = template.postForEntity("/login", registration, Void.class)
                .getHeaders()
                .getFirst("accessToken");
        var update = new HyperUserUpdateDto("alfa", "bravo");
        var headers = new HttpHeaders();
        headers.add("Authorization", token);
        var request = new HttpEntity<>(update, headers);
        var response = template.exchange("/account", HttpMethod.PUT, request, HyperUserDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo(update.name());
        assertThat(response.getBody().surname()).isEqualTo(update.surname());
    }
}
