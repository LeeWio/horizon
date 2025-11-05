# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Communication Standards

**IMPORTANT: Language Requirements**
- **Communication Language:** All conversations, explanations, and discussions with me must be in **中文 (Chinese)**
- **Code Language:** All code, comments, variable names, and technical documentation must be in **English only**
- **Documentation:** All technical documentation, README files, and code comments should be written in English

When explaining concepts, debugging issues, or discussing implementation details, respond in Chinese. When writing or modifying code, use English for all code elements, comments, and documentation.

## Project Overview

Horizon is a comprehensive Spring Boot blog platform backend that provides RESTful APIs, secure authentication, and scalable content management. It's designed to work with Sunrise (React frontend) as a full-stack blog solution.

**Key Technologies:**
- **Spring Boot 3.5.5** with Java 24
- **Spring Security** with JWT authentication
- **Spring Data JPA** with MySQL 8
- **Redis** for caching and sessions
- **RabbitMQ** for asynchronous messaging
- **WebSocket** for real-time notifications
- **SpringDoc OpenAPI** for API documentation
- **Lombok** for boilerplate reduction
- **Hutool** for Java utilities

## Development Commands

```bash
# Build the project
./mvnw clean compile

# Run tests
./mvnw test

# Run application (development)
./mvnw spring-boot:run

# Build JAR for production
./mvnw clean package

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Generate API docs
./mvnw asciidoctor:process-asciidoc
```

## Architecture

### Package Structure
```
src/main/java/com/sunrizon/horizon/
├── config/           # Configuration classes (Security, Redis, RabbitMQ, etc.)
├── controller/       # REST API controllers
├── dto/             # Data Transfer Objects for requests/responses
├── enums/           # Enumeration classes
├── exception/       # Custom exception handlers
├── filter/          # JWT and security filters
├── messaging/       # RabbitMQ message handlers
├── pojo/            # Persistent objects (entities)
├── repository/      # JPA repositories
├── service/         # Business logic layer
├── utils/           # Utility classes
└── vo/              # View objects for API responses
```

### Core Features Implemented

**User Management:**
- JWT-based authentication with role-based access control
- User registration with email verification
- Multi-state user management (PENDING, ACTIVE, INACTIVE, BANNED)
- Password reset and OTP verification

**Content Management:**
- Article CRUD operations with draft/published/archived states
- Category and tag system
- Article series management
- SEO optimization (meta tags, sitemap, robots.txt)
- Content moderation and audit workflow

**Engagement Features:**
- Nested comment system with moderation
- Article interactions (likes, bookmarks, shares)
- User following/follower system
- Real-time notifications via WebSocket
- Reading history tracking

**Search & Discovery:**
- Full-text search across articles
- Advanced filtering (author, category, tags, date ranges)
- Search history and suggestions
- Trending articles and popular content

**Infrastructure:**
- Redis caching for performance
- RabbitMQ for async processing (email notifications, content publishing)
- WebSocket for real-time features
- Comprehensive monitoring with Spring Actuator + Prometheus
- API documentation with Swagger/OpenAPI

### Database Schema

**Core Entities:**
- `User` - User accounts and profiles
- `Article` - Blog posts with SEO metadata
- `Comment` - Nested comment structure
- `Category` & `Tag` - Content organization
- `Series` - Article collections
- `Interaction` - User engagement metrics
- `Notification` - Real-time notifications
- `ReadingHistory` - User reading tracking

### Security Configuration

- JWT tokens with configurable expiration
- Role-based access control (ADMIN, USER, MODERATOR)
- CORS configuration for frontend integration
- XSS protection with Jsoup filtering
- Password encryption with BCrypt

### External Service Integration

**Email Service:**
- QQ SMTP configuration for notifications
- Email verification for registration
- Password reset emails

**File Upload:**
- Multipart file support (10MB limit)
- Configurable upload directory

**Monitoring:**
- Spring Actuator endpoints at `/actuator`
- Prometheus metrics export
- Health checks for DB, Redis, RabbitMQ

## Configuration

### Application Configuration (application.yml)
- **Database:** MySQL on localhost:3306 (database: blog)
- **Redis:** localhost:6379
- **RabbitMQ:** localhost:5672
- **Server:** Port 8080
- **File Upload:** Directory `uploads`, max 10MB

### Key Configuration Classes
- `SecurityConfig` - JWT authentication and authorization
- `RedisConfig` - Redis template and serialization
- `RabbitConfig` - Message queue configuration
- `WebSocketConfig` - Real-time notification setup
- `OpenApiConfig` - Swagger documentation

## API Documentation

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI Docs:** http://localhost:8080/v3/api-docs
- **Actuator:** http://localhost:8080/actuator

## Testing

Run test suite with `./mvnw test`. The project includes:
- Unit tests for services and repositories
- Integration tests with MockMvc
- Spring Security test support
- RabbitMQ test integration

## Development Notes

### Message Queue Usage
The system uses RabbitMQ for:
- Email notifications (user registration, password reset)
- Content publishing workflows
- User audit notifications to administrators

### Real-time Features
WebSocket integration provides:
- Live notifications for likes, comments, follows
- Real-time user presence status
- Instant content updates

### Caching Strategy
Redis is used for:
- User session storage
- Frequently accessed articles
- Search history and suggestions
- API rate limiting

### Security Considerations
- All sensitive endpoints require JWT authentication
- Admin endpoints protected by role-based access
- XSS filtering on all user-generated content
- Input validation on all DTOs
- Database queries use parameterized statements

## Getting Started

1. **Prerequisites:** Java 24, Maven 3.6+, MySQL 8, Redis, RabbitMQ
2. **Database Setup:** Create `blog` database in MySQL
3. **Configuration:** Update `application.yml` with your credentials
4. **Run:** `./mvnw spring-boot:run`
5. **Access:** API at http://localhost:8080, docs at /swagger-ui.html

## Current Development Status

The backend provides comprehensive blog functionality with user management, content creation, social features, and administrative tools. See `BLOG_FEATURES.md` for detailed feature status and roadmap.