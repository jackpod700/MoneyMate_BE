package com.konkuk.moneymate.activities.validator;

import com.konkuk.moneymate.activities.dto.AssetDto;
import com.konkuk.moneymate.activities.enums.AssetType;
import org.springframework.stereotype.Component;

@Component
public class AssetValidator {
    public void checkAsset(AssetDto assetDto) {
        checkAssetType(assetDto.getAssetType());
    }

    private void checkAssetType(String assetType) {
        try{
            AssetType.valueOf(assetType);
        }catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("잘못된 자산 타입입니다: " + assetType);
        }
    }
}
