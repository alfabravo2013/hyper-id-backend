package com.github.alfabravo2013.hyperidbackend.api;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HyperUserRepo extends JpaRepository<HyperUser, String> {
    Optional<HyperUser> findByAccessToken(String token);
}
