# Backend Quality Gates Documentation

## Table of Contents
- [Overview](#overview)
- [Quality Gates Summary](#quality-gates-summary)
- [Automated Testing](#automated-testing)
- [Code Coverage](#code-coverage)
- [SonarCloud Integration](#sonarcloud-integration)
- [Setup and Configuration](#setup-and-configuration)
- [GitHub Actions Workflow](#github-actions-workflow)
- [Viewing Reports and Results](#viewing-reports-and-results)
- [Common Tasks](#common-tasks)
- [Troubleshooting](#troubleshooting)
- [Maintenance](#maintenance)

## Overview

The TodoList backend implements comprehensive quality gates to ensure code quality, security, and maintainability. All quality checks run automatically on every push and pull request through GitHub Actions.

### Quality Gates Enforced

| Gate | Type | Threshold | When | Blocks Merge |
|------|------|-----------|------|--------------|
| Unit Tests | JUnit 5 | 100% pass | Every commit | Yes |
| Code Coverage | JaCoCo | ≥ 50% | Every commit | No (reported) |
| SonarCloud Analysis | Static Analysis | Quality Gate | PRs & main | Yes |
| Build Success | Gradle | Must succeed | Every commit | Yes |

## Quality Gates Summary

### 1. Unit Tests
- **Framework:** JUnit 5
- **Threshold:** All tests must pass
- **Command:** `./gradlew test`
- **Reports:** `build/reports/tests/test/index.html`

### 2. Code Coverage
- **Tool:** JaCoCo 0.8.11
- **Minimum Coverage:** 50%
- **Command:** `./gradlew jacocoTestReport`
- **Reports:**
  - XML: `build/reports/jacoco/test/jacocoTestReport.xml`
  - HTML: `build/reports/jacoco/test/html/index.html`

### 3. SonarCloud Static Analysis
- **Platform:** SonarCloud (cloud-hosted)
- **Project Key:** `ttambunan01-sudo_todolist`
- **Organization:** `ttambunan01-sudo`
- **Quality Gate:** Default (can be customized)
- **Dashboard:** https://sonarcloud.io/project/overview?id=ttambunan01-sudo_todolist

**Analyzed Metrics:**
- Code coverage
- Code smells
- Bugs
- Vulnerabilities
- Security hotspots
- Technical debt
- Duplicated code
- Maintainability rating

**Exclusions:**
- Configuration classes (`**/config/**`)
- Data Transfer Objects (`**/dto/**`)
- Entity classes (`**/entity/**`)
- Enums (`**/enums/**`)

## Automated Testing

### Test Structure
```
src/test/java/
└── com/miniproject/todolist/
    ├── controller/
    │   └── TodoControllerTest.java
    ├── service/
    │   └── TodoServiceTest.java
    └── repository/
        └── TodoRepositoryTest.java
```

### Running Tests Locally

```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport

# Run specific test class
./gradlew test --tests "com.miniproject.todolist.controller.TodoControllerTest"

# Run tests in continuous mode
./gradlew test --continuous
```

### Test Reports

After running tests, view the HTML report:
```bash
# macOS
open build/reports/tests/test/index.html

# Linux
xdg-open build/reports/tests/test/index.html
```

## Code Coverage

### JaCoCo Configuration

Located in `build.gradle.kts`:

```kotlin
jacoco {
    toolVersion = "0.8.11"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)  // Generate report after tests
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)   // For SonarCloud
        html.required.set(true)  // For local viewing
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.50".toBigDecimal()  // 50% minimum
            }
        }
    }
}
```

### Viewing Coverage Reports

```bash
# Generate coverage report
./gradlew jacocoTestReport

# View HTML report (macOS)
open build/reports/jacoco/test/html/index.html

# View HTML report (Linux)
xdg-open build/reports/jacoco/test/html/index.html
```

### Coverage Metrics

- **Line Coverage:** Percentage of code lines executed
- **Branch Coverage:** Percentage of decision branches covered
- **Complexity:** Cyclomatic complexity metric
- **Minimum Target:** 50% overall coverage

## SonarCloud Integration

### How It Works

1. **Trigger:** Runs on pull requests and pushes to `main` branch
2. **Process:**
   - GitHub Actions checks out code
   - Runs tests and generates JaCoCo coverage report
   - Sends code and coverage data to SonarCloud
   - SonarCloud analyzes code quality, security, and coverage
   - Results posted back to PR as comments and checks

3. **Quality Gate:** PR can only merge if SonarCloud quality gate passes

### Configuration

**GitHub Actions Integration:**
Located in `.github/workflows/backend-ci.yml`:

```yaml
sonarcloud-scan:
  runs-on: ubuntu-latest
  needs: build-and-test
  if: github.event_name == 'pull_request' || github.ref == 'refs/heads/main'

  steps:
    - name: Checkout code
      uses: actions/checkout@v4
      with:
        fetch-depth: 0  # Full history for better analysis

    - name: Build and run tests with coverage
      run: ./gradlew build jacocoTestReport

    - name: SonarCloud Scan
      uses: SonarSource/sonarcloud-github-action@master
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      with:
        args: >
          -Dsonar.projectKey=ttambunan01-sudo_todolist
          -Dsonar.organization=ttambunan01-sudo
          -Dsonar.java.binaries=build/classes/java/main
          -Dsonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/test/jacocoTestReport.xml
          -Dsonar.sources=src/main/java
          -Dsonar.tests=src/test/java
          -Dsonar.exclusions=**/config/**,**/dto/**,**/entity/**,**/enums/**
          -Dsonar.java.source=21
```

## Setup and Configuration

### Prerequisites

1. **SonarCloud Account**
   - Sign up at https://sonarcloud.io
   - Connect your GitHub account
   - Create organization (if not exists)

2. **GitHub Repository Access**
   - Admin access to the repository
   - Ability to add secrets

### Step-by-Step Setup

#### 1. Create SonarCloud Project

1. Go to https://sonarcloud.io
2. Click "+" → "Analyze new project"
3. Select `ttambunan01-sudo/todolist` repository
4. Choose "GitHub Actions" as the analysis method
5. Copy the `SONAR_TOKEN` provided

#### 2. Add GitHub Secret

1. Go to GitHub repository → Settings → Secrets and variables → Actions
2. Click "New repository secret"
3. Name: `SONAR_TOKEN`
4. Value: Paste the token from SonarCloud
5. Click "Add secret"

#### 3. Configure Quality Gate (Optional)

**Default Quality Gate (Sonar way):**
- Coverage on new code: > 80%
- Duplicated lines on new code: < 3%
- Maintainability rating on new code: A
- Reliability rating on new code: A
- Security rating on new code: A

**To Customize:**
1. Go to SonarCloud project → Quality Gates
2. Click "Create"
3. Set custom thresholds
4. Assign to project

#### 4. Verify Integration

1. Create a test branch
2. Make a small change
3. Create a pull request
4. Verify SonarCloud check appears in PR
5. Check that analysis completes successfully

## GitHub Actions Workflow

### Workflow Jobs

The CI pipeline consists of three jobs:

```
build-and-test
     ↓
sonarcloud-scan
     ↓
docker-build-push (only on push to main/staging/develop)
```

### Job: build-and-test

**Purpose:** Run tests and generate coverage reports

```yaml
steps:
  1. Checkout code
  2. Set up JDK 21
  3. Run tests (./gradlew test)
  4. Generate coverage report (./gradlew jacocoTestReport)
  5. Build JAR (./gradlew bootJar)
  6. Upload test results (artifact)
  7. Upload coverage report (artifact)
```

### Job: sonarcloud-scan

**Purpose:** Analyze code quality and security

**Runs:** On PRs and pushes to `main`

```yaml
steps:
  1. Checkout code (with full history)
  2. Set up JDK 21
  3. Build and run tests with coverage
  4. Run SonarCloud analysis
```

**Dependencies:** Requires successful `build-and-test` job

### Job: docker-build-push

**Purpose:** Build and push Docker image

**Runs:** Only on pushes to `main`, `staging`, or `develop`

**Dependencies:** Requires both `build-and-test` and `sonarcloud-scan` to pass

## Viewing Reports and Results

### Local Test Reports

```bash
# Run tests and open report
./gradlew test && open build/reports/tests/test/index.html

# Run coverage and open report
./gradlew jacocoTestReport && open build/reports/jacoco/test/html/index.html
```

### GitHub Actions Artifacts

1. Go to GitHub repository → Actions
2. Click on a workflow run
3. Scroll to "Artifacts" section
4. Download:
   - `test-results` - JUnit HTML reports
   - `coverage-report` - JaCoCo coverage reports

### SonarCloud Dashboard

**Access:** https://sonarcloud.io/project/overview?id=ttambunan01-sudo_todolist

**Views:**
- **Overview:** Summary of quality metrics
- **Issues:** Bugs, vulnerabilities, code smells
- **Security Hotspots:** Security-sensitive code requiring review
- **Measures:** Detailed metrics and trends
- **Code:** Line-by-line issue annotations
- **Activity:** History of analyses

**PR Decoration:**
SonarCloud automatically comments on PRs with:
- Quality gate status
- New issues introduced
- Coverage on new code
- Link to detailed analysis

## Common Tasks

### 1. Adjust Coverage Threshold

Edit `build.gradle.kts`:

```kotlin
tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.60".toBigDecimal()  // Change from 0.50 to 0.60
            }
        }
    }
}
```

### 2. Exclude Files from Coverage

Add to `build.gradle.kts`:

```kotlin
tasks.jacocoTestReport {
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/config/**",
                    "**/dto/**",
                    "**/entity/**",
                    "**/Application.class"
                )
            }
        })
    )
}
```

### 3. Disable SonarCloud for Specific PRs

Add to PR description:
```
[skip sonar]
```

Or modify workflow condition in `.github/workflows/backend-ci.yml`.

### 4. Run SonarCloud Locally (Analysis Only)

```bash
# Install SonarScanner
brew install sonar-scanner

# Run local analysis (results sent to SonarCloud)
./gradlew build jacocoTestReport

sonar-scanner \
  -Dsonar.projectKey=ttambunan01-sudo_todolist \
  -Dsonar.organization=ttambunan01-sudo \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=YOUR_SONAR_TOKEN
```

### 5. Fix Common Quality Issues

**High Cyclomatic Complexity:**
```java
// Before: Complex if-else chain
if (status == Status.PENDING) {
    // ...
} else if (status == Status.IN_PROGRESS) {
    // ...
} else if (status == Status.COMPLETED) {
    // ...
}

// After: Strategy pattern or switch expression
return switch (status) {
    case PENDING -> handlePending();
    case IN_PROGRESS -> handleInProgress();
    case COMPLETED -> handleCompleted();
};
```

**Duplicated Code:**
- Extract common logic to utility methods
- Use inheritance or composition
- Apply DRY (Don't Repeat Yourself) principle

**Security Vulnerabilities:**
- Never log sensitive data
- Use parameterized queries (JPA does this automatically)
- Validate all input
- Use HTTPS for external connections

## Troubleshooting

### Issue 1: SonarCloud Analysis Fails with "Automatic Analysis Conflict"

**Error:**
```
You are running CI analysis while Automatic Analysis is enabled.
Please consider disabling one of them.
```

**Solution:**
1. Go to SonarCloud project → Administration → Analysis Method
2. Disable "Automatic Analysis"
3. Use only GitHub Actions CI analysis

**Rationale:** SonarCloud offers two analysis methods:
- Automatic (analyses code directly from GitHub)
- CI-based (analyses from GitHub Actions)

They conflict - choose CI-based for more control.

---

### Issue 2: Gradle SonarQube Plugin Incompatibility

**Error:**
```
'org.gradle.api.plugins.Convention org.gradle.api.internal.plugins.DslObject.getConvention()'
java.lang.NoSuchMethodError
```

**Cause:** SonarQube Gradle plugin versions incompatible with Gradle 9+

**Attempted Plugin Versions (All Failed):**
- `org.sonarqube:6.0.1.5171` ❌
- `org.sonarqube:5.1.0.4882` ❌
- `org.sonarqube:4.4.1.3373` ❌

**Solution:** Use SonarCloud GitHub Action instead

**Implementation:**
1. Remove SonarQube plugin from `build.gradle.kts`
2. Use `SonarSource/sonarcloud-github-action@master` in GitHub Actions
3. Pass configuration via `-D` arguments

**Why This Works:**
- GitHub Action uses its own scanner (not Gradle plugin)
- No Gradle API compatibility issues
- Same analysis quality
- Simpler configuration

---

### Issue 3: Coverage Report Not Found

**Error:**
```
SonarCloud: Coverage report not found
```

**Solution:**
1. Ensure JaCoCo generates XML report:
```kotlin
tasks.jacocoTestReport {
    reports {
        xml.required.set(true)  // Must be true
    }
}
```

2. Verify path in SonarCloud configuration:
```
-Dsonar.coverage.jacoco.xmlReportPaths=build/reports/jacoco/test/jacocoTestReport.xml
```

3. Ensure tests run before analysis:
```bash
./gradlew build jacocoTestReport
```

---

### Issue 4: GitHub Actions Fails on `chmod +x gradlew`

**Error:**
```
Permission denied
```

**Solution:**
```bash
# Locally, ensure gradlew is executable
chmod +x gradlew
git add gradlew
git commit -m "Make gradlew executable"
git push
```

**Prevention:** Always commit with executable bit:
```bash
git update-index --chmod=+x gradlew
```

---

### Issue 5: Secrets Not Available in Workflow

**Error:**
```
env.SONAR_TOKEN is empty
```

**Checklist:**
1. Secret exists in GitHub Settings → Secrets
2. Secret name matches workflow exactly (`SONAR_TOKEN`)
3. Workflow has permissions to access secrets
4. Not running on forked PR (secrets hidden for security)

**For Forked PRs:**
Secrets are not available. Options:
- Require PR from branch (not fork)
- Disable SonarCloud for forked PRs
- Use `pull_request_target` (security risk - review carefully)

---

### Issue 6: Quality Gate Fails on Low Coverage

**Error:**
```
Quality Gate failed: Coverage on new code < 80%
```

**Short-term Solution:**
1. Add tests for new code
2. Focus on critical paths first

**Long-term Solution:**
Adjust quality gate thresholds in SonarCloud:
1. SonarCloud → Quality Gates
2. Modify coverage threshold
3. Consider grandfathering: strict for new code, lenient for overall

---

### Issue 7: Build Hangs on Tests

**Symptom:** Tests run indefinitely

**Common Causes:**
1. Test waiting for timeout
2. Infinite loop in test
3. Deadlock in concurrent test

**Solution:**
```bash
# Run with stack trace
./gradlew test --stacktrace

# Run with debug output
./gradlew test --debug

# Run specific test
./gradlew test --tests "ClassName.testMethod"
```

Add timeout to tests:
```java
@Test
@Timeout(value = 5, unit = TimeUnit.SECONDS)
void testMethod() {
    // Test code
}
```

---

### Issue 8: SonarCloud Shows Wrong Branch

**Issue:** Analysis shows on wrong branch

**Solution:**
Ensure fetch-depth is 0 for full git history:
```yaml
- name: Checkout code
  uses: actions/checkout@v4
  with:
    fetch-depth: 0  # Important for branch detection
```

## Maintenance

### Regular Tasks

**Weekly:**
- Review SonarCloud issues
- Address new security vulnerabilities
- Monitor coverage trends

**Monthly:**
- Update dependencies
- Review and adjust quality gates
- Clean up technical debt

**Quarterly:**
- Review exclusion patterns
- Evaluate coverage targets
- Update documentation

### Dependency Updates

Update JaCoCo version in `build.gradle.kts`:
```kotlin
jacoco {
    toolVersion = "0.8.12"  // Check for latest
}
```

### SonarCloud Token Rotation

1. Generate new token in SonarCloud
2. Update GitHub secret `SONAR_TOKEN`
3. Test with a PR
4. Revoke old token

### Monitoring

**Key Metrics to Track:**
- Coverage trend (should increase over time)
- Number of code smells (should decrease)
- Technical debt ratio
- Security vulnerabilities (should be 0)

**SonarCloud Email Notifications:**
Configure in SonarCloud → My Account → Notifications:
- Quality gate status changes
- New issues on your code
- New security vulnerabilities

## Best Practices

1. **Write Tests First:** Aim for >80% coverage on new code
2. **Fix Issues Early:** Address SonarCloud issues in same PR
3. **Review Coverage Reports:** Don't just chase numbers - ensure meaningful tests
4. **Security First:** Fix all security vulnerabilities immediately
5. **Keep It Green:** Never merge with failing quality gates
6. **Document Exceptions:** If excluding code from coverage, document why
7. **Run Locally:** Test quality gates locally before pushing
8. **Monitor Trends:** Watch for quality degradation over time

## References

- **JaCoCo Documentation:** https://www.jacoco.org/jacoco/trunk/doc/
- **SonarCloud:** https://sonarcloud.io
- **SonarCloud GitHub Action:** https://github.com/SonarSource/sonarcloud-github-action
- **JUnit 5:** https://junit.org/junit5/docs/current/user-guide/
- **Gradle Testing:** https://docs.gradle.org/current/userguide/java_testing.html

## Support

**Issues:** https://github.com/ttambunan01-sudo/todolist/issues
**SonarCloud Project:** https://sonarcloud.io/project/overview?id=ttambunan01-sudo_todolist
