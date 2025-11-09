# Authentication & Security Package (`com.konkuk.moneymate.auth`)

## Overview

The `auth` package is the core authentication and security module for the Moneymate application. It implements JWT-based authentication, Spring Security configuration, and manages user authentication workflows including login, registration, token management, and SMS verification.

## Package Structure

```
auth/
├── api/                    # API Layer - Controllers, DTOs, and Services
│   ├── controller/         # REST API controllers (7 controllers)
│   ├── request/            # Request DTOs
│   ├── response/           # Response DTOs
│   ├── service/            # Service Layer - Business Logic (9 services)
│   └── RedisConfig.java    # Redis configuration
├── application/            # Application Layer - Filters and Security Components
│   ├── AuthenticationFilter.java
│   ├── AuthEntryPoint.java
│   ├── RefreshTokenValidator.java
│   ├── UserCredentials.java
│   └── JwtProvider.java
├── exception/              # Custom Exceptions
├── SecurityConfig.java     # Spring Security Configuration
└── OpenAiConfig.java       # Swagger/OpenAPI Configuration
```

**Note**: Service layer has been refactored and moved to `auth.api.service` package.

## Core Components

### Security Configuration

#### `SecurityConfig.java`
**Purpose**: Main Spring Security configuration class

**Key Features**:
- JWT-based stateless authentication
- BCrypt password encoding
- CORS configuration
- Public endpoint definitions
- Security filter chain setup

**Permit-All Patterns** (No Authentication Required):
- `/login`, `/register` - Authentication endpoints
- `/health` - Health check
- `/test/**` - Test pages
- `/swagger-ui/**`, `/v3/api-docs/**` - API documentation
- `/css/**`, `/js/**`, `/images/**` - Static resources
- `/api/proxy/**` - API proxies

**Key Methods**:
```java
@Bean SecurityFilterChain filterChain(HttpSecurity http)
@Bean AuthenticationManager authenticationManager(AuthenticationConfiguration)
@Bean PasswordEncoder passwordEncoder()
@Bean CorsConfigurationSource corsConfigurationSource()
```

**Session Management**: STATELESS (No server-side sessions)

#### `OpenAiConfig.java`
**Purpose**: Configures Swagger/OpenAPI documentation

**Configuration**:
- Title: "Moneymate Rest API"
- Description: "apis"
- Version: "1.0"

## Sub-Packages

### 1. API Layer (`auth.api`)
Contains REST controllers, request/response DTOs, services, and Redis configuration.

**Controllers**:
- `LoginController` - User login
- `RegisterController` - User registration
- `LogoutController` - User logout
- `ReissueTokenController` - Token refresh
- `UserController` - User management (find ID, reset password)
- `MessageAuthController` - SMS authentication
- `HealthController` - Health check endpoint

**Services** (in `auth.api.service`):
- `JwtService` - JWT token generation and parsing
- `JwtBlackListService` - Token blacklist management (Redis)
- `LoginService` - Login business logic
- `RegisterService` - Registration business logic
- `LogoutService` - Logout business logic
- `ReissueTokenManageService` - Token refresh logic
- `MessageAuthService` - SMS verification service
- `UserService` - User management service
- `UserDetailsServiceImpl` - Spring Security UserDetailsService

**See**: [api/CLAUDE.md](api/CLAUDE.md) for details

### 2. Application Layer (`auth.application`)
Contains Spring Security filters and authentication components.

**Key Components**:
- `AuthenticationFilter` - JWT validation filter
- `AuthEntryPoint` - Custom 401 handler
- `RefreshTokenValidator` - Refresh token validation logic
- `UserCredentials` - User credentials record
- `JwtProvider` - (Empty placeholder)

**See**: [application/CLAUDE.md](application/CLAUDE.md) for details

### 3. Service Layer (`auth.api.service`)
Contains business logic for authentication and token management.

**Location**: Services have been moved to `auth.api.service` package (refactored)

**Services**:
- `JwtService` - JWT token generation and parsing
- `JwtBlackListService` - Token blacklist management (Redis)
- `LoginService` - Login business logic
- `RegisterService` - Registration business logic
- `LogoutService` - Logout business logic
- `ReissueTokenManageService` - Token refresh logic
- `MessageAuthService` - SMS verification service
- `UserService` - User management service
- `UserDetailsServiceImpl` - Spring Security UserDetailsService

**See**: [api/service/CLAUDE.md](api/service/CLAUDE.md) for details

### 4. Exception Package (`auth.exception`)
Custom authentication exceptions.

**Exceptions**:
- `AccessTokenExpiredException` - Access token expired
- `RefreshTokenExpiredException` - Refresh token expired
- `InvalidTokenException` - Invalid token format/signature
- `UnAuthorizationException` - Unauthorized access

### 5. Templates Package (`auth.templates`)
Test pages, AI agent templates, and API proxy controllers.

**Components**:
- `TestStockPage` - Stock price test page controller
- `NaverStockProxyController` - Naver stock API proxy
- `EodhdProxyController` - EODHD stock API proxy
- `MarketValueRankingRefresher` - Market ranking scheduler
- `$page$ai/` - AI agent test pages (v1, v2, v3)

**See**: [templates/CLAUDE.md](../common/templates/CLAUDE.md) for details

## Authentication Flow

### 1. Registration Flow
```
Client → RegisterController.register()
       → RegisterService.register()
       → UserRepository.save()
       → Response: 200 OK
```

### 2. Login Flow
```
Client → LoginController.login()
       → LoginService.login()
       → AuthenticationManager.authenticate()
       → JwtService.getAccessToken() + getRefreshToken()
       → Response: { accessToken, refreshToken, tokenType: "Bearer" }
```

