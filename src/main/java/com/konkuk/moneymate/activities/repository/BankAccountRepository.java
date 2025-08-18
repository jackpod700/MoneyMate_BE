package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    BankAccount save(BankAccount bankAccount);
    List<BankAccount> findByUser_Uid(UUID uuid);

    /**
     * 사용자의 UUID(user_uid)와 예금 종류(deposit_type)를 기준으로 계좌 목록을 조회합니다.
     * @param userUid 사용자의 UUID
     * @param depositType 예금 종류 (예: "입출금", "예적금", "증권")
     * @return 조건에 맞는 BankAccount 리스트
     */
    List<BankAccount> findByUser_UidAndDepositType(UUID userUid, String depositType);
}
