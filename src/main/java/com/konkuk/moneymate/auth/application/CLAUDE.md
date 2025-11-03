# Application Layer (`com.konkuk.moneymate.auth.application`)

## Overview

The `application` package contains the core application-level security components for JWT authentication. It includes Spring Security filters, authentication entry points, and token validation logic that intercept and process all incoming HTTP requests.

## Package Structure

```
application/
├── AuthenticationFilter.java      # JWT validation filter
├── AuthEntryPoint.java           # Custom 401 handler
├── RefreshTokenValidator.java    # Refresh token validation
├── UserCredentials.java          # User credentials record
└── JwtProvider.java              # (Empty placeholder)
```

## Core Components

### 1. AuthenticationFilter

**Purpose**: Spring Security filter that validates JWT tokens on every request

**Type**: `OncePerRequestFilter` - Guaranteed to execute once per request

**Key Responsibilities**:
- Extract JWT token from Authorization header
- Validate token is not blacklisted
- Parse and verify token signature
- Extract user information from token
- Set authentication in SecurityContext
- Handle token validation errors

**Filter Chain Position**:
```
Client Request
  ↓
AuthenticationFilter (JWT validation)
  ↓
UsernamePasswordAuthenticationFilter (Spring Security)
  ↓
Other Security Filters
  ↓
Controller
```

#### Core Method: `doFilterInternal()`

**Process Flow**:
```java
1. Extract Authorization header
   └→ If null: Skip to next filter (public endpoint)
   
2. Validate token not blacklisted
   └→ JwtBlackListService.validateAccessTokenNotBlacklisted()
   
3. Parse token and extract user
   └→ JwtService.getAuthUser(request)
   
4. Create Authentication object
   └→ UsernamePasswordAuthenticationToken(user, null, emptyList)
   
5. Set in SecurityContext
   └→ SecurityContextHolder.getContext().setAuthentication()
   
6. Continue filter chain
   └→ filterChain.doFilter(request, response)
```

**Error Handling**:

```java
// Invalid Token Exception
catch (InvalidTokenException | JwtException e) {
    response.setStatus(401);
    response.setContentType("application/json");
    response.getWriter().write(
        "{\"status\":\"Unauthorized\",\"message\":\"" + e.getMessage() + "\"}"
    );
    return; // Stop filter chain
}

// Unexpected Server Error
catch (Exception e) {
    response.setStatus(500);
    response.setContentType("application/json");
    response.getWriter().write(
        "{\"status\":\"Server Error\",\"message\":\"" + e.getMessage() + "\"}"
    );
    return; // Stop filter chain
}
```

**Dependencies**:
- `JwtService` - Token parsing and user extraction
- `JwtBlackListService` - Blacklist validation

**Configuration**:
Registered in `SecurityConfig.java`:
```java
.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
```

---

### 2. AuthEntryPoint

**Purpose**: Custom authentication entry point that handles unauthorized access attempts

**Type**: Implements `AuthenticationEntryPoint`

**Triggered When**:
- User tries to access protected endpoint without token
- Token validation fails in `AuthenticationFilter`
- Spring Security detects unauthorized access

**Method**: `commence()`

**Response**:
```java
Status: 401 Unauthorized
Content-Type: application/json; charset=UTF-8
Body: {
  "error": "error message from AuthenticationException"
}
```

**Process**:
```java
1. Set HTTP status to 401
2. Set response encoding to UTF-8
3. Set content type to application/json
4. Write error JSON to response
```

**Usage in SecurityConfig**:
```java
http.exceptionHandling((exceptionHandling) -> 
    exceptionHandling.authenticationEntryPoint(exceptionHandler)
)
```

**Why Custom Entry Point?**:
- Default Spring Security returns HTML error pages
- REST APIs need JSON responses
- Provides consistent error format with `ApiResponse`

---

### 3. RefreshTokenValidator

**Purpose**: Validates refresh tokens through multiple security checks

**Validation Steps**:

#### Step 1: Token Validity Check
**Method**: `validateToken(String token)`

**Checks**:
- Token signature verification
- Token expiration check
- Token structure validation

**Exceptions**:
- `RefreshTokenExpiredException` - Token expired
- `JwtException` - Invalid signature or malformed token

**Implementation**:
```java
public void validateToken(String token) {
    try {
        jwtService.getUserIdFromRefreshToken(token);
    } catch (ExpiredJwtException e) {
        throw new RefreshTokenExpiredException("Invalid refresh token");
    } catch (JwtException e) {
        throw new RefreshTokenExpiredException("Invalid refresh token");
    }
}
```

