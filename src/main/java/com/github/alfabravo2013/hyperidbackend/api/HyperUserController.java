package com.github.alfabravo2013.hyperidbackend.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import javax.validation.Valid;
import java.util.Map;

@CrossOrigin(originPatterns = "*")
@RestController
public class HyperUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HyperUserController.class);

    private final HyperUserService userService;

    public HyperUserController(HyperUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create user", description = "Register a new user with username and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "Incorrect JSON format"),
            @ApiResponse(responseCode = "409", description = "username already taken")})
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(
            @Parameter(
                    description = "username and password container",
                    content = @Content(schema = @Schema(implementation = HyperUserCredentials.class)))
            @RequestBody @Valid HyperUserCredentials credentials) {
        LOGGER.debug("Registering: {}", credentials);

        userService.register(credentials);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Authenticate user", description = "Submit user credentials to get an access token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    headers = {
                            @Header(name = "accessToken", description = "Access token"),
                            @Header(name = "sessionId", description = "Session ID") },
                    content = @Content(schema = @Schema(implementation = HyperUserDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Incorrect JSON format"),
            @ApiResponse(responseCode = "401", description = "Bad credentials") })
    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(
            @Parameter(
                    description = "username and password container",
                    schema = @Schema(implementation = HyperUserCredentials.class))
            @RequestBody @Valid HyperUserCredentials credentials) {
        LOGGER.debug("Logging in: {}", credentials);

        var map = userService.login(credentials);
        var body = (HyperUserDto) map.get("body");
        var token = (String) map.get("token");
        var sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();

        var headers = new HttpHeaders();
        headers.add("sessionId", sessionId);
        headers.add("accessToken", token);

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    @Operation(summary = "Log out", description = "invalidates provided access token")
    @ApiResponse(responseCode = "200", description = "successful operation")
    @PostMapping(path = "/logout")
    public ResponseEntity<?> logout(
            @Parameter(name = "Authorization", description = "access token")
            @RequestHeader("Authorization") String token) {
        userService.logout(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get user details", description = "retrieves user profile associated with provided access token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(schema = @Schema(implementation = HyperUserDto.class))),
            @ApiResponse(responseCode = "403", description = "no account associated with provided token"),
            @ApiResponse(responseCode = "400", description = "Header 'Authorization' is missing")
    })
    @GetMapping(path = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HyperUserDto> getAccount(
            @Parameter(name = "Authorization", description = "access token")
            @RequestHeader("Authorization") String token) {
        LOGGER.debug("Found Authorization: {}", token);

        var body = userService.getAccount(token);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Operation(summary = "Update user details", description = "changes user name, surname and password")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = @Content(schema = @Schema(implementation = HyperUserDto.class))),
            @ApiResponse(responseCode = "404", description = "no account associated with provided token")
    })
    @PutMapping(
            path = "/account",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAccount(
            @Parameter(name = "Authorization", description = "authorization token")
            @RequestHeader("Authorization") String token,
            @Parameter(
                    name = "new details",
                    description = "user details container",
                    content = @Content(schema = @Schema(implementation = HyperUserUpdateDto.class)))
            @RequestBody HyperUserUpdateDto userDto) {
        LOGGER.debug("Setting new user details: {}", userDto);

        var body = userService.updateAccount(token, userDto);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @Operation(summary = "Delete all", description = "clears all accounts from the database")
    @ApiResponse(responseCode = "200", description = "successful operation")
    @DeleteMapping(path = "/account")
    public ResponseEntity<Void> deleteAll() {
        userService.deleteAllUsers();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<Object> test() {
        return new ResponseEntity<>(Map.of("text", "Test"), HttpStatus.OK);
    }
}
