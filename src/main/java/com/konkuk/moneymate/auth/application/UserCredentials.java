package com.konkuk.moneymate.auth.application;

import java.util.UUID;

public record UserCredentials(String userid, String password, UUID uid) {
}
