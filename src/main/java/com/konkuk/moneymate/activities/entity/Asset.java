package com.konkuk.moneymate.activities.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Data
@Entity
public class Asset {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "asset_uid", columnDefinition = "BINARY(16)", nullable = false)
    private UUID uid;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="uid")
    private User user;

    @Column(name="price", nullable = false)
    private int price;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;





}