#### Step 2: Token Owner Validation
**Method**: `validateTokenOwner(String refreshToken, String id)`

**Purpose**: Ensure token belongs to the requesting user

**Process**:
1. Extract userId from token payload
2. Compare with provided user ID
3. Throw exception if mismatch

**Security**: Prevents token theft attacks

#### Step 3: Blacklist Check
**Method**: `validateBlacklistedToken(String refreshToken)`

**Purpose**: Ensure token has not been logged out/invalidated

**Process**:
1. Check Redis for blacklist entry
2. Throw exception if found

**Why Blacklist Refresh Tokens?**:
- User logged out → invalidate token immediately
- Token compromised → revoke access
- Token refreshed → invalidate old token

**Usage Example** (in ReissueTokenManageService):
```java
refreshTokenValidator.validateToken(refreshToken);
refreshTokenValidator.validateTokenOwner(refreshToken, userId);
refreshTokenValidator.validateBlacklistedToken(refreshToken);
// All checks passed → issue new tokens
```

**Dependencies**:
- `JwtService` - Token parsing
- `JwtBlackListService` - Blacklist checking

---

### 4. UserCredentials

**Purpose**: Immutable user credentials data container

**Type**: Java Record (immutable data class)

**Structure**:
```java
public record UserCredentials(
    String userid,
    String password,
    UUID uid
) {}
```

**Usage**:
- Login request body deserialization
- Pass credentials to `AuthenticationManager`
- Temporary object during authentication

**Why Record?**:
- Immutable by default (security)
- Automatic equals/hashCode
- Compact syntax
- Thread-safe

**Example**:
```java
// Client sends JSON
{
  "userid": "user123",
  "password": "hashedpw",
  "uid": "uuid-here"
}

// Spring deserializes to
UserCredentials credentials = new UserCredentials("user123", "hashedpw", uuid);

// Used in authentication
UsernamePasswordAuthenticationToken creds = 
    new UsernamePasswordAuthenticationToken(
        credentials.userid(), 
        credentials.password()
    );
```

---

### 5. JwtProvider

**Status**: Empty placeholder class

**Purpose**: Reserved for future JWT provider abstraction

**Current State**:
```java
public class JwtProvider {
    // Empty
}
```

**Potential Future Use**:
- Abstract JWT generation logic
- Support multiple JWT libraries
- Implement JWT refresh strategies
- Handle JWT key rotation

**Current Implementation**: All JWT logic is in `JwtService`

---

## Authentication Flow

### Complete Request Authentication Flow

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Client Request                                           │
│    GET /api/asset/all                                       │
│    Authorization: Bearer eyJhbGciOiJIUzI1NiIs...          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. AuthenticationFilter.doFilterInternal()                  │
│    - Extract Authorization header                           │
│    - Get token: "Bearer eyJhbGci..."                       │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. JwtBlackListService.validateAccessTokenNotBlacklisted()  │
│    - Check Redis: blacklist:access:<token>                  │
│    - If found → throw InvalidTokenException                 │
│    - If not found → continue                                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. JwtService.getAuthUser(request)                          │
│    - Parse JWT token                                        │
│    - Verify signature                                       │
│    - Check expiration                                       │
│    - Extract subject (userId)                               │
│    - Return: "user123"                                      │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. Create Authentication                                    │
│    new UsernamePasswordAuthenticationToken(                 │
│        "user123",  // principal                             │
│        null,       // credentials (not needed after auth)   │
│        Collections.emptyList()  // authorities             │
│    )                                                        │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 6. SecurityContextHolder.setAuthentication()                │
│    - Store authentication in ThreadLocal                    │
│    - Available to all downstream components                 │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 7. Continue Filter Chain                                    │
│    filterChain.doFilter(request, response)                  │
│    → Other security filters                                 │
│    → Dispatcher Servlet                                     │
│    → Controller method                                      │
└─────────────────────────────────────────────────────────────┘
```

### Error Path (Invalid Token)

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Client Request with Invalid/Expired Token               │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. AuthenticationFilter catches InvalidTokenException       │
│    - Set status: 401                                        │
│    - Write JSON error response                              │
│    - return (stop filter chain)                             │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. Response to Client                                       │
│    Status: 401 Unauthorized                                 │
│    Body: {"status":"Unauthorized","message":"..."}         │
└─────────────────────────────────────────────────────────────┘
```

