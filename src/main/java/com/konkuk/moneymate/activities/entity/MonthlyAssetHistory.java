package com.konkuk.moneymate.activities.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

/**
 * <h3>monthly_total_asset_history의 Entity 클래스</h3>
 * <b>PK : asset_history UUID</b><br>
 * <b>FK : user_uid ref from user.uid</b>
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "monthly_total_asset_history")
@Entity
public class MonthlyAssetHistory {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "asset_history", columnDefinition = "BINARY(16)")
    private UUID uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_uid")
    private User user;


    // 월 별로 저장한다면 YearMonth ?
    @Column(name = "month", nullable = false)
    private LocalDate month;  // "2025-05-24"

    @Column(name="price", nullable = false)
    private int price;

    @Column(name="monthly_income", nullable = false)
    private int mIncome;

    @Column(name="monthly_outcome", nullable = false)
    private int mOutcome;

    // getter getMonthYear()에서 yyyy-mm에 사용할 yyyy-mm 반환
    public YearMonth getMonthYear() {
        return YearMonth.from(month);
    }

    public MonthlyAssetHistory(User user, LocalDate month, int price, int mIncome, int mOutcome) {
        this.user = user;
        this.month = month;
        this.price = price;
        this.mIncome = mIncome;
        this.mOutcome = mOutcome;
    }
}
