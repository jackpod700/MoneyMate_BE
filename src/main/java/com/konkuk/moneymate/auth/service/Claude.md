# Service Layer (`com.konkuk.moneymate.auth.service`)

## Overview

The `service` package contains all business logic for authentication, token management, user account operations, and SMS verification. Services are called by controllers and interact with repositories, external APIs (SMS), and Redis for caching.

## Package Structure

```
service/
├── JwtService.java                    # JWT token generation and parsing
├── JwtBlackListService.java          # Token blacklist management (Redis)
├── LoginService.java                  # Login business logic
├── LogoutService.java                 # Logout and token invalidation
├── RegisterService.java               # User registration logic
├── ReissueTokenManageService.java    # Token refresh logic
├── MessageAuthService.java            # SMS verification service
├── UserService.java                   # User account management
└── UserDetailsServiceImpl.java        # Spring Security UserDetailsService
```

## Core Services

### 1. JwtService

**Purpose**: Core JWT token generation, parsing, and validation service

**Key Constants**:
```java
ACCESS_TOKEN_EXPIRE_TIME = 5 * 60 * 1000L;      // 5 minutes
REFRESH_TOKEN_EXPIRE_TIME = 1 * 24 * 60 * 60 * 1000L;  // 1 day
AUTHORIZATION_HEADER = "Authorization";
BEARER_TYPE = "Bearer";
Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);  // Auto-generated
```

#### Main Methods:

##### `getAccessToken(String userId)`
**Purpose**: Generate access token for authenticated user

**Process**:
1. Lookup user in database to get UUID
2. Create JWT with userId (subject) and UUID (claim)
3. Set 5-minute expiration
4. Sign with HS256 secret key
5. Return compact JWT string

**Token Structure**:
```json
{
  "sub": "user123",
  "uid": "uuid-string",
  "exp": 1234567890
}
```

##### `getRefreshToken(String userId)`
**Purpose**: Generate refresh token

**Process**:
1. Verify user exists in database
2. Create JWT with userId (subject) only
3. Set 1-day expiration
4. Sign with HS256 secret key
5. Return compact JWT string

**Token Structure**:
```json
{
  "sub": "user123",
  "exp": 1234567890
}
```

##### `getAuthUser(HttpServletRequest request)`
**Purpose**: Extract authenticated user ID from JWT in request

**Process**:
1. Get Authorization header
2. Remove "Bearer " prefix
3. Parse and validate JWT
4. Extract subject (userId)
5. Return userId string

**Usage**: Used in all authenticated endpoints to identify current user

##### `getUserIdFromRefreshToken(String token)`
**Purpose**: Extract user ID from refresh token

**Returns**: String userId  
**Throws**: 
- `RuntimeException` - Token expired
- `RuntimeException` - Invalid token

##### `getUserUid(HttpServletRequest request)`
**Purpose**: Extract user UUID from access token

**Returns**: String UUID

##### `payloadPrint(HttpServletRequest request)`
**Purpose**: Debug method to view token payload

**Returns**: Map with:
- `sub` - User ID
- `uid` - User UUID
- `exp` - Expiration timestamp

##### `getExpiration(String token)`
**Purpose**: Get token expiration date

**Returns**: Date object  
**Handles**: Both valid and expired tokens

---

### 2. JwtBlackListService

**Purpose**: Manage token blacklist in Redis for logout and token refresh

**Storage**: Redis key-value store with TTL

#### Redis Key Patterns:
```
blacklist:access:<token>   → "true" (TTL: access token expiration)
blacklist:refresh:<token>  → "true" (TTL: refresh token expiration)
```

#### Main Methods:

##### `blacklistAccessToken(String accessToken)`
**Overloaded Methods**:
1. `blacklistAccessToken(token, expirationMillis)` - With explicit TTL
2. `blacklistAccessToken(token)` - Auto-calculate TTL from token

**Process**:
1. Remove "Bearer " prefix if present
2. Calculate TTL from token expiration
3. Store in Redis with TTL
4. Log blacklist registration

**Auto-cleanup**: Redis automatically removes expired entries

##### `blacklistRefreshToken(String refreshToken)`
**Similar to access token blacklisting**

##### `isAccessTokenBlacklisted(String accessToken)`
**Purpose**: Check if token is blacklisted

**Returns**: boolean  
**Process**: Check if key exists in Redis

##### `validateAccessTokenNotBlacklisted(String accessToken)`
**Purpose**: Throw exception if token is blacklisted

**Throws**: `InvalidTokenException`  
**Usage**: Called in `AuthenticationFilter`

##### `validateRefreshTokenNotBlacklisted(String refreshToken)`
**Similar validation for refresh tokens**

**Why Blacklist?**:
- Immediate logout effect (no need to wait for expiration)
- Prevent reuse of old tokens after refresh
- Support for token revocation
- Security: block compromised tokens

