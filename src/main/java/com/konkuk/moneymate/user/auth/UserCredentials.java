package com.konkuk.moneymate.user.auth;

import java.util.UUID;

public record UserCredentials(String userid, String password, UUID uid) {
}
