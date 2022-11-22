package com.github.alfabravo2013.hyperidbackend.api;

import com.github.alfabravo2013.hyperidbackend.exceptions.AccessDeniedException;
import com.github.alfabravo2013.hyperidbackend.exceptions.FailedAuthException;
import com.github.alfabravo2013.hyperidbackend.exceptions.UsernameTakenException;
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
        var user = userRepo.findByAccessToken(token).orElseThrow(AccessDeniedException::new);
        return HyperUserDto.of(user);
    }

    public HyperUserDto updateAccount(String token, HyperUserUpdateDto updated) {

        var user = userRepo.findByAccessToken(token).orElseThrow(AccessDeniedException::new);

        user.setName(updated.name());
        user.setSurname(updated.surname());
        userRepo.save(user);

        return HyperUserDto.of(user);
    }

    public void logout(String token) {
        var user = userRepo.findByAccessToken(token).orElseThrow(AccessDeniedException::new);
        user.setAccessToken(null);
        userRepo.save(user);
    }
}
