package com.konkuk.moneymate.activities.repository;

import com.konkuk.moneymate.activities.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {
}
