package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUserName(String username);
    Optional<User> findByUserId(String userId);
    Optional<User> findByUid(UUID uid);

    boolean existsByUserId(String userId);
}
