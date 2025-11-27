# CI/CD Pipeline Documentation

## Overview

The TodoList Backend service uses GitHub Actions for continuous integration and continuous deployment. The pipeline automatically builds, tests, and deploys the application on every push to the repository.

## Pipeline Architecture

```
┌─────────────┐
│  Developer  │
│  git push   │
└──────┬──────┘
       │
       v
┌─────────────────────────────────────┐
│      GitHub Actions Triggered       │
└──────────────┬──────────────────────┘
               │
               v
       ┌───────────────┐
       │ build-and-test│
       ├───────────────┤
       │ • Setup JDK 21│
       │ • Run Tests   │
       │ • Coverage    │
       │ • Build JAR   │
       └───────┬───────┘
               │
        [Tests Pass?]
               │
               ├─ No ──> ❌ Pipeline Fails
               │
               v Yes
       ┌────────────────┐
       │docker-build-push│ (main branch only)
       ├────────────────┤
       │ • Build Image  │
       │ • Tag (latest) │
       │ • Tag (SHA)    │
       │ • Push to Hub  │
       └───────┬────────┘
               │
               v
       ┌────────────────┐
       │   Docker Hub   │
       │ ttambunan01/   │
       │ todolist-backend│
       └────────────────┘
```

## Workflow Files

### `.github/workflows/backend-ci.yml`

Main CI/CD workflow that handles building, testing, and Docker image creation.

**Location:** `.github/workflows/backend-ci.yml`

## Triggers

The CI pipeline runs on:

1. **Push to main branch** - Full pipeline including Docker build/push
2. **Pull requests to main** - Tests and build only (no Docker push)
3. **Manual trigger** - Via GitHub Actions UI (`workflow_dispatch`)

## Jobs

### Job 1: build-and-test

**Purpose:** Compile code, run tests, and generate reports

**Steps:**

1. **Checkout code** - Uses `actions/checkout@v4`
2. **Set up JDK 21** - Uses `actions/setup-java@v4` with Temurin distribution
3. **Cache Gradle dependencies** - Automatically caches Gradle packages
4. **Grant execute permission** - Makes gradlew executable
5. **Run tests** - Executes `./gradlew test`
6. **Generate coverage report** - Runs `./gradlew jacocoTestReport`
7. **Build JAR** - Creates executable JAR with `./gradlew bootJar`
8. **Upload test results** - Uploads test reports as artifacts
9. **Upload coverage report** - Uploads JaCoCo coverage reports

**Artifacts Created:**
- `test-results` - JUnit test reports
- `coverage-report` - JaCoCo code coverage reports

**Duration:** ~3-5 minutes

### Job 2: docker-build-push

**Purpose:** Build and publish Docker image to Docker Hub

**Conditions:**
- Only runs on `push` events
- Only runs on `main` branch
- Requires `build-and-test` job to succeed

**Steps:**

1. **Checkout code**
2. **Set up Docker Buildx** - Enables multi-platform builds
3. **Login to Docker Hub** - Uses secrets for authentication
4. **Extract metadata** - Generates image tags
5. **Build and push** - Creates and publishes Docker image

**Image Tags Created:**
- `latest` - Always points to the latest main branch build
- `git-<sha>` - Specific commit SHA (e.g., `git-14ae598`)

**Duration:** ~5-8 minutes

## Secrets Required

Configure these in GitHub repository settings:

| Secret Name | Description | Example |
|------------|-------------|---------|
| `DOCKERHUB_USERNAME` | Docker Hub username | `ttambunan01` |
| `DOCKERHUB_TOKEN` | Docker Hub access token | `dckr_pat_xxxxx...` |

**To add secrets:**
1. Go to `https://github.com/ttambunan01-sudo/todolist/settings/secrets/actions`
2. Click "New repository secret"
3. Add name and value
4. Click "Add secret"

## Running Tests Locally

Before pushing, run tests locally to catch issues early:

```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html

# Build the JAR
./gradlew bootJar

# Run the application
java -jar build/libs/todolist-0.0.1-SNAPSHOT.jar
```

## Viewing Pipeline Results

### GitHub Actions UI

1. Go to `https://github.com/ttambunan01-sudo/todolist/actions`
2. Click on a workflow run to see details
3. Click on a job to see logs
4. Download artifacts (test reports, coverage)

### Workflow Status Badge

The README.md includes a status badge:

```markdown
![CI Status](https://github.com/ttambunan01-sudo/todolist/actions/workflows/backend-ci.yml/badge.svg)
```

**Status Indicators:**
- 🟢 Green (passing) - All tests passed, build succeeded
- 🔴 Red (failing) - Tests failed or build error
- 🟡 Yellow (pending) - Workflow is currently running

### Test Reports

After each run:

1. Go to workflow run page
2. Scroll to "Artifacts" section
3. Download `test-results` or `coverage-report`
4. Extract and open `index.html`

## Docker Images

### Pulling Images