### 3. API Request Flow (Authenticated)
```
Client (with Bearer token) → AuthenticationFilter.doFilterInternal()
                           → JwtBlackListService.validateAccessTokenNotBlacklisted()
                           → JwtService.getAuthUser()
                           → SecurityContextHolder.setAuthentication()
                           → API Controller
                           → Response
```

### 4. Token Refresh Flow
```
Client → ReissueTokenController.reissueToken()
       → RefreshTokenValidator.validateToken()
       → RefreshTokenValidator.validateTokenOwner()
       → RefreshTokenValidator.validateBlacklistedToken()
       → JwtService.getAccessToken() + getRefreshToken()
       → Old tokens blacklisted in Redis
       → Response: { new accessToken, new refreshToken }
```

### 5. Logout Flow
```
Client → LogoutController.logout()
       → LogoutService.logout()
       → JwtBlackListService.blacklistAccessToken()
       → JwtBlackListService.blacklistRefreshToken()
       → Tokens stored in Redis with TTL
       → Response: 200 OK
```

### 6. SMS Verification Flow
```
Client → MessageAuthController.smsSend()
       → MessageAuthService.smsSend()
       → CoolSMS API call
       → Verification code stored in Redis (3 min TTL)
       → Response: SMS sent

Client → MessageAuthController.smsVerify()
       → MessageAuthService.smsVerify()
       → Verify code from Redis
       → Generate userVerifyCode
       → Store userVerifyCode in Redis (5 min TTL)
       → Response: { userVerifyCode }
```

## JWT Token Structure

### Access Token
**Expiration**: 5 minutes  
**Payload**:
```json
{
  "sub": "userId",
  "uid": "user-uuid",
  "exp": timestamp
}
```

### Refresh Token
**Expiration**: 1 day  
**Payload**:
```json
{
  "sub": "userId",
  "exp": timestamp
}
```

### Token Format
```
Authorization: Bearer <token>
```

## Token Blacklist (Redis)

When users log out or refresh tokens, old tokens are added to Redis blacklist:

**Key Patterns**:
- Access Token: `blacklist:access:<token>`
- Refresh Token: `blacklist:refresh:<token>`

**TTL**: Matches token expiration time  
**Auto-cleanup**: Redis automatically removes expired entries

## Security Features

### Password Encoding
- **Algorithm**: BCrypt
- **Rounds**: Default (10)
- Passwords are hashed before storage

### CORS Configuration
**Allowed Origins**:
- `http://localhost:8080`
- `http://moneymate.s3-website.ap-northeast-2.amazonaws.com`

**Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS  
**Allowed Headers**: All (*)  
**Credentials**: Enabled

### CSRF Protection
**Status**: Disabled (Stateless JWT authentication)

### Session Management
**Policy**: STATELESS (No server-side sessions)

## SMS Verification (CoolSMS)

**Provider**: CoolSMS (Nurigo SDK)  
**Sender Number**: 01040184834  
**Code Format**: 4-digit random number  
**Code Expiration**: 3 minutes  
**UserVerifyCode Expiration**: 5 minutes

**Configuration**:
```properties
coolsms.api.key=${COOLSMS_API_KEY}
coolsms.api.secret=${COOLSMS_API_SECRET}
```

## Dependencies

### External
- Spring Security
- Spring Data Redis
- JJWT (JWT library)
- CoolSMS SDK (Nurigo)

### Internal
- `activities.entity.User` - User entity
- `activities.repository.UserRepository` - User data access
- `common.ApiResponse` - Standard response wrapper
- `common.ApiResponseMessage` - Response message enum

## Error Handling

### Authentication Errors
- **401 Unauthorized**: Invalid/expired token, blacklisted token
- **403 Forbidden**: Insufficient permissions
- **409 Conflict**: Duplicate user ID during registration
- **400 Bad Request**: Invalid request data

### Custom Exception Handling
`AuthEntryPoint` handles all authentication failures and returns JSON:
```json
{
  "error": "error message"
}
```

## Testing Endpoints

### Health Check
```
GET /health
Response: 200 OK
```

### Test Pages
- `/test/page/stock` - Stock price inquiry test
- `/test/page/agent/v1` - AI Agent test v1
- `/test/page/agent/v2` - AI Agent test v2 (Asset analysis)
- `/test/page/agent/v3` - AI Agent test v3 (Portfolio analysis)

## Best Practices

1. **Always use Bearer prefix**: `Authorization: Bearer <token>`
2. **Store refresh tokens securely**: Use HttpOnly cookies or secure storage
3. **Handle token expiration**: Implement automatic refresh logic
4. **Never expose JWT secret**: Use environment variables
5. **Logout properly**: Always call logout endpoint to blacklist tokens
6. **SMS verification**: Verify phone number before registration/password reset

## Configuration Requirements

### Environment Variables
```properties
# JWT (auto-generated in JwtService)
# No external configuration needed

# SMS (CoolSMS)
COOLSMS_API_KEY=your_api_key
COOLSMS_API_SECRET=your_api_secret

# Redis
spring.data.redis.host=your-redis-host
spring.data.redis.port=6379
spring.data.redis.password=your-password
```

## Future Enhancements

- [ ] OAuth2 social login (Google, Kakao, Naver)
- [ ] Multi-factor authentication (MFA)
- [ ] Biometric authentication support
- [ ] Token rotation policy
- [ ] Rate limiting for login attempts
- [ ] Security audit logging

---

**Package Owner**: Authentication Team  
**Last Updated**: November 2024  
**Spring Security Version**: 6.x (Spring Boot 3.4.3)

