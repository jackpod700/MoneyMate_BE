# API Layer (`com.konkuk.moneymate.auth.api`)

## Overview

The `api` package contains the REST API layer for authentication and user management. It includes controllers that handle HTTP requests, request/response DTOs, and Redis configuration for session and cache management.

## Package Structure

```
api/
├── controller/              # REST Controllers
│   ├── LoginController.java
│   ├── RegisterController.java
│   ├── LogoutController.java
│   ├── ReissueTokenController.java
│   ├── UserController.java
│   ├── MessageAuthController.java
│   └── HealthController.java
├── request/                 # Request DTOs
│   ├── UserAuthRequest.java
│   ├── RefreshTokenBody.java
│   ├── SmsMessageRequest.java
│   └── Null.java
├── response/                # Response DTOs
│   ├── AuthTokensResponse.java
│   └── SmsMessageResponse.java
└── RedisConfig.java         # Redis Configuration
```

## Controllers

### 1. LoginController

**Purpose**: Handles user login and JWT token operations

**Endpoints**:

#### `POST /login`
**Description**: User login with credentials  
**Request Body**:
```json
{
  "userid": "string",
  "password": "string",
  "uid": "uuid"
}
```
**Response**: `200 OK`
```json
{
  "status": "OK",
  "message": "로그인 성공",
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "grantType": "Bearer"
  }
}
```
**Error**: `401 Unauthorized` - Invalid credentials

#### `GET /jwt`
**Description**: Debug endpoint to view JWT payload (not for production)  
**Headers**: `Authorization: Bearer <token>`  
**Response**: `200 OK`
```json
{
  "sub": "userId",
  "uid": "user-uuid",
  "exp": timestamp
}
```

**Dependencies**:
- `LoginService` - Business logic
- `JwtService` - Token operations

---

### 2. RegisterController

**Purpose**: Handles user registration and ID validation

**Endpoints**:

#### `POST /register`
**Description**: Register a new user account  
**Request Body**:
```json
{
  "userId": "string",
  "userName": "string",
  "password": "string",
  "phoneNumber": "string",
  "birthday": "date"
}
```
**Response**: `200 OK`
```json
{
  "status": "OK",
  "message": "회원가입 성공",
  "data": "[200] 회원가입 성공"
}
```
**Errors**:
- `400 Bad Request` - Registration failed
- `409 Conflict` - User ID already exists

#### `GET /user/check-id`
**Description**: Check if user ID is available  
**Query Params**: `userId=string`  
**Response**: `200 OK` (available) or `409 Conflict` (already exists)

**Dependencies**:
- `RegisterService` - Registration logic
- `UserRepository` - Database operations

**Security**:
- Password is BCrypt hashed before storage
- Duplicate user ID check enforced

---

### 3. LogoutController

**Purpose**: Handles user logout and token invalidation

**Endpoints**:

#### `POST /logout`
**Description**: Logout and blacklist tokens  
**Headers**: `Authorization: Bearer <accessToken>`  
**Request Body**:
```json
{
  "refreshToken": "string"
}
```
**Response**: `200 OK`
```json
{
  "status": "OK",
  "message": "로그아웃 성공",
  "data": null
}
```

**Process**:
1. Extract access token from header
2. Extract refresh token from body
3. Add both tokens to Redis blacklist
4. Tokens remain blacklisted until natural expiration

**Dependencies**:
- `LogoutService` - Logout logic
- `JwtBlackListService` - Token blacklisting

---

### 4. ReissueTokenController

**Purpose**: Handles JWT token refresh

**Endpoints**:

#### `POST /user/reissue-token`
**Description**: Refresh access and refresh tokens  
**Request Body**:
```json
{
  "refreshToken": "string"
}
```
**Response**: `200 OK`
```json
{
  "status": "OK",
  "message": "토큰 재발급 성공",
  "data": {
    "accessToken": "new-access-token",
    "refreshToken": "new-refresh-token",
    "grantType": "Bearer"
  }
}
```
**Errors**:
- `401 Unauthorized` - Invalid/expired refresh token
- `400 Bad Request` - Token validation failed

**Process**:
1. Validate refresh token (signature, expiration, owner)
2. Check if token is blacklisted
3. Generate new access + refresh tokens
4. Blacklist old tokens
5. Return new tokens

**Dependencies**:
- `ReissueTokenManageService` - Token refresh logic

---

### 5. UserController

**Purpose**: User account management operations

**Endpoints**:

#### `DELETE /user/delete`
**Description**: Delete user account  
**Headers**: `Authorization: Bearer <token>`  
**Request Body**:
```json
{
  "refreshToken": "string"
}
```
**Response**: `200 OK` - Account deleted

