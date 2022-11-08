package com.github.alfabravo2013.hyperidbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class HyperUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HyperUserController.class);

    private final HyperUserRepo userRepo;

    public HyperUserService(HyperUserRepo userRepo) {
        this.userRepo = userRepo;
    }

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

    public Map<String, Object> login(HyperUserCredentials credentials) {
        var user = userRepo.findById(credentials.username()).orElseThrow(FailedAuthException::new);

        if (!user.getPassword().equals(credentials.password())) {
            throw new FailedAuthException();
        }

        var newToken = UUID.randomUUID().toString();
        user.setAccessToken(newToken);
        userRepo.save(user);

        return Map.of("body", HyperUserDto.of(user), "token", user.getAccessToken());
    }

    public void deleteAllUsers() {
        userRepo.deleteAll();
    }

    public HyperUserDto getAccount(String token) {
        var user = userRepo.findByAccessToken(token).orElseThrow(NotFoundException::new);
        return HyperUserDto.of(user);
    }

    public HyperUserDto updateAccount(String token, HyperUserDto updated) {
        // todo think about password changing and rethink how access is granted
        //  now it's prohibited to change username while we need to check if principal owns the account to be updated
        //  JWT token as a solution?

        var user = userRepo.findByAccessToken(token).orElseThrow(NotFoundException::new);
        if (!user.getUsername().equals(updated.username())) {
            throw new AccessDeniedException();
        }

        user.setUsername(updated.username());
        user.setName(updated.name());
        user.setSurname(updated.surname());
        userRepo.save(user);

        return HyperUserDto.of(user);
    }

    public void logout(String token) {
        var user = userRepo.findByAccessToken(token).orElseThrow(NotFoundException::new);
        user.setAccessToken(null);
    }
}
