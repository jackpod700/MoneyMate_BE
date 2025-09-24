package com.konkuk.moneymate.activities.entity.stats;

import jakarta.persistence.*;
import lombok.*;

/**
 * <h3>IncomeStats (tb: stats_income)</h3>
 * <li><strong>소득 통계를 위한 table entity</strong></li>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Entity
@Table(name="stats_data_income")
public class StatsIncomeData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="c1_obj_nm", length=127)
    private String c1ObjName;

    @Column(name="c2_obj_nm", length=127)
    private String c2ObjName;

    @Column(name="c1_nm", length=127)
    private String c1Name;

    @Column(name="c2_nm", length=127)
    private String c2Name;

    @Column(name="c1", length=63)
    private String c1;

    @Column(name="c2", length=63)
    private String c2;

    /**
     * value (DT) : String
     */
    @Column(name="value", length=20)
    private String value;

    @Column(name="pre_de", length=63)
    private Integer year;

    @Column(name="item_nm", length=127)
    private String itemName;

    @Column(name="item_id", length=63)
    private String itemId;

    @Column(name="unit_nm", length=16)
    private String unitName;

}