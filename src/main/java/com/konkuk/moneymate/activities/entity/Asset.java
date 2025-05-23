package com.konkuk.moneymate.activities.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

/**
 * <h3>asset Entity 클래스</h3>
 * <b>PK : asset_uid UUID <br></b>
 * <b>FK : user_uid ref from user.uid</b>
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="asset")
public class Asset {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "asset_uid", columnDefinition = "BINARY(16)", nullable = false)
    private UUID uid;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_uid", nullable = false)
    private User user;

    @Column(name="price", nullable = false)
    private Long price;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    public Asset(User user, Long price, String name, String type) {
        this.user = user;
        this.price = price;
        this.name = name;
        this.type = type;
    }





}
