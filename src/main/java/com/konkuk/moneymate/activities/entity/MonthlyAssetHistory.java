package com.konkuk.moneymate.activities.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table(name = "monthly_total_asset_history")
@Entity
public class MonthlyAssetHistory {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uid", columnDefinition = "BINARY(16)")
    private UUID uid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_uid")
    private User user;


    // 월 별로 저장한다면 YearMonth ?
    @Column(name = "month")
    private LocalDateTime month;

    @Column(name="price", nullable = false)
    private int price;

    @Column(name="monthly_income", nullable = false)
    private int mIncome;

    @Column(name="monthly_outcome", nullable = false)
    private int mOutcome;
}
