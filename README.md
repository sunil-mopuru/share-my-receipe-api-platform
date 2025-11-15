# Share My Recipe API Platform

A modern recipe publishing platform with a secure, well-documented REST API that enables public browsing of recipes, chef sign-up, JWT-protected authoring endpoints, and chef following features.

## Features

1. **Public Recipes Endpoint**
   - List recipes with filters (keyword search, date range, chef)
   - Pagination with configurable page size
   - Response includes pagination metadata

2. **Authentication & Chef Onboarding**
   - Chef sign-up with email verification (optional in v1)
   - JWT-based authentication (access token with short TTL)
   - Role-based access control (user, chef, admin)

3. **Recipe Authoring**
   - Create recipes with title, summary, ingredients, steps, labels, and images
   - Update/delete own recipes (admins can moderate)
   - Draft vs Published states
   - Upload 1..n images per recipe

4. **Chef Following Service**
   - Chefs can follow/unfollow other chefs
   - View recipes from followed chefs

5. **Asynchronous Processing**
   - Recipe operations are queued for background processing
   - Separate worker application consumes queue and processes data

## Technology Stack

- **Backend**: Spring Boot 3.1, Java 17
- **Database**: H2 (dev), PostgreSQL (prod)
- **Security**: Spring Security, JWT
- **Messaging**: RabbitMQ
- **Build Tool**: Maven

## Project Structure

```
├── src/main/java/com/recipes
│   ├── ShareMyRecipeApiPlatformApplication.java  # Main application
│   ├── controllers/                              # REST controllers
│   ├── entities/                                 # JPA entities
│   ├── payload/                                  # DTOs for requests/responses
│   ├── repositories/                             # Spring Data JPA repositories
│   ├── security/                                 # JWT security configuration
│   └── services/                                 # Business logic (if needed)
│
├── worker/                                       # Separate worker application
│   ├── src/main/java/com/recipes/worker
│   │   ├── RecipeWorkerApplication.java          # Worker application
│   │   ├── RabbitMQConfig.java                  # RabbitMQ configuration
│   │   └── MessageListeners.java                # Message processors
│   └── pom.xml                                  # Worker Maven configuration
│
├── pom.xml                                       # Main application Maven configuration
└── README.md                                     # This file
```

## API Endpoints

### Authentication
- `POST /api/auth/signup` - Register a new chef
- `POST /api/auth/signin` - Authenticate and get JWT token

### Public Recipes
- `GET /api/public/recipes` - List public recipes with filters

### Chef Following
- `POST /api/chefs/{id}/follow` - Follow a chef
- `DELETE /api/chefs/{id}/follow` - Unfollow a chef
- `GET /api/chefs/following` - Get list of followed chefs
- `GET /api/chefs/followers` - Get list of followers

### Recipe Management (JWT required)
- `POST /api/recipes` - Create a new recipe
- `PUT /api/recipes/{id}` - Update a recipe
- `PUT /api/recipes/{id}/publish` - Publish a recipe
- `DELETE /api/recipes/{id}` - Delete a recipe

### Followed Chefs Recipes (JWT required)
- `GET /api/followed-recipes` - List recipes from followed chefs

## Setup and Running

1. **Prerequisites**
   - Java 17
   - Maven 3.8+
   - RabbitMQ server

2. **Run RabbitMQ**
   ```bash
   # Using Docker
   docker run -d --hostname my-rabbit --name some-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
   ```

3. **Build and Run Main Application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Build and Run Worker Application**
   ```bash
   cd worker
   mvn clean install
   mvn spring-boot:run
   ```

## Configuration

The application supports multiple database configurations:

- **Default (H2 in-memory)**: For development and testing
- **MySQL**: For production deployments
- **PostgreSQL**: Alternative production database

To switch between profiles:
```bash
# Run with default H2 in-memory database
mvn spring-boot:run

# Run with MySQL database
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Configuration files:
- `application.properties`: Default configuration
- `application-mysql.properties`: MySQL configuration
- `application-test.properties`: Test configuration

The application can also be configured via `application.properties`:

- JWT secret and expiration
- Database connection
- RabbitMQ connection
- File upload limits

## Development

### Code Structure
- Entities: JPA entities representing database tables
- Repositories: Spring Data JPA repositories for database access
- Controllers: REST endpoints
- Security: JWT-based authentication and authorization
- Payload: DTOs for API requests and responses

### Testing

The application includes comprehensive unit and integration tests:

- **Unit Tests**: Test individual components like entities, services, and utilities
- **Integration Tests**: Test the complete flow of API endpoints and database operations
- **Repository Tests**: Test database operations with TestEntityManager
- **Controller Tests**: Test REST endpoints with MockMvc

To run tests:
```bash
# Run all tests
mvn test

# Run tests for main application
mvn test -pl .

# Run tests for worker application
mvn test -pl worker
```

Test profiles are configured to use in-memory H2 databases for isolated testing.

## Deployment

For production deployment:
1. Configure PostgreSQL database
2. Update `application.properties` with production settings
3. Set strong JWT secret
4. Configure RabbitMQ for production
5. Deploy both main application and worker