---

### 3. LoginService

**Purpose**: Handle user login authentication

**Dependencies**:
- `AuthenticationManager` - Spring Security authentication
- `JwtService` - Generate tokens

#### Main Method: `login(UserCredentials credentials)`

**Process**:
```java
1. Create authentication token
   UsernamePasswordAuthenticationToken(userid, password)
   
2. Authenticate with Spring Security
   authenticationManager.authenticate(creds)
   
3. Generate JWT tokens
   accessToken = jwtService.getAccessToken(userId)
   refreshToken = jwtService.getRefreshToken(userId)
   
4. Create response
   AuthTokensResponse.of(accessToken, refreshToken, "Bearer")
   
5. Return 200 OK with tokens
```

**Success Response**:
```json
{
  "status": "OK",
  "message": "로그인 성공",
  "data": {
    "accessToken": "...",
    "refreshToken": "...",
    "grantType": "Bearer"
  }
}
```

**Error Response** (401):
```json
{
  "status": "Unauthorized",
  "message": "로그인 실패",
  "data": null
}
```

---

### 4. LogoutService

**Purpose**: Handle user logout and token blacklisting

**Dependencies**:
- `JwtService` - Extract user info
- `RefreshTokenValidator` - Validate tokens
- `JwtBlackListService` - Blacklist tokens

#### Main Method: `logout(RefreshTokenBody body, HttpServletRequest request)`

**Process**:
```java
1. Extract tokens
   accessToken = request.getHeader("Authorization")
   refreshToken = body.getRefreshToken()
   
2. Validate tokens
   refreshTokenValidator.validateToken(refreshToken)
   refreshTokenValidator.validateTokenOwner(refreshToken, userId)
   refreshTokenValidator.validateBlacklistedToken(refreshToken)
   
3. Blacklist both tokens
   jwtBlackListService.blacklistAccessToken(accessToken)
   jwtBlackListService.blacklistRefreshToken(refreshToken)
   
4. Return success response
```

**Success Response** (200):
```json
{
  "status": "OK",
  "message": "로그아웃 성공",
  "data": {
    "message": "[200] 로그아웃 처리 완료"
  }
}
```

**Error Cases**:
- Missing tokens → 400 Bad Request
- Invalid token → 401 Unauthorized
- Server error → 500 Internal Server Error

---

### 5. RegisterService

**Purpose**: Handle user registration and ID validation

**Dependencies**:
- `UserRepository` - Database operations
- `BCryptPasswordEncoder` - Password hashing

#### Main Method: `register(User user)`

**Process**:
```java
1. Encode password
   user.encodeBCryptPassword()
   
2. Check for duplicate ID
   if (userRepository.existsByUserId(userId)) → 400
   
3. Save user to database
   userRepository.save(user)
   
4. Return success response
```

**User Entity Encoding**:
```java
// User.encodeBCryptPassword()
this.password = new BCryptPasswordEncoder().encode(this.password);
```

**Success Response** (200):
```json
{
  "status": "OK",
  "message": "회원가입 성공",
  "data": "[200] 회원가입 성공"
}
```

**Error Cases**:
- Duplicate user ID → 409 Conflict
- Registration failure → 400 Bad Request

#### Method: `checkUserId(String userId)`

**Purpose**: Check if user ID is available

**Returns**:
- 200 OK - ID available
- 409 Conflict - ID already exists

---

### 6. ReissueTokenManageService

**Purpose**: Handle JWT token refresh

**Dependencies**:
- `JwtService` - Generate new tokens
- `RefreshTokenValidator` - Validate refresh token
- `JwtBlackListService` - Blacklist old token

#### Main Method: `reissueToken(RefreshTokenBody body, request, response)`

**Process**:
```java
1. Extract refresh token from body
   
2. Validate refresh token
   refreshTokenValidator.validateToken(token)
   refreshTokenValidator.validateBlacklistedToken(token)
   
3. Extract user ID
   userId = jwtService.getUserIdFromRefreshToken(token)
   
4. Blacklist old refresh token
   jwtBlackListService.blacklistRefreshToken(token)
   
5. Generate new tokens
   newAccessToken = jwtService.getAccessToken(userId)
   newRefreshToken = jwtService.getRefreshToken(userId)
   
6. Set HttpOnly cookie (optional)
   ResponseCookie.from("REFRESH_TOKEN", newRefreshToken)
     .httpOnly(true)
     .secure(true)
     .sameSite("None")
   
7. Return new tokens
```

**Success Response** (200):
```json
{
  "status": "OK",
  "message": "토큰 재발급 성공",
  "data": {
    "accessToken": "new-token",
    "refreshToken": "new-refresh-token",
    "grantType": "Bearer"
  }
}
```

**Security Note**: Old refresh token is blacklisted immediately to prevent reuse

---

