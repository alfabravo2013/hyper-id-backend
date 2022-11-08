package com.github.alfabravo2013.hyperidbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.github.alfabravo2013.hyperidbackend.api.ErrorDto.REGISTER_ERR;

@RestController
public class HyperUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HyperUserController.class);

    private final HyperUserService userService;

    public HyperUserController(HyperUserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody HyperUserCredentials credentials) {
        LOGGER.debug("Registering: {}", credentials);

        return userService
                .register(credentials)
                .map(dto -> new ResponseEntity<>(HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ErrorDto.of(REGISTER_ERR), HttpStatus.CONFLICT));
    }

    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody HyperUserCredentials credentials) {
        LOGGER.debug("Logging in: {}", credentials);

        // todo refactor to exception handling
        return userService
                .login(credentials)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<?> logout() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccount(HttpRequest request) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(
            path = "/account",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAccount(HttpRequest request, @RequestBody HyperUserDto userDto) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(path = "/account")
    public ResponseEntity<Void> deleteAll() {
        userService.deleteAllUsers();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
