package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    BankAccount save(BankAccount bankAccount);
    List<BankAccount> findByUser_Uid(UUID uuid);
}
