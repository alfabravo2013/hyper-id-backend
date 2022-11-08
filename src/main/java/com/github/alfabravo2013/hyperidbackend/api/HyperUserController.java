package com.github.alfabravo2013.hyperidbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpSession;
import java.util.UUID;

@RestController
public class HyperUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HyperUserController.class);

    private final HyperUserService userService;

    public HyperUserController(HyperUserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(@RequestBody HyperUserCredentials credentials) {
        LOGGER.debug("Registering: {}", credentials);

        userService.register(credentials);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody HyperUserCredentials credentials) {
        LOGGER.debug("Logging in: {}", credentials);

        var body = userService.login(credentials);

        var headers = new HttpHeaders();
        var sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        var token = UUID.randomUUID().toString();
        headers.add("sessionId", sessionId);
        headers.add("accessToken", token);

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
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