```bash
# Pull latest
docker pull ttambunan01/todolist-backend:latest

# Pull specific commit
docker pull ttambunan01/todolist-backend:git-14ae598

# List all tags
docker pull ttambunan01/todolist-backend --all-tags
```

### Running Container

```bash
# Run latest image
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/todolist_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e SPRING_DATA_REDIS_HOST=host.docker.internal \
  ttambunan01/todolist-backend:latest

# With docker-compose
docker-compose up -d
```

### Image Details

- **Base Image:** `eclipse-temurin:21-jre-alpine`
- **Size:** ~350MB
- **Architecture:** linux/amd64, linux/arm64
- **Health Check:** Configured with Spring Actuator
- **Port:** 8080
- **User:** Non-root (spring)

## Build Optimization

### Gradle Caching

GitHub Actions automatically caches Gradle dependencies to speed up builds:

```yaml
- uses: actions/setup-java@v4
  with:
    cache: 'gradle'  # Enables caching
```

**Benefits:**
- First build: ~3-4 minutes (downloads dependencies)
- Subsequent builds: ~1-2 minutes (uses cache)

### Docker Layer Caching

The workflow uses GitHub Actions cache for Docker layers:

```yaml
cache-from: type=gha
cache-to: type=gha,mode=max
```

**Benefits:**
- Faster Docker builds
- Reduced build time by ~50%
- Efficient layer reuse

## Troubleshooting

### Pipeline Fails on Tests

**Problem:** Tests pass locally but fail in CI

**Common Causes:**
1. Database connection issues
2. Redis not available
3. Environment-specific configuration

**Solution:**
```bash
# Check test logs in GitHub Actions
# Look for specific failure messages
# Run tests with same conditions:
./gradlew clean test --no-daemon
```

### Docker Push Fails

**Problem:** Cannot push to Docker Hub

**Common Causes:**
1. Invalid credentials
2. Repository doesn't exist
3. Token expired

**Solution:**
```bash
# Verify secrets are set correctly
# Check Docker Hub repository exists
# Regenerate access token if needed

# Test locally:
docker login -u ttambunan01
docker tag todolist-backend:latest ttambunan01/todolist-backend:test
docker push ttambunan01/todolist-backend:test
```

### Build Takes Too Long

**Problem:** Pipeline exceeds time limits

**Solutions:**
1. Check if caching is working
2. Reduce test execution time
3. Optimize Gradle build
4. Use parallel test execution

### Secrets Not Working

**Problem:** `secrets.DOCKERHUB_USERNAME` is empty

**Solution:**
1. Verify secret name matches exactly (case-sensitive)
2. Check secret is set in repository settings
3. Ensure secret has a value
4. Re-create the secret

## Best Practices

### Commit Messages

Use conventional commits for clarity:

```bash
feat: add new todo endpoint
fix: resolve null pointer exception
test: add service layer tests
docs: update API documentation
ci: improve build performance
```

### Pull Request Workflow

1. Create feature branch: `git checkout -b feature/new-feature`
2. Make changes and commit
3. Push to GitHub: `git push origin feature/new-feature`
4. Create pull request
5. Wait for CI to pass (required)
6. Request review
7. Merge when approved and green

### Testing Strategy

**Before Pushing:**
```bash
# 1. Run tests locally
./gradlew clean test

# 2. Check coverage
./gradlew jacocoTestReport

# 3. Verify build
./gradlew build

# 4. Test Docker build
docker build -t todolist-backend:local .

# 5. Push to GitHub
git push origin main
```

## Monitoring and Alerts

### GitHub Notifications

Enable notifications for:
- Workflow failures
- Pull request status checks
- Deployment events

**Settings:** `Profile > Settings > Notifications > Actions`

### Metrics

Track these metrics:
- **Build Success Rate:** Aim for > 95%
- **Average Build Time:** Target < 5 minutes
- **Test Coverage:** Maintain > 50%
- **Failed Deployments:** Monitor and investigate

## Future Enhancements

### Planned Improvements

1. **Security Scanning**
   - Add Trivy vulnerability scanning
   - OWASP dependency check
   - Secret scanning

2. **Quality Gates**
   - SonarQube integration
   - Code smell detection
   - Technical debt tracking

3. **Deployment Automation**
   - Kubernetes deployment workflow
   - Staging environment
   - Production deployment with approval

4. **Performance Testing**
   - Load testing with k6
   - Performance regression detection
   - Benchmark comparisons

5. **Notifications**
   - Slack integration
   - Email notifications
   - Discord webhooks

## Related Documentation

- [README.md](../README.md) - Project overview and quick start
- [Deployment Guide](DEPLOYMENT.md) - Kubernetes deployment (coming soon)
- [API Documentation](http://localhost:8080/swagger-ui.html) - Swagger UI (when running)

## Support

For issues or questions about the CI/CD pipeline:

1. Check GitHub Actions logs
2. Review this documentation
3. Check recent commits for changes
4. Create an issue on GitHub

---

**Last Updated:** 2025-11-28
**Pipeline Version:** 1.0.0