### 7. MessageAuthService

**Purpose**: Handle SMS verification for registration and password reset

**Dependencies**:
- CoolSMS SDK (Nurigo)
- Redis for code storage

**Configuration**:
```java
@Value("${coolsms.api.key}")
private String apiKey;

@Value("${coolsms.api.secret}")
private String apiSecretKey;
```

**Constants**:
```java
VERIFY_CODE_EXPIRE_SEC = 3 * 60;  // 3 minutes
ID_VERIFY_CODE_EXPIRE_SEC = 5 * 60;  // 5 minutes
```

#### Main Method: `smsSend(String receiver)`

**Process**:
```java
1. Generate 4-digit random code
   verifyNumber = rand.nextInt(8999) + 1000
   
2. Create SMS message
   Message.setFrom("01040184834")
   Message.setTo(receiver)
   Message.setText("[MoneyMate] 인증번호 [" + code + "]...")
   
3. Send via CoolSMS API
   messageService.send(message)
   
4. Store code in Redis (3-min TTL)
   redisTemplate.set(receiver, code, 180, SECONDS)
   
5. Return success response
```

**Success Response** (200):
```json
{
  "status": "OK",
  "message": "SMS 발송 성공",
  "data": {
    "from": "01040184834",
    "to": "01012345678",
    "text": "[MoneyMate] 인증번호 [1234]..."
  }
}
```

#### Method: `smsVerify(String receiver, Integer verifyCode)`

**Process**:
```java
1. Retrieve code from Redis
   savedCode = redisTemplate.get(receiver)
   
2. Validate code exists
   if (savedCode == null) → 400 EXPIRED
   
3. Validate code matches
   if (!savedCode.equals(verifyCode)) → 400 MISMATCH
   
4. Delete verification code
   redisTemplate.delete(receiver)
   
5. Generate userVerifyCode (UUID substring)
   userVerifyCode = UUID.randomUUID()
     .toString()
     .replace("-", "")
     .substring(0, 12)
   
6. Store userVerifyCode in Redis (5-min TTL)
   redisTemplate.set(receiver, userVerifyCode, 300, SECONDS)
   
7. Return userVerifyCode in response
```

**Success Response** (200):
```json
{
  "status": "OK",
  "message": "SMS 인증 성공",
  "data": {
    "receiver": "01012345678",
    "verifyCode": 1234,
    "resultMessage": "[MoneyMate] 인증 성공",
    "userVerifyCode": "abc123def456"
  }
}
```

**Error Cases**:
- Code expired (> 3 min) → 400 EXPIRED
- Wrong code → 400 MISMATCH
- SMS send failure → 500 Internal Server Error

---

### 8. UserService

**Purpose**: User account management operations

**Dependencies**:
- `UserRepository` - Database operations
- `JwtService` - Extract current user
- `LogoutService` - Logout on delete
- `RedisTemplate` - Verify code validation

#### Main Methods:

##### `deleteUser(RefreshTokenBody body, HttpServletRequest request)`

**Process**:
```java
1. Get current user from token
   userId = jwtService.getAuthUser(request)
   
2. Find user in database
   user = userRepository.findByUserId(userId)
   
3. Delete user from database
   userRepository.delete(user)
   
4. Logout user (blacklist tokens)
   logoutService.logout(body, request)
   
5. Return success response
```

##### `verifyPw(UserDto userDto, HttpServletRequest request)`

**Purpose**: Verify user password (for sensitive operations)

**Process**:
```java
1. Get current user
2. Compare provided password with stored hash
   encoder.matches(password, user.getPassword())
3. Return 200 OK or 401 Unauthorized
```

##### `findUserId(UserAuthRequest request, HttpServletRequest httpRequest)`

**Purpose**: Find user ID by phone number verification

**Process**:
```java
1. Get phone number and userVerifyCode
2. Retrieve stored code from Redis
3. Validate code matches
4. Delete code from Redis
5. Format phone number (010-xxxx-xxxx)
6. Find user by phone number
7. Return user ID
```

**Phone Number Formatting**:
```java
formatPhoneNumber("01012345678") → "010-1234-5678"
```

##### `resetPasswordRequest(UserAuthRequest request, HttpServletRequest httpRequest)`

**Purpose**: Reset password via SMS verification

**Process**:
```java
1. Validate userVerifyCode from Redis
2. Find user by userId and phoneNumber
3. Verify phone number matches
4. Check new password != old password
5. Encode and save new password
6. Delete verification code
7. Return success
```

**Security Checks**:
- User exists
- Phone number matches
- Not same as old password
- Verification code valid

##### `changePassword(UserAuthRequest request, HttpServletRequest httpRequest)`

**Similar to resetPasswordRequest but for authenticated users**

---

### 9. UserDetailsServiceImpl

**Purpose**: Spring Security integration for user authentication

