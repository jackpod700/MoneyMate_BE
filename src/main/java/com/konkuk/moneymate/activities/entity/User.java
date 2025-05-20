package com.konkuk.moneymate.activities.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "uid", columnDefinition = "BINARY(16)")
    private UUID uid;

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