#### `POST /user/verify/pw`
**Description**: Verify user password  
**Headers**: `Authorization: Bearer <token>`  
**Request Body**:
```json
{
  "password": "string"
}
```
**Response**: `200 OK` or `401 Unauthorized`

#### `POST /user/find-id`
**Description**: Find user ID by phone verification  
**Request Body**:
```json
{
  "phoneNumber": "string",
  "userVerifyCode": "string"
}
```
**Response**: `200 OK` with user ID

#### `POST /user/reset-pw`
**Description**: Reset password via SMS verification  
**Request Body**:
```json
{
  "userId": "string",
  "phoneNumber": "string",
  "userVerifyCode": "string",
  "password": "new-password"
}
```
**Response**: `200 OK` - Password reset successful

#### `GET /user/info`
**Description**: Get current user information  
**Headers**: `Authorization: Bearer <token>`  
**Response**: `200 OK`
```json
{
  "userId": "string",
  "userName": "string",
  "birthday": "date",
  "phoneNumber": "string"
}
```

#### `PATCH /user/update`
**Description**: Update user information  
**Headers**: `Authorization: Bearer <token>`  
**Request Body** (all fields optional):
```json
{
  "userName": "string",
  "birthday": "date",
  "phoneNumber": "string"
}
```
**Response**: `200 OK`

**Dependencies**:
- `UserService` - User management logic
- `JwtService` - Extract user from token
- `UserRepository` - Database operations

---

### 6. MessageAuthController

**Purpose**: SMS verification for registration and password reset

**Endpoints**:

#### `POST /user/verify/sms-send`
**Description**: Send SMS verification code  
**Request Body**:
```json
{
  "phoneNumber": "string",
  "message": "string"
}
```
**Response**: `200 OK`
```json
{
  "status": "OK",
  "message": "SMS 발송 성공",
  "data": {
    "from": "01040184834",
    "to": "phoneNumber",
    "text": "[MoneyMate] 인증번호 [1234] 을 입력해 주세요."
  }
}
```

**Process**:
1. Generate 4-digit random code
2. Send SMS via CoolSMS API
3. Store code in Redis (3-minute TTL)

#### `POST /user/verify/sms-request`
**Description**: Verify SMS code and get userVerifyCode  
**Request Body**:
```json
{
  "phoneNumber": "string",
  "verifyCode": 1234
}
```
**Response**: `200 OK`
```json
{
  "status": "OK",
  "message": "SMS 인증 성공",
  "data": {
    "receiver": "phoneNumber",
    "verifyCode": 1234,
    "resultMessage": "[MoneyMate] 인증 성공",
    "userVerifyCode": "12-char-uuid"
  }
}
```

**Errors**:
- `400 EXPIRED` - Code expired (> 3 minutes)
- `400 MISMATCH` - Wrong verification code
- `500 Internal Server Error` - SMS send failed

**Process**:
1. Retrieve code from Redis
2. Validate code matches
3. Generate userVerifyCode (UUID, 5-minute TTL)
4. Store userVerifyCode in Redis
5. Delete original verification code

**Dependencies**:
- `MessageAuthService` - SMS logic
- CoolSMS SDK (Nurigo)
- Redis for code storage

---

### 7. HealthController

**Purpose**: Health check endpoint for load balancers

**Endpoints**:

#### `GET /health`
**Description**: Simple health check  
**Response**: `200 OK`
```text
Check Success!!
```

**Use Cases**:
- AWS ELB/ALB health checks
- Kubernetes liveness/readiness probes
- Monitoring systems

---

## Request DTOs

### UserAuthRequest
**Purpose**: User authentication and account recovery

**Fields**:
```java
String userId
String password
String phoneNumber
String userVerifyCode
```

**Used In**:
- Find user ID
- Reset password
- Account verification

---

### RefreshTokenBody
**Purpose**: Refresh token container

**Fields**:
```java
String refreshToken
```

**Used In**:
- Logout
- Token refresh
- Account deletion

---

### SmsMessageRequest
**Purpose**: SMS verification requests

**Fields**:
```java
String phoneNumber
String message
Integer verifyCode
```

**Used In**:
- Send SMS code
- Verify SMS code

---

## Response DTOs

### AuthTokensResponse
**Purpose**: JWT token pair response

**Fields**:
```java
String accessToken
String refreshToken
String grantType  // "Bearer"
```

**Factory Method**:
```java
AuthTokensResponse.of(accessToken, refreshToken, "Bearer")
```

**Used In**:
- Login response
- Token refresh response

---

### SmsMessageResponse
**Purpose**: SMS verification result

