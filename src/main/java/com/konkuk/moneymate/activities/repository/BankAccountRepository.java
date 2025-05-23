package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
}
