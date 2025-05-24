package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, UUID> {
    List<Asset> findByUser_Uid(UUID uuid);
}
