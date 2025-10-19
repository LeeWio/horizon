# AGENTS.md

This file provides guidance to Qoder (qoder.com) when working with code in this repository.

## Project Overview

Horizon is a Spring Boot 3.5.5 backend application for a modern blog platform, working in tandem with the Sunrise frontend. It provides RESTful APIs, secure authentication, content management, real-time notifications, and SEO capabilities.

**Tech Stack:**
- Java 24
- Spring Boot 3.5.5 (Web, Data JPA, Security, Redis, Validation)
- MySQL 8 with Hibernate
- Redis for caching
- RabbitMQ for async messaging
- WebSocket (STOMP) for real-time notifications
- JWT authentication (using Hutool library)
- SpringDoc OpenAPI 3 for API documentation
- Spring Boot Actuator + Prometheus for monitoring

## Development Commands

### Build & Run
```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Package without tests
./mvnw clean package -DskipTests
```

### Testing
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=HorizonApplicationTests

# Run tests with coverage
./mvnw test jacoco:report
```

### Code Quality
```bash
# Compile the project
./mvnw compile

# Validate project structure
./mvnw validate
```

## Project Architecture

### Package Structure
```
com.sunrizon.horizon/
├── config/           # Spring configuration beans
│   ├── SecurityConfig.java        # JWT + Spring Security setup
│   ├── RabbitConfig.java         # RabbitMQ message broker config
│   ├── WebSocketConfig.java      # STOMP WebSocket config
│   ├── RedisConfig.java          # Redis cache config
│   └── OpenApiConfig.java        # Swagger/OpenAPI config
├── controller/       # REST API endpoints
├── service/          # Business logic interfaces
├── service/impl/     # Service implementations
├── repository/       # JPA repositories (Spring Data)
├── pojo/            # JPA entities (@Entity classes)
├── dto/             # Request/Response DTOs
├── vo/              # View objects for responses
├── exception/       # Custom exceptions + GlobalExceptionHandler
├── filter/          # Security filters (JWT, XSS)
├── utils/           # Utility classes (JwtUtil, RedisUtil, etc.)
├── messaging/       # RabbitMQ listeners and message classes
├── enums/           # Enums (ResponseCode, etc.)
└── constants/       # Application constants
```

### Key Architectural Patterns

**Layered Architecture:**
- **Controller Layer**: Handles HTTP requests, validates input, delegates to service layer
- **Service Layer**: Contains business logic, orchestrates operations across repositories
- **Repository Layer**: Data access using Spring Data JPA
- **DTO/VO Pattern**: Separates internal entities from API contracts

**Security Architecture:**
- JWT-based stateless authentication
- `JwtAuthenticationFilter` extracts and validates JWT tokens from Authorization header
- `CustomUserDetails` implements Spring Security's UserDetails
- `SecurityConfig` configures Spring Security with custom handlers for auth failures/access denied
- `XssFilter` sanitizes input using Jsoup to prevent XSS attacks
- Method-level security with `@PreAuthorize` annotations

**Async Messaging with RabbitMQ:**
- `RabbitConfig` sets up JSON message converters and dedicated thread pool
- Listeners in `messaging/` package handle async events (OTP emails, user audit notifications)
- Messages sent via `RabbitTemplate` for decoupled, scalable operations

**Real-Time Notifications:**
- WebSocket with STOMP protocol configured in `WebSocketConfig`
- Connection endpoint: `/ws` (with SockJS fallback)
- Client sends to: `/app/*`
- Server broadcasts: `/topic/*` (public) and `/queue/*` (personal)
- User-specific notifications: `/user/{username}/queue/*`

**Caching Strategy:**
- Redis configured for caching frequently accessed data
- `ICacheService` provides caching abstraction
- `RedisUtil` offers low-level Redis operations

**Exception Handling:**
- `GlobalExceptionHandler` provides unified error response format
- Custom exceptions: `BusinessException`, `ResourceNotFoundException`, `ValidationException`
- `ResultResponse` utility wraps all API responses with consistent structure

**Content Moderation:**
- `ContentAudit` entity tracks content audit status
- `AuditService` handles content approval/rejection workflow
- `SensitiveWordUtil` filters inappropriate content

**SEO Management:**
- `ArticleSeo` entity stores meta descriptions, keywords, OG tags
- `SeoConfig` entity stores global SEO settings
- `SeoController` manages SEO configurations

### Database Design

**Core Entities:**
- `User` - User accounts with roles/permissions
- `Article` - Blog posts with content, metadata, SEO
- `Comment` - Nested comments on articles
- `Tag`, `Category`, `Series` - Content organization
- `Interaction` - Likes, bookmarks, shares
- `Follow` - User following relationships
- `Notification` - User notifications
- `ContentAudit` - Content moderation tracking
- `SearchHistory`, `ReadingHistory` - User activity tracking

**Entity Relationships:**
- User ↔ Role (Many-to-Many)
- Role ↔ Permission (Many-to-Many)
- Article ↔ Tag (Many-to-Many)
- Article → Category (Many-to-One)
- Article → User (Many-to-One, author)
- Comment → Article (Many-to-One)
- Comment → User (Many-to-One)
- Interaction → User + Article (tracking user actions)

**JPA Configuration:**
- `ddl-auto: update` - Auto-creates/updates tables (change to `validate` in production)
- `@EnableJpaAuditing` - Automatic `createdAt`/`updatedAt` timestamps

### Configuration Files

**application.yml** contains:
- Database: MySQL connection (localhost:3306/blog)
- Redis: localhost:6379
- RabbitMQ: localhost:5672
- Mail: QQ SMTP for email notifications
- File uploads: max 10MB
- WebSocket: CORS origins (localhost:3000, localhost:8080)
- Actuator endpoints: health, metrics, prometheus
- Swagger UI: /swagger-ui.html
- OpenAPI docs: /v3/api-docs

**Important:** Credentials in application.yml should be externalized using environment variables or Spring profiles for production.

## API Documentation

Access Swagger UI when running locally:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Monitoring & Metrics

**Spring Boot Actuator endpoints:**
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Prometheus: http://localhost:8080/actuator/prometheus
- Info: http://localhost:8080/actuator/info

## Development Notes

### Adding New Features

1. **Create Entity** (if needed) in `pojo/` package
   - Add `@Entity`, `@Table` annotations
   - Use Lombok (`@Data`, `@Builder`, etc.)
   - Consider audit fields (`@CreatedDate`, `@LastModifiedDate`)

2. **Create Repository** in `repository/` package
   - Extend `JpaRepository<Entity, ID>`
   - Add custom query methods if needed

3. **Create DTOs** in `dto/` package for requests, `vo/` for responses
   - Add validation annotations (`@NotNull`, `@Size`, etc.)

4. **Create Service Interface** in `service/` package
   - Define business operations

5. **Implement Service** in `service/impl/` package
   - Add `@Service` annotation
   - Inject repositories with `@Resource`
   - Throw `BusinessException` for business errors

6. **Create Controller** in `controller/` package
   - Add `@RestController` and `@RequestMapping`
   - Use `@PreAuthorize` for authorization
   - Return `ResultResponse<?>` for consistency
   - Add OpenAPI annotations (`@Operation`, `@Tag`)

7. **Add Exception Handling** if needed
   - Custom exceptions in `exception/` package
   - Handle in `GlobalExceptionHandler`

### Security Considerations

- JWT tokens expire after 2 hours (configured in `JwtUtil`)
- Secret key stored in `JwtUtil` - **externalize to environment variables**
- CORS currently allows all origins (`*`) - **restrict in production**
- XSS filtering enabled via `XssFilter`
- Sensitive word filtering via `SensitiveWordUtil`
- Database credentials in application.yml - **use environment variables**

### Testing Strategy

- Tests located in `src/test/java`
- Spring Boot Test with MockMvc for integration tests
- Spring Security Test for auth testing
- Currently minimal test coverage - expand as needed

### RabbitMQ Message Flow

**Queue Names:**
- `email.otp` - OTP email delivery
- `user.audit` - User registration audit notifications

**Message Flow:**
1. Service publishes message to queue via `RabbitTemplate`
2. Listener (in `messaging/` package) consumes message
3. Listener performs async operation (send email, notify admins, etc.)
4. WebSocket notification sent to relevant users

### Common Utilities

- `JwtUtil` - JWT token creation/validation
- `RedisUtil` - Redis operations
- `MailUtil` - Email sending
- `ResultResponse` - Standardized API response wrapper
- `SecurityContextUtil` - Get current authenticated user
- `SensitiveWordUtil` - Content filtering
- `XssUtil` - XSS sanitization

## Blog Display Functionality

The blog display functionality is already implemented through existing controllers and services:

### Homepage Display
- **Trending Articles**: `GET /api/article/trending` with parameters:
  - `type`: VIEW, LIKE, FAVORITE (default: VIEW)
  - `timeRange`: DAY, WEEK, MONTH, ALL (default: WEEK)
- **Article List**: `GET /api/article` with pagination support

### Article Detail Page
- **Article Details**: `GET /api/article/{id}/detail` returns article content with SEO metadata
- **Article Content**: `GET /api/article/{id}` returns basic article information

### Category Page
- **Category List**: `GET /api/category` with pagination support
- **Specific Category**: `GET /api/category/{cid}` get category by ID
- **Category by Name**: `GET /api/category/name/{name}` get category by name
- **Category by Slug**: `GET /api/category/slug/{slug}` get category by slug