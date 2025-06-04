package com.konkuk.moneymate.activities.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * <h3>User Entity 클래스 </h3>
 * <strong>PK : uid (UUID)</strong> <br>
 * <strong>Constructor: User(String userId, String userName, String password)</strong>
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "user")
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

    @Column(name = "phone_number", length = 13, nullable = false)
    private String phoneNumber;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;



    public User(String userId, String userName, String password, String phoneNumber, LocalDate birthday) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
    }

    public void encodeBCryptPassword() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(this.password);
    }


}


/*
    @Column(name = "connected_id", unique = true, nullable = false)
    private String connectedId;

    @Column(name = "registered")
    private LocalDate registered;
 */