package com.konkuk.moneymate.activities.dto;

import com.konkuk.moneymate.activities.entity.Asset;
import com.konkuk.moneymate.activities.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class AssetDto {
    private UUID assetUid;
    private String assetName;
    private String assetType;
    private Long assetPrice;

    public AssetDto(String assetName, String assetType, Long assetPrice) {
        this.assetName = assetName;
        this.assetType = assetType;
        this.assetPrice = assetPrice;
    }

    public Asset toEntity(User user) {
        return new Asset(
                user,
                this.assetPrice,
                this.assetName,
                this.assetType
        );
    }
}