**Type**: Implements `UserDetailsService`

**Method**: `loadUserByUsername(String userId)`

**Process**:
```java
1. Find user in database
   user = userRepository.findByUserId(userId)
   
2. If not found → throw UsernameNotFoundException
   
3. Build UserDetails
   User.withUsername(userId)
       .password(currentUser.getPassword())
       .build()
       
4. Return UserDetails
```

**Usage**: 
- Called by Spring Security during authentication
- Used by `AuthenticationManager.authenticate()`
- Password comparison done by Spring Security

**Why Custom Implementation?**:
- Default Spring Security uses username
- This implementation uses userId instead
- Integrates with custom User entity

---

## Service Dependencies Graph

```
Controllers
    │
    ├─→ LoginService
    │     ├─→ AuthenticationManager (Spring Security)
    │     └─→ JwtService
    │
    ├─→ LogoutService
    │     ├─→ JwtService
    │     ├─→ RefreshTokenValidator
    │     └─→ JwtBlackListService
    │
    ├─→ RegisterService
    │     └─→ UserRepository
    │
    ├─→ ReissueTokenManageService
    │     ├─→ JwtService
    │     ├─→ RefreshTokenValidator
    │     └─→ JwtBlackListService
    │
    ├─→ MessageAuthService
    │     ├─→ CoolSMS SDK
    │     └─→ RedisTemplate
    │
    └─→ UserService
          ├─→ UserRepository
          ├─→ JwtService
          ├─→ LogoutService
          └─→ RedisTemplate
```

---

## Best Practices

### Service Layer Responsibilities

1. **Business Logic Only**:
   - No HTTP-specific code (headers, status codes)
   - No direct request/response handling
   - Return DTOs or entities

2. **Transaction Management**:
   - Use `@Transactional` for database operations
   - Handle rollback scenarios
   - Consider transaction boundaries

3. **Error Handling**:
   - Throw business exceptions
   - Let controllers handle HTTP responses
   - Log detailed errors

4. **Validation**:
   - Validate business rules
   - Check data consistency
   - Verify permissions

### Security Best Practices

1. **Password Handling**:
   - Always use BCrypt
   - Never log passwords
   - Compare with constant-time algorithm

2. **Token Management**:
   - Blacklist on logout
   - Validate before use
   - Set appropriate expiration

3. **SMS Codes**:
   - Short expiration (3-5 minutes)
   - Delete after use
   - Random generation

---

## Testing Strategies

### Unit Testing Services

```java
@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Mock
    private AuthenticationManager authManager;
    
    @Mock
    private JwtService jwtService;
    
    @InjectMocks
    private LoginService loginService;
    
    @Test
    void testSuccessfulLogin() {
        // Given
        UserCredentials creds = new UserCredentials("user", "pass", uuid);
        Authentication auth = mock(Authentication.class);
        when(authManager.authenticate(any())).thenReturn(auth);
        when(jwtService.getAccessToken(any())).thenReturn("access-token");
        
        // When
        ResponseEntity<?> response = loginService.login(creds);
        
        // Then
        assertEquals(200, response.getStatusCodeValue());
        verify(jwtService).getAccessToken("user");
    }
}
```

### Integration Testing

```java
@SpringBootTest
@Transactional
class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testDeleteUser() {
        // Create test user
        User user = createTestUser();
        userRepository.save(user);
        
        // Delete user
        userService.deleteUser(body, request);
        
        // Verify deletion
        assertFalse(userRepository.existsByUserId(user.getUserId()));
    }
}
```

---

## Common Issues and Solutions

### Issue: Token blacklist not working
**Cause**: Redis connection failure  
**Solution**: Check Redis configuration and connectivity

### Issue: SMS not sending
**Cause**: CoolSMS API key invalid  
**Solution**: Verify environment variables set correctly

### Issue: Password verification failing
**Cause**: BCrypt rounds mismatch  
**Solution**: Use same BCryptPasswordEncoder configuration

### Issue: Token refresh creating multiple tokens
**Cause**: Old token not blacklisted  
**Solution**: Ensure blacklisting happens before generating new tokens

---

## Performance Considerations

### Redis Operations
- Blacklist checks are O(1)
- Set TTL to avoid manual cleanup
- Use pipelining for bulk operations

### Database Queries
- Index on userId and phoneNumber
- Use Optional to avoid null checks
- Consider caching user lookups

### JWT Operations
- Reuse Key object (don't regenerate)
- Cache parsed tokens if needed
- Use appropriate expiration times

---

**Package Owner**: Service Team  
**Last Updated**: November 2024  
**Related Documentation**:
- [Parent: auth/CLAUDE.md](../CLAUDE.md)
- [API Layer: api/CLAUDE.md](../api/CLAUDE.md)
- [Application Layer: application/CLAUDE.md](../application/CLAUDE.md)

