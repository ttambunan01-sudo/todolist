# TodoList Backend Service

![CI Status](https://github.com/ttambunan01-sudo/todolist/actions/workflows/backend-ci.yml/badge.svg)

A modern RESTful API for managing todo items built with Spring Boot 4.0 and Java 21.

## Features

- ✅ Full CRUD operations for todo items
- ✅ Priority management (LOW, MEDIUM, HIGH)
- ✅ Due date tracking
- ✅ Tag-based categorization
- ✅ Pagination support
- ✅ Redis caching for improved performance
- ✅ PostgreSQL database with Flyway migrations
- ✅ Swagger/OpenAPI documentation
- ✅ Comprehensive test coverage
- ✅ Docker containerization
- ✅ CI/CD with GitHub Actions

## Technology Stack

- **Framework:** Spring Boot 4.0.0
- **Language:** Java 21
- **Build Tool:** Gradle (Kotlin DSL)
- **Database:** PostgreSQL 13
- **Cache:** Redis 7
- **Migration:** Flyway
- **Documentation:** SpringDoc OpenAPI 3
- **Testing:** JUnit 5, Mockito
- **Containerization:** Docker

## Quick Start

### Prerequisites

- Java 21 JDK
- PostgreSQL 13+
- Redis 7+
- Docker (optional)

### Local Development

1. **Clone the repository**
   ```bash
   git clone git@github.com:ttambunan01-sudo/todolist.git
   cd todolist
   ```

2. **Configure database**

   Update `src/main/resources/application.yaml` with your PostgreSQL credentials

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the API**
   - API Base URL: `http://localhost:8080/api/v1`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - API Docs: `http://localhost:8080/api-docs`

### Using Docker

```bash
# Build the image
docker build -t todolist-backend:latest .

# Run the container
docker run -p 8080:8080 todolist-backend:latest
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/todos` | Create a new todo |
| GET | `/api/v1/todos` | Get all todos (paginated) |
| GET | `/api/v1/todos/{id}` | Get todo by ID |
| PUT | `/api/v1/todos/{id}` | Update todo |
| DELETE | `/api/v1/todos/{id}` | Delete todo |
| GET | `/api/v1/todos/filter?completed=true` | Filter by completion status |
| GET | `/api/v1/todos/search?query=meeting` | Search by title |

## Testing

```bash
# Run tests
./gradlew test

# Generate coverage report
./gradlew jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```

## CI/CD Pipeline

This project uses GitHub Actions for continuous integration and deployment:

- **On Push/PR:** Runs tests and builds the application
- **On Main Branch:** Builds and pushes Docker image to Docker Hub
- **Test Reports:** Automatically uploaded as artifacts
- **Coverage Reports:** Generated with JaCoCo

See [CI/CD Documentation](docs/CI.md) for more details.

## Docker Image

Pull the latest image:
```bash
docker pull ttambunan01/todolist-backend:latest
```

## Health Checks

- **Liveness:** `GET /actuator/health/liveness`
- **Readiness:** `GET /actuator/health/readiness`
- **Overall Health:** `GET /actuator/health`
- **Metrics:** `GET /actuator/metrics`

## Configuration

Key configuration properties in `application.yaml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/todolist_db
    username: postgres
    password: postgres

  data:
    redis:
      host: localhost
      port: 6379

  cache:
    type: redis
    redis:
      time-to-live: 600000  # 10 minutes
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests: `./gradlew test`
5. Submit a pull request

## License

[Add your license here]

## Authors

[Add your name/team here]

---

**Last Updated:** 2025-11-28

**test**
