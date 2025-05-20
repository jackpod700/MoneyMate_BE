package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);
    Optional<User> findByUserId(String userId);
}
