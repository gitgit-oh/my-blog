# Authentication Endpoints

<cite>
**Referenced Files in This Document**
- [AdminController.java](file://blog-backend/src/main/java/com/blog/controller/AdminController.java)
- [AdminService.java](file://blog-backend/src/main/java/com/blog/service/AdminService.java)
- [JwtUtil.java](file://blog-backend/src/main/java/com/blog/util/JwtUtil.java)
- [JwtInterceptor.java](file://blog-backend/src/main/java/com/blog/config/JwtInterceptor.java)
- [WebConfig.java](file://blog-backend/src/main/java/com/blog/config/WebConfig.java)
- [application.yml](file://blog-backend/src/main/resources/application.yml)
- [Admin.java](file://blog-backend/src/main/java/com/blog/entity/Admin.java)
- [AdminMapper.java](file://blog-backend/src/main/java/com/blog/mapper/AdminMapper.java)
- [Login.vue](file://blog-frontend/src/views/admin/Login.vue)
- [admin.js](file://blog-frontend/src/api/admin.js)
- [request.js](file://blog-frontend/src/api/request.js)
- [auth.js](file://blog-frontend/src/stores/auth.js)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Project Structure](#project-structure)
3. [Core Components](#core-components)
4. [Architecture Overview](#architecture-overview)
5. [Detailed Component Analysis](#detailed-component-analysis)
6. [Dependency Analysis](#dependency-analysis)
7. [Performance Considerations](#performance-considerations)
8. [Troubleshooting Guide](#troubleshooting-guide)
9. [Conclusion](#conclusion)

## Introduction
This document provides comprehensive API documentation for the admin authentication endpoints, focusing on the POST /api/admin/login endpoint. It covers request/response schemas, authentication flow, token generation, security considerations, and practical usage examples with curl commands. The system uses JWT tokens for secure admin access and includes a dedicated JWT interceptor middleware for protecting admin routes.

## Project Structure
The authentication system spans both backend and frontend components:

```mermaid
graph TB
subgraph "Backend"
AC["AdminController<br/>REST Controller"]
AS["AdminService<br/>Business Logic"]
JU["JwtUtil<br/>Token Utility"]
WI["JwtInterceptor<br/>Security Middleware"]
WC["WebConfig<br/>Interceptor Registration"]
AM["AdminMapper<br/>Data Access"]
AE["Admin Entity<br/>Domain Model"]
YML["application.yml<br/>Configuration"]
end
subgraph "Frontend"
LV["Login.vue<br/>Admin Login View"]
AJ["admin.js<br/>API Module"]
RJ["request.js<br/>HTTP Client"]
AU["auth.js<br/>Auth Store"]
end
AC --> AS
AC --> JU
AS --> AM
AM --> AE
WI --> JU
WC --> WI
YML --> JU
YML --> WC
LV --> AJ
AJ --> RJ
AU --> RJ
RJ --> AC
```

**Diagram sources**
- [AdminController.java:19-44](file://blog-backend/src/main/java/com/blog/controller/AdminController.java#L19-L44)
- [AdminService.java:9-33](file://blog-backend/src/main/java/com/blog/service/AdminService.java#L9-L33)
- [JwtUtil.java:12-56](file://blog-backend/src/main/java/com/blog/util/JwtUtil.java#L12-L56)
- [JwtInterceptor.java:10-35](file://blog-backend/src/main/java/com/blog/config/JwtInterceptor.java#L10-L35)
- [WebConfig.java:8-38](file://blog-backend/src/main/java/com/blog/config/WebConfig.java#L8-L38)
- [application.yml:27-32](file://blog-backend/src/main/resources/application.yml#L27-L32)

**Section sources**
- [AdminController.java:19-44](file://blog-backend/src/main/java/com/blog/controller/AdminController.java#L19-L44)
- [application.yml:27-32](file://blog-backend/src/main/resources/application.yml#L27-L32)

## Core Components
The authentication system consists of several key components working together:

### Backend Components
- **AdminController**: Handles admin authentication requests and exposes the login endpoint
- **AdminService**: Manages admin account operations and password verification
- **JwtUtil**: Provides JWT token generation, validation, and extraction functionality
- **JwtInterceptor**: Implements security middleware for protecting admin routes
- **WebConfig**: Registers interceptors and configures CORS settings

### Frontend Components
- **Login.vue**: Admin login interface with form validation
- **admin.js**: API module for admin-related requests
- **request.js**: HTTP client with automatic token injection and error handling
- **auth.js**: Pinia store for managing authentication state

**Section sources**
- [AdminController.java:25-44](file://blog-backend/src/main/java/com/blog/controller/AdminController.java#L25-L44)
- [AdminService.java:13-22](file://blog-backend/src/main/java/com/blog/service/AdminService.java#L13-L22)
- [JwtUtil.java:21-55](file://blog-backend/src/main/java/com/blog/util/JwtUtil.java#L21-L55)
- [JwtInterceptor.java:16-34](file://blog-backend/src/main/java/com/blog/config/JwtInterceptor.java#L16-L34)
- [WebConfig.java:17-22](file://blog-backend/src/main/java/com/blog/config/WebConfig.java#L17-L22)

## Architecture Overview
The authentication architecture follows a layered approach with clear separation of concerns:

```mermaid
sequenceDiagram
participant Client as "Admin Client"
participant Controller as "AdminController"
participant Service as "AdminService"
participant Mapper as "AdminMapper"
participant Util as "JwtUtil"
participant Interceptor as "JwtInterceptor"
participant Config as "WebConfig"
Note over Client,Config : Authentication Flow
Client->>Controller : POST /api/admin/login
Controller->>Service : findByUsername(username)
Service->>Mapper : SELECT admin WHERE username=?
Mapper-->>Service : Admin record
Service->>Service : checkPassword(raw, hash)
Service-->>Controller : Admin object
Controller->>Util : generateToken(username)
Util-->>Controller : JWT token
Controller-->>Client : {token}
Note over Client,Config : Protected Route Access
Client->>Interceptor : Request to /api/admin/*
Interceptor->>Util : validateToken(bearer)
Util-->>Interceptor : boolean
Interceptor-->>Client : 401 Unauthorized or 200 OK
```

**Diagram sources**
- [AdminController.java:34-44](file://blog-backend/src/main/java/com/blog/controller/AdminController.java#L34-L44)
- [AdminService.java:16-22](file://blog-backend/src/main/java/com/blog/service/AdminService.java#L16-L22)
- [AdminMapper.java:9-10](file://blog-backend/src/main/java/com/blog/mapper/AdminMapper.java#L9-L10)
- [JwtUtil.java:25-47](file://blog-backend/src/main/java/com/blog/util/JwtUtil.java#L25-L47)
- [JwtInterceptor.java:16-34](file://blog-backend/src/main/java/com/blog/config/JwtInterceptor.java#L16-L34)
- [WebConfig.java:18-21](file://blog-backend/src/main/java/com/blog/config/WebConfig.java#L18-L21)

## Detailed Component Analysis

### POST /api/admin/login Endpoint

#### Request Schema
The login endpoint accepts a JSON payload with the following structure:

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| username | string | Yes | Administrator username |
| password | string | Yes | Plain text password |

Example request body:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

#### Successful Response
On successful authentication, the endpoint returns a 200 OK status with a JSON object containing the JWT token:

Response body:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY3OTk5MDQwMCwiZXhwIjoxNjc5OTk5MDQwMH0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
}
```

#### Error Responses
The endpoint returns appropriate HTTP status codes for different error scenarios:

- **401 Unauthorized**: Invalid credentials (username not found or incorrect password)
  ```json
  {
    "message": "Invalid credentials"
  }
  ```

#### Implementation Details
The login endpoint follows these steps:
1. Extract username and password from request body
2. Lookup admin user by username
3. Verify password using BCrypt hashing
4. Generate JWT token if authentication succeeds
5. Return token in response body

**Section sources**
- [AdminController.java:34-44](file://blog-backend/src/main/java/com/blog/controller/AdminController.java#L34-L44)
- [AdminService.java:16-22](file://blog-backend/src/main/java/com/blog/service/AdminService.java#L16-L22)

### Token Generation Process

#### JWT Configuration
The system uses HMAC SHA-256 signing with configurable secret and expiration:

- **Secret Key**: Configured via `blog.jwt.secret` property
- **Expiration**: Configured via `blog.jwt.expiration` property (in milliseconds)
- **Default Values**: 
  - Secret: "MyBlogSecretKey2024ForJwtTokenGenerationOnly"
  - Expiration: 86400000 milliseconds (24 hours)

#### Token Structure
Generated tokens follow standard JWT format with three parts separated by dots:
1. Header: Algorithm and token type information
2. Payload: Claims including subject (username) and timestamps
3. Signature: HMAC signature for token validation

#### Token Validation
The JwtUtil component provides comprehensive token validation:
- Verifies HMAC signature using configured secret
- Checks token expiration against current time
- Extracts username from token claims

**Section sources**
- [JwtUtil.java:15-34](file://blog-backend/src/main/java/com/blog/util/JwtUtil.java#L15-L34)
- [application.yml:28-30](file://blog-backend/src/main/resources/application.yml#L28-L30)

### Security Middleware Integration

#### JWT Interceptor
The JwtInterceptor provides transparent authentication for protected routes:

```mermaid
flowchart TD
Start([Incoming Request]) --> CheckURI["Check URI starts with /api/admin"]
CheckURI --> IsLogin{"Is /api/admin/login?"}
IsLogin --> |Yes| Allow["Allow Access"]
IsLogin --> |No| CheckAuth["Check Authorization Header"]
CheckAuth --> HasAuth{"Has Bearer Token?"}
HasAuth --> |No| Return401["Return 401 Unauthorized"]
HasAuth --> |Yes| ValidateToken["Validate JWT Token"]
ValidateToken --> TokenValid{"Token Valid?"}
TokenValid --> |No| Return401
TokenValid --> |Yes| Allow
Allow --> End([Proceed to Controller])
```

**Diagram sources**
- [JwtInterceptor.java:16-34](file://blog-backend/src/main/java/com/blog/config/JwtInterceptor.java#L16-L34)

#### Interceptor Registration
The WebConfig registers the JWT interceptor for all admin routes except login:

- **Protected Routes**: `/api/admin/**`
- **Excluded Routes**: `/api/admin/login`
- **Registration Pattern**: `addPathPatterns("/api/admin/**").excludePathPatterns("/api/admin/login")`

**Section sources**
- [JwtInterceptor.java:16-34](file://blog-backend/src/main/java/com/blog/config/JwtInterceptor.java#L16-L34)
- [WebConfig.java:18-21](file://blog-backend/src/main/java/com/blog/config/WebConfig.java#L18-L21)

### Frontend Integration

#### Authentication Flow
The frontend handles authentication through a coordinated flow:

```mermaid
sequenceDiagram
participant Vue as "Login.vue"
participant API as "admin.js"
participant HTTP as "request.js"
participant Store as "auth.js"
participant Backend as "AdminController"
Vue->>API : login(credentials)
API->>HTTP : POST /api/admin/login
HTTP->>Backend : Request with credentials
Backend-->>HTTP : {token}
HTTP-->>API : Response
API->>Store : setToken(response.token)
Store->>Store : Save to localStorage
API-->>Vue : Success
Vue->>Vue : Redirect to /admin
```

**Diagram sources**
- [Login.vue:32-41](file://blog-frontend/src/views/admin/Login.vue#L32-L41)
- [admin.js:3](file://blog-frontend/src/api/admin.js#L3)
- [request.js:9-18](file://blog-frontend/src/api/request.js#L9-L18)
- [auth.js:7-10](file://blog-frontend/src/stores/auth.js#L7-L10)

#### Automatic Token Injection
The frontend HTTP client automatically adds authentication headers:

- **Header Format**: `Authorization: Bearer <token>`
- **Condition**: Only added when token exists in store
- **Persistence**: Token stored in localStorage for session persistence

#### Error Handling
The frontend implements robust error handling:
- **401 Status**: Triggers automatic logout and redirects to login page
- **Form Validation**: Prevents submission with empty credentials
- **User Feedback**: Displays meaningful error messages

**Section sources**
- [Login.vue:32-41](file://blog-frontend/src/views/admin/Login.vue#L32-L41)
- [admin.js:3](file://blog-frontend/src/api/admin.js#L3)
- [request.js:9-18](file://blog-frontend/src/api/request.js#L9-L18)
- [auth.js:7-15](file://blog-frontend/src/stores/auth.js#L7-L15)

## Dependency Analysis

```mermaid
graph TB
subgraph "Authentication Dependencies"
AC["AdminController"] --> AS["AdminService"]
AC --> JU["JwtUtil"]
AS --> AM["AdminMapper"]
AM --> AE["Admin Entity"]
WI["JwtInterceptor"] --> JU
WC["WebConfig"] --> WI
RJ["request.js"] --> AU["auth.js"]
LV["Login.vue"] --> AJ["admin.js"]
end
subgraph "External Dependencies"
JU --> JWT["Java JWT Library"]
AS --> BCrypt["BCrypt Password Encoder"]
RJ --> Axios["Axios HTTP Client"]
end
subgraph "Configuration"
YML["application.yml"] --> JU
YML --> WC
WC --> WI
end
```

**Diagram sources**
- [AdminController.java:25-29](file://blog-backend/src/main/java/com/blog/controller/AdminController.java#L25-L29)
- [AdminService.java:13-14](file://blog-backend/src/main/java/com/blog/service/AdminService.java#L13-L14)
- [JwtUtil.java:3](file://blog-backend/src/main/java/com/blog/util/JwtUtil.java#L3)
- [WebConfig.java:12](file://blog-backend/src/main/java/com/blog/config/WebConfig.java#L12)
- [request.js:1](file://blog-frontend/src/api/request.js#L1)

### Component Coupling
- **High Cohesion**: Each component has a single responsibility
- **Low Coupling**: Components interact through well-defined interfaces
- **Clear Dependencies**: Explicit dependency declarations in constructor injection

### Security Considerations
- **Password Storage**: Uses BCrypt hashing with salt
- **Token Security**: HMAC SHA-256 signing with configurable secret
- **Transport Security**: Should be used over HTTPS in production
- **Token Expiration**: Built-in expiration prevents indefinite access
- **CORS Configuration**: Allows cross-origin requests for development

**Section sources**
- [AdminService.java:14](file://blog-backend/src/main/java/com/blog/service/AdminService.java#L14)
- [JwtUtil.java:21-23](file://blog-backend/src/main/java/com/blog/util/JwtUtil.java#L21-L23)
- [WebConfig.java:31-37](file://blog-backend/src/main/java/com/blog/config/WebConfig.java#L31-L37)

## Performance Considerations
- **Token Validation**: Minimal overhead with constant-time validation
- **Database Queries**: Single query per login attempt with username indexing
- **Memory Usage**: Tokens are validated in-memory without database round-trips
- **Caching**: Consider implementing token blacklisting for logout functionality
- **Connection Pooling**: Database connections managed by Spring Boot auto-configuration

## Troubleshooting Guide

### Common Issues and Solutions

#### Login Failures
**Symptoms**: 401 Unauthorized responses
**Causes**:
- Incorrect username or password
- User not found in database
- Password hash mismatch

**Solutions**:
1. Verify credentials match database records
2. Check password encoding configuration
3. Confirm user exists in admin table

#### Token Validation Errors
**Symptoms**: 401 Unauthorized on protected routes
**Causes**:
- Expired token
- Invalid signature
- Missing Authorization header
- Wrong token format

**Solutions**:
1. Generate new token via login endpoint
2. Verify JWT secret matches backend configuration
3. Ensure proper Bearer token format
4. Check token expiration settings

#### Frontend Authentication Issues
**Symptoms**: Automatic logout or redirect loops
**Causes**:
- Token not stored in localStorage
- HTTP interceptor not adding Authorization header
- CORS configuration blocking requests

**Solutions**:
1. Verify localStorage contains token
2. Check request interceptor configuration
3. Review CORS settings in WebConfig
4. Ensure proper route protection registration

**Section sources**
- [AdminController.java:39-40](file://blog-backend/src/main/java/com/blog/controller/AdminController.java#L39-L40)
- [JwtInterceptor.java:20-31](file://blog-backend/src/main/java/com/blog/config/JwtInterceptor.java#L20-L31)
- [request.js:20-29](file://blog-frontend/src/api/request.js#L20-L29)

## Conclusion
The admin authentication system provides a secure, well-structured solution for administrator access control. The implementation follows modern security practices with proper password hashing, JWT tokenization, and comprehensive middleware protection. The system is designed for easy integration with both backend and frontend components while maintaining clear separation of concerns and robust error handling.

Key strengths include:
- Secure password storage with BCrypt
- Configurable JWT token settings
- Transparent middleware protection
- Comprehensive frontend integration
- Clear error handling and user feedback

The system can be easily extended with additional security features such as token refresh mechanisms, rate limiting, and advanced logging capabilities.