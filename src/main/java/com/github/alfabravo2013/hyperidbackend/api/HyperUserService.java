package com.github.alfabravo2013.hyperidbackend.api;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class HyperUserService {
    private final HyperUserRepo userRepo;

    public HyperUserService(HyperUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    public Optional<HyperUserDto> register(HyperUserCredentials credentials) {
        if (userRepo.existsById(credentials.username())) {
            return Optional.empty();
        }

        var newUser = new HyperUser();
        newUser.setUsername(credentials.username());
        newUser.setPassword(credentials.password());
        userRepo.save(newUser);

        return Optional.of(HyperUserDto.of(newUser));
    }

    public Optional<HyperUserDto> login(HyperUserCredentials credentials) {
        return userRepo
                .findById(credentials.username())
                .map(HyperUserDto::of);
    }

    @Transactional
    public void deleteAllUsers() {
        userRepo.deleteAll();
    }
}
