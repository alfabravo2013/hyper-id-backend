package com.github.alfabravo2013.hyperidbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HyperUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HyperUserController.class);

    private final HyperUserRepo userRepo;

    public HyperUserService(HyperUserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    public void register(HyperUserCredentials credentials) {
        if (userRepo.existsById(credentials.username())) {
            LOGGER.debug("Found existing {}", credentials.username());
            throw new UsernameTakenException();
        }

        var newUser = new HyperUser();
        newUser.setUsername(credentials.username());
        newUser.setPassword(credentials.password());
        userRepo.save(newUser);
    }

    public HyperUserDto login(HyperUserCredentials credentials) {
        var user = userRepo.findById(credentials.username()).orElseThrow(FailedAuthException::new);

        if (!user.getPassword().equals(credentials.password())) {
            throw new FailedAuthException();
        }

        return HyperUserDto.of(user);
    }

    @Transactional
    public void deleteAllUsers() {
        userRepo.deleteAll();
    }
}
