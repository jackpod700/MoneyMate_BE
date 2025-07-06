package com.konkuk.moneymate.auth.auth;

import java.util.UUID;

public record UserCredentials(String userid, String password, UUID uid) {
}
