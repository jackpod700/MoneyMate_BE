package com.konkuk.moneymate.activities.assets.service;

import com.konkuk.moneymate.activities.assets.dto.AssetDto;
import com.konkuk.moneymate.activities.bankaccount.dto.BankAccountDto;
import com.konkuk.moneymate.activities.bankaccount.service.BankAccountService;
import com.konkuk.moneymate.activities.stock.dto.StockHoldingDto;
import com.konkuk.moneymate.activities.assets.entity.Asset;
import com.konkuk.moneymate.activities.user.entity.User;
import com.konkuk.moneymate.activities.stock.repository.AccountStockRepository;
import com.konkuk.moneymate.activities.assets.repository.AssetRepository;
import com.konkuk.moneymate.activities.user.repository.UserRepository;
import com.konkuk.moneymate.common.ApiResponseMessage;
import com.konkuk.moneymate.activities.stock.StockPriceApiClient;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class AssetService {

    private final BankAccountService bankAccountService;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final AccountStockRepository accountStockRepository;

    public AssetService( BankAccountService bankAccountService,
                        AssetRepository assetRepository,
                        UserRepository userRepository,
                         AccountStockRepository accountStockRepository) {
        this.bankAccountService = bankAccountService;
        this.assetRepository = assetRepository;
        this.userRepository = userRepository;
        this.accountStockRepository = accountStockRepository;
    }

    public void registerAsset(AssetDto assetDto, String userUid) {

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

    public List<StockHoldingDto> getStockHoldingsWithPrice(String userUid) {
        List<StockHoldingDto> holdings = accountStockRepository.findAllStockHoldings(UUID.fromString(userUid));

        Map<String, BigDecimal> prices;
        HashMap<String, BigDecimal> exchangeRates = new HashMap<>();
        exchangeRates.put("USD", StockPriceApiClient.getKRWUSD_exchangeRate());

        try{
            prices = StockPriceApiClient.getCurrentPrices(holdings);

        } catch (Exception e) {
            throw new RuntimeException("실시간 주식 조회 중 오류 발생: " + e.getMessage(), e);
        }

        for (StockHoldingDto dto : holdings) {
            BigDecimal currentPrice;
            BigDecimal buyPrice;
            if(dto.getCurrency().equals("KRW")){
                currentPrice = prices.get(dto.getTicker() + "." + dto.getExchangeId());
                buyPrice = dto.getAveragePrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
            }
            else {
                currentPrice = prices.get(dto.getTicker() + "." + dto.getExchangeId())
                        .multiply(exchangeRates.get(dto.getCurrency()));
                buyPrice = dto.getAveragePrice()
                        .multiply(exchangeRates.get(dto.getCurrency()))
                        .multiply(BigDecimal.valueOf(dto.getQuantity()));
            }
            BigDecimal currentTotal = currentPrice.multiply(BigDecimal.valueOf(dto.getQuantity()));
            BigDecimal profit = currentTotal.subtract(buyPrice)
                    .divide(buyPrice, 2, RoundingMode.HALF_UP);

            dto.setCurrentTotalPrice(currentTotal);
            dto.setProfit(profit);
        }

        return holdings;
    }
}
