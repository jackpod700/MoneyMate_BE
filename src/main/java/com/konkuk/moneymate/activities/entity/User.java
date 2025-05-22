package com.konkuk.moneymate.activities.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "user_uid", columnDefinition = "BINARY(16)")
    private UUID uid;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Set<BankAccount> bankAccounts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Set<MonthlyAssetHistory> monthlyAssetHistorys;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Set<Asset> assets;


    @Column(name = "id", unique = true, nullable = false)
    private String userId;

    @Column(name = "pw", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String userName;

    @Column(name = "phone_number", length = 13)
    private String phoneNumber;

    @Column(name = "birthday")
    private LocalDate birthday;

    public User(String userId, String userName, String password) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
    }





}


/*
    @Column(name = "connected_id", unique = true, nullable = false)
    private String connectedId;

    @Column(name = "registered")
    private LocalDate registered;
 */