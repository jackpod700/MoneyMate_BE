package com.konkuk.moneymate.activities.service;

import com.konkuk.moneymate.activities.dto.AssetDto;
import com.konkuk.moneymate.activities.dto.BankAccountDto;
import com.konkuk.moneymate.activities.entity.Asset;
import com.konkuk.moneymate.activities.entity.User;
import com.konkuk.moneymate.activities.repository.AssetRepository;
import com.konkuk.moneymate.activities.repository.UserRepository;
import com.konkuk.moneymate.activities.validator.AssetValidator;
import com.konkuk.moneymate.common.ApiResponseMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class AssetService {

    private final BankAccountService bankAccountService;
    private final AssetValidator assetValidator;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    public AssetService( BankAccountService bankAccountService,
                        AssetValidator assetValidator,
                        AssetRepository assetRepository,
                        UserRepository userRepository) {
        this.bankAccountService = bankAccountService;
        this.assetValidator = new AssetValidator();
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
    }

    public void registerAsset(AssetDto assetDto, String userUid) {

        assetValidator.checkAsset(assetDto);

        User user = userRepository.findByUid(UUID.fromString(userUid))
                .orElseThrow(() -> new EntityNotFoundException(ApiResponseMessage.USER_NOT_FOUND.getMessage()));
        Asset asset = assetDto.toEntity(user);

        // Save the asset entity to the repository
        assetRepository.save(asset);
    }

    public List<AssetDto> getAsset(String userUid){
        List<AssetDto> assetList = new ArrayList<>();
        assetRepository.findByUser_Uid(UUID.fromString(userUid))
                .forEach(asset -> {
                    AssetDto assetDto = asset.toDto();
                    assetList.add(assetDto);
                });
        return assetList;
    }

    public void deleteAsset(String assetUid, String userUid) throws IllegalAccessException {
        UUID assetUUID = UUID.fromString(assetUid);
        User user = userRepository.findByUid(UUID.fromString(userUid))
                .orElseThrow(() -> new EntityNotFoundException(ApiResponseMessage.USER_NOT_FOUND.getMessage()));

        Asset asset = assetRepository.findById(assetUUID)
                .orElseThrow(() -> new EntityNotFoundException(ApiResponseMessage.ASSET_NOT_FOUND.getMessage()));

        if (!asset.getUser().getUid().equals(user.getUid())) {
            throw new IllegalAccessException(ApiResponseMessage.NO_ACCESS_AUTHORITY.getMessage());
        }

        assetRepository.delete(asset);
    }

    public Long getTotalPrice(String userUid) {
        List<AssetDto> assets = getAsset(userUid);
        List<BankAccountDto> bankAccounts = bankAccountService.getAccountList(userUid);

        Long totalPrice = 0L;
        for (AssetDto assetDto : assets) {
            totalPrice += assetDto.getAssetPrice();
        }
        for (BankAccountDto bankAccountDto : bankAccounts) {
            totalPrice += bankAccountDto.getAccountBalance();
        }
        return totalPrice;
    }
}