### No Token Path (Public Endpoint)

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Client Request without Token                            │
│    POST /login                                              │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. AuthenticationFilter                                     │
│    - Authorization header is null                           │
│    - Skip validation (public endpoint)                      │
│    - Continue to next filter                                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. SecurityFilterChain                                      │
│    - Check if /login is in PERMIT_ALL_PATTERNS             │
│    - Yes → Allow access                                     │
│    - No → Trigger AuthEntryPoint (401)                     │
└─────────────────────────────────────────────────────────────┘
```

---

## Security Considerations

### Thread Safety
- `SecurityContextHolder` uses ThreadLocal
- Each request has isolated security context
- No shared state between requests

### Performance
- Filter runs on every request
- Blacklist check is Redis lookup (fast)
- Token parsing uses cached key
- No database queries in filter

### Token Validation
- Signature verification prevents tampering
- Expiration check prevents replay attacks
- Blacklist prevents use of old tokens
- Owner validation prevents token theft

### Error Handling
- Never expose sensitive error details
- Log detailed errors server-side
- Return generic messages to client
- Always return JSON (not HTML)

---

## Best Practices

### Filter Implementation
1. **One Responsibility**: Only validate JWT, delegate to services
2. **Fail Fast**: Return immediately on validation failure
3. **No Business Logic**: Keep authentication separate from authorization
4. **Proper Exception Handling**: Catch all exceptions, return appropriate status

### Token Validation
1. **Validate Signature First**: Prevents parsing untrusted data
2. **Check Expiration**: Reject expired tokens immediately
3. **Verify Not Blacklisted**: Prevent use of revoked tokens
4. **Extract Minimal Data**: Only get what's needed (userId)

### Error Responses
1. **Consistent Format**: Always use JSON
2. **Appropriate Status Codes**: 401 for auth, 500 for server errors
3. **User-Friendly Messages**: Don't expose implementation details
4. **UTF-8 Encoding**: Support international characters

---

## Testing Considerations

### Unit Testing AuthenticationFilter
```java
@Test
void testValidToken() {
    // Mock request with valid token
    // Mock services to return success
    // Assert SecurityContext is set
    // Assert filter chain continues
}

@Test
void testInvalidToken() {
    // Mock request with invalid token
    // Mock service to throw exception
    // Assert 401 response
    // Assert filter chain stops
}

@Test
void testNoToken() {
    // Mock request without token
    // Assert filter chain continues
    // Assert SecurityContext is empty
}
```

### Integration Testing
```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationIntegrationTest {
    @Test
    void testProtectedEndpointWithValidToken() {
        // Call /api/asset/all with valid token
        // Expect 200 OK
    }
    
    @Test
    void testProtectedEndpointWithoutToken() {
        // Call /api/asset/all without token
        // Expect 401 Unauthorized
    }
}
```

---

## Debugging Tips

### Enable Debug Logging
```properties
logging.level.com.konkuk.moneymate.auth.application=DEBUG
```

### Common Issues

**Issue**: "Token is null" errors
- **Cause**: Frontend not sending Authorization header
- **Solution**: Check header format: `Authorization: Bearer <token>`

**Issue**: Token valid but 401 error
- **Cause**: Token might be blacklisted
- **Solution**: Check Redis for blacklist entry

**Issue**: "Invalid signature" errors
- **Cause**: JWT secret key mismatch
- **Solution**: Ensure same key used for signing and verification

**Issue**: Filter not executing
- **Cause**: Filter not registered in SecurityConfig
- **Solution**: Check `addFilterBefore()` configuration

---

## Dependencies

### External
- Spring Security Core
- Spring Web
- JJWT Library

### Internal
- `auth.service.JwtService`
- `auth.service.JwtBlackListService`
- `auth.exception.*`

---

## Future Enhancements

- [ ] Support multiple JWT issuers
- [ ] Implement JWT key rotation
- [ ] Add rate limiting per user
- [ ] Support API key authentication
- [ ] Add request signing verification
- [ ] Implement token introspection endpoint

---

**Package Owner**: Security Team  
**Last Updated**: November 2024  
**Related Documentation**:
- [Parent: auth/CLAUDE.md](../CLAUDE.md)
- [Service Layer: service/CLAUDE.md](../service/CLAUDE.md)
- [API Layer: api/CLAUDE.md](../api/CLAUDE.md)

