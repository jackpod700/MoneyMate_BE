package com.konkuk.moneymate.activities.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class AccountStockId implements Serializable {

    private UUID bankAccount;
    private String stock;

    public AccountStockId() {}

    public AccountStockId(UUID bankAccount, String stock) {
        this.bankAccount = bankAccount;
        this.stock = stock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountStockId)) return false;
        AccountStockId that = (AccountStockId) o;
        return Objects.equals(bankAccount, that.bankAccount) &&
                Objects.equals(stock, that.stock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bankAccount, stock);
    }
}