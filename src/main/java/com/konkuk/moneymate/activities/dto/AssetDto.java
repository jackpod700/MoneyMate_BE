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
    private Long assetPrice;

    public AssetDto(String assetName, Long assetPrice) {
        this.assetName = assetName;
        this.assetPrice = assetPrice;
    }

    public Asset toEntity(User user) {
        return new Asset(
                user,
                this.assetPrice,
                this.assetName
        );
    }
}