**Fields**:
```java
String receiver       // Phone number
Integer verifyCode    // Verification code
String resultMessage  // Result message
String userVerifyCode // UUID for further verification
```

**Factory Method**:
```java
SmsMessageResponse.of(receiver, verifyCode, message, userVerifyCode)
```

---

## Redis Configuration

### RedisConfig.java
**Purpose**: Configure Redis connection and templates

**Beans**:
- `RedisConnectionFactory` - Redis connection
- `RedisTemplate<String, String>` - Key-value operations
- `CacheManager` - Spring Cache integration

**Configuration**:
```properties
spring.data.redis.host=your-host
spring.data.redis.port=6379
spring.data.redis.password=your-password
```

**Usage**:
- Token blacklist storage
- SMS verification codes
- User verify codes
- Session management

**TTL Examples**:
- SMS verification code: 3 minutes
- User verify code: 5 minutes
- Access token blacklist: 5 minutes
- Refresh token blacklist: 1 day

---

## API Response Format

All API responses follow a standard format defined by `ApiResponse<T>`:

```json
{
  "status": "string",     // HTTP status text
  "message": "string",    // User-friendly message
  "data": object          // Response payload (nullable)
}
```

**Messages** are defined in `ApiResponseMessage` enum:
- `USER_LOGIN_SUCCESS`
- `USER_LOGIN_FAIL`
- `USER_REGISTER_SUCCESS`
- `USER_REGISTER_FAIL`
- `USER_ID_EXISTS`
- `USER_ID_AVAILABLE`
- `SMS_SEND_SUCCESS`
- `SMS_VERIFY_SUCCESS`
- `INVALID_REFRESH_TOKEN`
- etc.

---

## Security Considerations

### Password Security
- BCrypt hashing (10 rounds by default)
- Passwords never sent in responses
- Password verification uses constant-time comparison

### Token Security
- Tokens stored in Authorization header
- Refresh tokens can optionally use HttpOnly cookies
- Old tokens blacklisted on refresh/logout
- Tokens expire automatically

### SMS Security
- Verification codes are random 4-digit numbers
- 3-minute expiration for codes
- Codes deleted after successful verification
- Rate limiting recommended (not implemented)

### Input Validation
- Phone number format validation
- User ID uniqueness check
- Password strength validation (client-side)

---

## Error Handling

Controllers use `ResponseEntity<?>` to return appropriate HTTP status codes:

- `200 OK` - Success
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Authentication failed
- `403 Forbidden` - Insufficient permissions
- `409 Conflict` - Resource conflict (duplicate user)
- `500 Internal Server Error` - Server error

Errors return `ApiResponse` with error details:
```json
{
  "status": "BAD_REQUEST",
  "message": "Error description",
  "data": null
}
```

---

## Testing Endpoints

### Postman Collection Structure
```
Moneymate Auth API/
├── Authentication/
│   ├── POST Login
│   ├── POST Register
│   ├── POST Logout
│   └── POST Reissue Token
├── User Management/
│   ├── GET User Info
│   ├── PATCH Update User
│   ├── DELETE Delete User
│   ├── POST Find User ID
│   └── POST Reset Password
├── SMS Verification/
│   ├── POST Send SMS Code
│   └── POST Verify SMS Code
└── Health Check/
    └── GET Health
```

### Test User Credentials
See `src/main/resources/sql/insert_users.sql` for test data

---

## Dependencies

### External Libraries
- Spring Web MVC
- Spring Security
- Spring Data Redis
- CoolSMS SDK (Nurigo)

### Internal Packages
- `com.konkuk.moneymate.activities.user.entity.User`
- `com.konkuk.moneymate.activities.user.repository.UserRepository`
- `com.konkuk.moneymate.auth.service.*`
- `com.konkuk.moneymate.common.ApiResponse`

---

## Best Practices

1. **Controller Responsibilities**:
   - Validate HTTP request format
   - Delegate business logic to services
   - Return appropriate HTTP status codes
   - Never contain business logic

2. **DTO Usage**:
   - Use request DTOs for input validation
   - Use response DTOs for consistent output
   - Never expose entity classes directly

3. **Error Handling**:
   - Always return `ApiResponse` format
   - Provide user-friendly error messages
   - Log detailed errors server-side

4. **Security**:
   - Validate all user input
   - Use secure headers (Authorization)
   - Never log sensitive data (passwords, tokens)

---

**Package Owner**: API Team  
**Last Updated**: November 2024  
**Related Documentation**: 
- [Parent: auth/CLAUDE.md](../CLAUDE.md)
- [Service Layer: service/CLAUDE.md](../service/CLAUDE.md)
- [Application Layer: application/CLAUDE.md](../application/CLAUDE.md)

