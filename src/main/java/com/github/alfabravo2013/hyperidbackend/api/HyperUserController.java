package com.github.alfabravo2013.hyperidbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HyperUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HyperUserController.class);

    private final HyperUserRepo userRepo;

    public HyperUserController(HyperUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(@RequestBody HyperUserCredentials credentials) {
        LOGGER.debug("Registering: {}", credentials);

        if (userRepo.existsById(credentials.username())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        var newUser = new HyperUser();
        newUser.setUsername(credentials.username());
        newUser.setPassword(credentials.password());
        userRepo.save(newUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody HyperUserCredentials credentials) {
        LOGGER.debug("Logging in: {}", credentials);

        return userRepo
                .findById(credentials.username())
                .filter(user -> user.getPassword().matches(credentials.password()))
                .map(HyperUserDto::of)
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

}
