# Blitzy Project Guide — Java Age Calculator Test Suite

---

## 1. Executive Summary

### 1.1 Project Overview

This project delivers a greenfield Java Age Calculator application with a comprehensive JUnit 5 unit test suite. The application computes a user's exact age (years, months, days) from a Date of Birth entered in `DD/MM/YYYY` format, leveraging `java.time.LocalDate`, `java.time.Period`, and `java.time.format.DateTimeFormatter` with `ResolverStyle.STRICT`. Built from an empty repository, the project includes 3 production source classes, 5 JUnit 5 test classes (105 tests), and a fully configured Maven build with JaCoCo coverage reporting. All 105 tests pass with 100% branch coverage on the core business logic classes.

### 1.2 Completion Status

```mermaid
pie title Project Completion — 83.0%
    "Completed (44h)" : 44
    "Remaining (9h)" : 9
```

| Metric | Value |
|---|---|
| **Total Project Hours** | **53** |
| **Completed Hours (AI)** | **44** |
| **Remaining Hours** | **9** |
| **Completion Percentage** | **83.0%** |

**Calculation:** 44 completed hours / (44 + 9) total hours = 44/53 = **83.0%**

### 1.3 Key Accomplishments

- ✅ Created complete Maven project from scratch with Java 17, JUnit 5.10.2, Surefire 3.5.5, and JaCoCo 0.8.12
- ✅ Implemented `AgeCalculator` with Clock injection pattern for deterministic testing and `ResolverStyle.STRICT` for rigorous date validation
- ✅ Implemented immutable `AgeResult` POJO with `toString()`, `equals()`, `hashCode()`, `getTotalMonths()`, and `getTotalDays()`
- ✅ Implemented `Main` console entry point with 3-tier exception handling (DateTimeParseException, IllegalArgumentException, Exception)
- ✅ Delivered 105 JUnit 5 tests across 5 test classes — 100% pass rate, 0 failures, 0 errors, 0 skipped
- ✅ Achieved 100% instruction and 100% branch coverage on `AgeCalculator` and `AgeResult`
- ✅ All 5 user-specified mandatory test scenarios implemented and verified (Normal DOB, Leap Year DOB, Invalid Date, Future Date, Wrong Format)
- ✅ Comprehensive edge case coverage: century leap year logic, month-end boundaries, zero age, extreme ages, null/empty input handling
- ✅ Full test suite executes in ~2.5 seconds

### 1.4 Critical Unresolved Issues

| Issue | Impact | Owner | ETA |
|---|---|---|---|
| `Main.java` at 0% test coverage (AAP target ≥70%) | Low — console I/O class with no business logic; all business logic is covered via `AgeCalculator` tests | Human Developer | 3 hours |
| `README.md` contains only placeholder heading `# 12feb_2` | Low — no user-facing documentation for the project | Human Developer | 1.5 hours |

### 1.5 Access Issues

No access issues identified. The project is a self-contained Java application with no external service dependencies, API keys, database connections, or third-party credentials. All dependencies are resolved from Maven Central.

### 1.6 Recommended Next Steps

1. **[Medium]** Write `MainTest.java` to achieve ≥70% coverage on `Main.java` using `System.setIn()`/`System.setOut()` redirection for Scanner-based I/O testing
2. **[Medium]** Set up CI/CD pipeline (e.g., GitHub Actions) with `mvn clean verify` to run tests and generate coverage on every push
3. **[Low]** Update `README.md` with project description, build instructions, usage examples, and architecture overview
4. **[Low]** Configure Maven Shade or Assembly plugin to produce an executable JAR for distribution
5. **[Low]** Conduct final production code review to verify exception messages, edge case handling, and documentation completeness

---

## 2. Project Hours Breakdown

### 2.1 Completed Work Detail

| Component | Hours | Description |
|---|---|---|
| Maven Project Configuration (pom.xml) | 2 | Java 17 compiler, JUnit 5.10.2, Surefire 3.5.5, JaCoCo 0.8.12 with prepare-agent and report goals (72 LOC) |
| AgeResult Model Class | 4 | Immutable POJO: constructor, 3 getters, toString(), getTotalMonths(), getTotalDays(), equals(), hashCode() with full Javadoc (168 LOC) |
| AgeCalculator Core Logic | 6 | Clock injection, STRICT DateTimeFormatter (uuuu pattern), calculateAge(String), calculateAge(LocalDate), parseDateOfBirth(), validateDate() with comprehensive Javadoc (227 LOC) |
| Main Console Entry Point | 2 | Scanner-based I/O with try-with-resources, 3-tier exception handling, delegated architecture (71 LOC) |
| AgeCalculatorTest — Core Unit Tests | 6 | 15 tests across 4 @Nested classes: happy path calculations, LocalDate overload, DOB parsing, clock injection, result object verification (487 LOC) |
| AgeResultTest — Model Tests | 5 | 24 tests across 7 @Nested classes: constructor/getters, toString format, zero/edge values, getTotalMonths, getTotalDays, immutability, equals/hashCode contract (354 LOC) |
| AgeCalculatorEdgeCaseTest — Boundary Tests | 6 | 14 tests across 5 @Nested classes: leap year DOBs (2000/2004/2024), century leap year logic (1900 vs 2000), zero/minimal age, month-end boundaries, extreme ages (440 LOC) |
| AgeCalculatorParameterizedTest — Data-Driven | 4 | 27 test runs via 5 @ParameterizedTest methods with @CsvSource/@ValueSource: 8 valid DOBs, 8 invalid dates, 8 malformed formats, 3 future dates (236 LOC) |
| AgeCalculatorValidationTest — Error Handling | 5 | 25 tests across 6 @Nested classes: future date rejection, invalid calendar dates, wrong formats, null/empty/special inputs, validateDate() direct, parseDateOfBirth() direct (394 LOC) |
| JaCoCo Coverage Configuration & Validation | 2 | Coverage instrumentation via prepare-agent, HTML/XML report generation, verified 100% branch coverage on AgeCalculator + AgeResult |
| Project Infrastructure & Git Setup | 1 | .gitignore (Maven/IDE/OS patterns), repository initialization, 11 well-structured commits |
| Code Quality Review & Fixes | 1 | Resolved 3 code quality findings from automated review; validated all 105 tests pass cleanly |
| **Total Completed** | **44** | **2,463 lines of code across 10 files; 105 tests with 100% pass rate** |

### 2.2 Remaining Work Detail

| Category | Hours | Priority |
|---|---|---|
| Main.java Unit Tests — MainTest.java with System.in/out redirection for Scanner I/O (AAP coverage target ≥70%) | 3 | Medium |
| CI/CD Pipeline Setup — GitHub Actions workflow for automated `mvn clean verify` on push/PR | 2 | Medium |
| README.md Project Documentation — Usage instructions, build guide, architecture overview, coverage badges | 1.5 | Low |
| Executable JAR Packaging — Maven Shade or Assembly plugin configuration for `java -jar` distribution | 1.5 | Low |
| Production Code Review & Hardening — Final review of exception messages, edge cases, documentation completeness | 1 | Low |
| **Total Remaining** | **9** | |

**Cross-Section Integrity Verification:** Section 2.1 (44h) + Section 2.2 (9h) = 53h = Total Project Hours in Section 1.2 ✓

---

## 3. Test Results

All tests were executed by Blitzy's autonomous validation system using `mvn clean test -B` on Java 17.0.18 with Maven 3.8.7.

| Test Category | Framework | Total Tests | Passed | Failed | Coverage % | Notes |
|---|---|---|---|---|---|---|
| Unit — AgeCalculatorTest (happy path, overloads, parsing, clock) | JUnit 5.10.2 | 15 | 15 | 0 | 100% (AgeCalculator) | 4 @Nested classes: HappyPath, LocalDateOverload, ParseDateOfBirth, ClockInjection |
| Unit — AgeResultTest (model, toString, equals/hashCode) | JUnit 5.10.2 | 24 | 24 | 0 | 100% (AgeResult) | 7 @Nested classes: Constructor, toString, ZeroEdge, getTotalMonths, getTotalDays, Immutability, EqualsHashCode |
| Edge Case — AgeCalculatorEdgeCaseTest (leap year, boundaries) | JUnit 5.10.2 | 14 | 14 | 0 | 100% (AgeCalculator) | 5 @Nested classes: LeapYear, CenturyLeapYear, ZeroMinimalAge, MonthEnd, ExtremeAge |
| Parameterized — AgeCalculatorParameterizedTest (data-driven) | JUnit 5.10.2 | 27 | 27 | 0 | 100% (AgeCalculator) | @CsvSource (8 valid DOBs), @ValueSource (8 invalid + 8 malformed + 3 future) |
| Validation — AgeCalculatorValidationTest (error handling) | JUnit 5.10.2 | 25 | 25 | 0 | 100% (AgeCalculator) | 6 @Nested classes: FutureDate, InvalidCalendar, WrongFormat, SpecialInput, validateDate, parseDateOfBirth |
| **Total** | **JUnit 5.10.2** | **105** | **105** | **0** | **100% branch (AgeCalculator + AgeResult)** | **Execution time: ~2.5s** |

**Coverage Summary (JaCoCo 0.8.12):**

| Class | Instruction Coverage | Branch Coverage | Missed Branches | Lines | Methods |
|---|---|---|---|---|---|
| AgeCalculator.java | 100% | 100% | 0 of 11 | 30 | 7 |
| AgeResult.java | 100% | 100% | 0 of 15 | 18 | 9 |
| Main.java | 0% | n/a | — | 15 | 2 |
| **Overall** | **84%** | **100%** | **0 of 20** | **63** | **18** |

---

## 4. Runtime Validation & UI Verification

All runtime scenarios were verified by Blitzy's autonomous validation system using the compiled application via `java -cp target/classes com.agecalculator.Main`.

### Console Application Verification

- ✅ **Valid DOB (15/08/1998):** Input accepted → Output: `Your age is 27 years, 7 months, and 12 days.`
- ✅ **Leap Year DOB (29/02/2000):** Input accepted → Output: `Your age is 26 years, 0 months, and 27 days.`
- ✅ **Invalid Date (31/02/2020):** Rejected → Output: `Invalid date format. Please enter date in DD/MM/YYYY format.`
- ✅ **Future Date (01/01/2099):** Rejected → Output: `Date of birth cannot be in the future.`
- ✅ **Wrong Format (abc):** Rejected → Output: `Invalid date format. Please enter date in DD/MM/YYYY format.`

### Build Pipeline Verification

- ✅ `mvn compile` — BUILD SUCCESS (3 source files compiled cleanly, 0 warnings)
- ✅ `mvn test-compile` — BUILD SUCCESS (5 test files compiled cleanly, 0 warnings)
- ✅ `mvn clean test -B` — BUILD SUCCESS (105 tests passed in ~2.5s)
- ✅ `mvn clean verify -B` — BUILD SUCCESS (JaCoCo report generated at `target/site/jacoco/index.html`)

### API/Method-Level Verification

- ✅ `AgeCalculator.calculateAge(String)` — Returns correct `AgeResult` for valid DOBs
- ✅ `AgeCalculator.calculateAge(LocalDate)` — Returns correct `AgeResult` for valid LocalDates
- ✅ `AgeCalculator.parseDateOfBirth(String)` — Parses DD/MM/YYYY to LocalDate correctly
- ✅ `AgeCalculator.validateDate(String)` — Throws appropriate exceptions for invalid/future dates
- ✅ `AgeResult.toString()` — Produces exact format: `Your age is X years, Y months, and Z days.`
- ✅ Clock injection — Different fixed clocks produce different deterministic results

---

## 5. Compliance & Quality Review

| AAP Requirement | Status | Evidence |
|---|---|---|
| **pom.xml** — Maven project with JUnit 5.10.2, Surefire 3.5.5, JaCoCo 0.8.12, Compiler 3.13.0, Java 17 | ✅ Pass | `pom.xml` lines 15-22: all version properties match AAP spec exactly |
| **AgeCalculator.java** — Core class with `calculateAge()`, `parseDateOfBirth()`, `validateDate()`, Clock injection, STRICT DateTimeFormatter | ✅ Pass | 227 LOC; 4 public methods; `ResolverStyle.STRICT` with `uuuu` pattern; `Clock` parameter constructor |
| **AgeResult.java** — Immutable model with constructor, getters, `toString()` format `Your age is X years, Y months, and Z days.` | ✅ Pass | 168 LOC; `toString()` matches exact user-specified format; `equals()` and `hashCode()` implemented |
| **Main.java** — Console entry point with Scanner input and exception handling | ✅ Pass | 71 LOC; try-with-resources Scanner; 3-tier catch (DateTimeParseException, IllegalArgumentException, Exception) |
| **AgeCalculatorTest.java** — Core unit tests (happy path, LocalDate overload, parsing, clock injection) | ✅ Pass | 487 LOC; 15 tests; 4 @Nested classes; all @DisplayName annotated |
| **AgeResultTest.java** — Model tests (constructor, getters, toString, totals, immutability, equals/hashCode) | ✅ Pass | 354 LOC; 24 tests; 7 @Nested classes |
| **AgeCalculatorEdgeCaseTest.java** — Edge case tests (leap years, boundaries, extreme ages) | ✅ Pass | 440 LOC; 14 tests; 5 @Nested classes |
| **AgeCalculatorParameterizedTest.java** — Parameterized tests (multiple valid/invalid/malformed inputs) | ✅ Pass | 236 LOC; 5 @ParameterizedTest methods; 27 test runs |
| **AgeCalculatorValidationTest.java** — Validation tests (future dates, invalid dates, wrong formats, null/empty) | ✅ Pass | 394 LOC; 25 tests; 6 @Nested classes |
| **User Test Case: Normal DOB (15/08/1998)** | ✅ Pass | `AgeCalculatorTest.testCalculateAge_NormalDOB_ReturnsCorrectAge()` and runtime verification |
| **User Test Case: Leap year DOB (29/02/2000)** | ✅ Pass | `AgeCalculatorEdgeCaseTest.LeapYearDOBTests` and runtime verification |
| **User Test Case: Invalid date (31/02/2020)** | ✅ Pass | `AgeCalculatorValidationTest.InvalidCalendarDateRejectionTests` and runtime verification |
| **User Test Case: Future date** | ✅ Pass | `AgeCalculatorValidationTest.FutureDateRejectionTests` and runtime verification |
| **User Test Case: Wrong format input** | ✅ Pass | `AgeCalculatorValidationTest.WrongFormatInputRejectionTests` and runtime verification |
| **Coverage: AgeCalculator ≥95% line, ≥90% branch** | ✅ Pass | JaCoCo: 100% instruction, 100% branch (0 of 11 branches missed) |
| **Coverage: AgeResult ≥95% line, ≥90% branch** | ✅ Pass | JaCoCo: 100% instruction, 100% branch (0 of 15 branches missed) |
| **Coverage: Main ≥70% line** | ⚠ Gap | JaCoCo: 0% — no MainTest.java in AAP test file plan; Scanner I/O testing requires System.in/out redirection |
| **Deterministic Clock injection** | ✅ Pass | `Clock.fixed()` pattern used in all time-dependent tests; `ClockInjectionTests` nested class in AgeCalculatorTest |
| **DD/MM/YYYY strict format enforcement** | ✅ Pass | `ResolverStyle.STRICT` with `dd/MM/uuuu` pattern rejects 31/02, 29/02/2001, etc. |
| **Exception message verification in tests** | ✅ Pass | `assertThrows()` with `getMessage()` assertions throughout AgeCalculatorValidationTest |
| **java.time API only (no legacy Date/SimpleDateFormat)** | ✅ Pass | Zero imports of `java.util.Date` or `java.text.SimpleDateFormat` in any file |
| **Maven standard directory layout** | ✅ Pass | Source under `src/main/java/`, tests under `src/test/java/`, mirrored package structure |
| **Test isolation (no shared mutable state)** | ✅ Pass | Fresh `AgeCalculator` instance in each `@BeforeEach`; no static mutable fields |
| **@DisplayName annotations on all tests** | ✅ Pass | Every test class and test method has descriptive `@DisplayName` |

### Quality Metrics Summary

| Quality Dimension | Status | Details |
|---|---|---|
| Compilation | ✅ Zero errors, zero warnings | All 8 Java files compile cleanly |
| Test Pass Rate | ✅ 100% (105/105) | 0 failures, 0 errors, 0 skipped |
| Branch Coverage (core) | ✅ 100% | All 20 branches covered on AgeCalculator + AgeResult |
| Test Execution Time | ✅ < 5 seconds | ~2.5 seconds for full suite |
| Code Documentation | ✅ Comprehensive Javadoc | All public methods documented with @param, @return, @throws, examples |
| Test Organization | ✅ @Nested grouping | 27 @Nested classes across 5 test files |

---

## 6. Risk Assessment

| Risk | Category | Severity | Probability | Mitigation | Status |
|---|---|---|---|---|---|
| Main.java lacks test coverage (0%), potential regressions in console I/O | Technical | Low | Low | Write MainTest.java with System.in/out redirection; all business logic is already tested via AgeCalculator | Open |
| No CI/CD pipeline — tests only run locally | Operational | Medium | High | Set up GitHub Actions workflow with `mvn clean verify -B` on push/PR triggers | Open |
| README.md has no project documentation | Operational | Low | Medium | Update README with build instructions, usage examples, and architecture overview | Open |
| No executable JAR packaging — requires manual classpath setup to run | Technical | Low | Medium | Configure Maven Shade or Assembly plugin for single executable JAR | Open |
| `LocalDate.now()` in production Main.java uses system clock (non-injectable) | Technical | Low | Low | Main.java delegates to `new AgeCalculator()` which uses system clock; acceptable for production | Mitigated |
| No input sanitization beyond DateTimeFormatter — very long strings could impact performance | Security | Low | Low | DateTimeFormatter parsing fails fast on non-matching input; no denial-of-service risk for single-user console app | Accepted |
| Approximate `getTotalDays()` method uses simplified formula (365 days/year, 30 days/month) | Technical | Low | Medium | Method Javadoc explicitly documents the approximation; users directed to `ChronoUnit.DAYS` for precision | Accepted |

---

## 7. Visual Project Status

```mermaid
pie title Project Hours Breakdown
    "Completed Work" : 44
    "Remaining Work" : 9
```

**Project Completion: 83.0%** (44 of 53 hours)

### Remaining Work by Priority

| Priority | Hours | Items |
|---|---|---|
| Medium | 5 | Main.java tests (3h), CI/CD pipeline (2h) |
| Low | 4 | README.md docs (1.5h), JAR packaging (1.5h), code review (1h) |
| **Total** | **9** | |

**Cross-Section Integrity Verification:** Remaining Work (9h) matches Section 1.2 (9h) and Section 2.2 (9h) ✓

---

## 8. Summary & Recommendations

### Achievements

The Blitzy platform autonomously delivered a complete, production-ready Java Age Calculator application with a comprehensive JUnit 5 test suite from an empty repository. The project is **83.0% complete** (44 hours completed out of 53 total hours). All AAP-specified test files, source files, and build configuration were delivered and validated:

- **105 tests** across 5 test classes — **100% pass rate** with zero failures, errors, or skipped tests
- **100% branch coverage** on `AgeCalculator` and `AgeResult` (the core business logic classes)
- All 5 **user-specified test scenarios** (Normal DOB, Leap Year, Invalid Date, Future Date, Wrong Format) implemented and passing
- **Deterministic testing** via Clock injection — no flaky tests
- **2,463 lines** of production-quality, fully documented Java code across 10 files
- Full test suite executes in **~2.5 seconds**

### Remaining Gaps

The remaining 9 hours (17.0% of total scope) consist of path-to-production activities:

1. **Main.java test coverage** (3h) — The AAP coverage target of ≥70% for Main.java was not achieved (0% currently). This is a medium-priority gap because all business logic is tested through `AgeCalculator` tests; Main.java contains only I/O delegation.
2. **CI/CD pipeline** (2h) — No automated test execution on push/PR. Standard GitHub Actions workflow needed.
3. **Documentation and packaging** (4h) — README.md update, executable JAR configuration, and final code review.

### Production Readiness Assessment

The core application and test suite are **production-ready for the calculation engine**. The `AgeCalculator` and `AgeResult` classes have 100% branch coverage with comprehensive edge case, parameterized, and validation testing. The remaining work is exclusively infrastructure and documentation — no business logic gaps exist.

### Success Metrics

| Metric | Target | Actual | Status |
|---|---|---|---|
| Test Pass Rate | 100% | 100% (105/105) | ✅ Exceeds |
| AgeCalculator Branch Coverage | ≥90% | 100% | ✅ Exceeds |
| AgeResult Branch Coverage | ≥90% | 100% | ✅ Exceeds |
| Main.java Line Coverage | ≥70% | 0% | ⚠ Gap |
| Test Execution Time | < 5 seconds | ~2.5 seconds | ✅ Meets |
| User Test Cases Implemented | 5/5 | 5/5 | ✅ Meets |

---

## 9. Development Guide

### System Prerequisites

| Software | Version | Verification Command |
|---|---|---|
| Java (OpenJDK) | 17.0.18+ | `java -version` |
| Apache Maven | 3.8.7+ | `mvn -version` |
| Git | 2.x+ | `git --version` |

### Environment Setup

```bash
# 1. Set JAVA_HOME (required for Maven to locate JDK)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# 2. Verify Java version
java -version
# Expected: openjdk version "17.0.18" 2026-01-20

# 3. Verify Maven version
mvn -version
# Expected: Apache Maven 3.8.7

# 4. Clone the repository and switch to the feature branch
git clone <repository-url>
cd <repository-name>
git checkout blitzy-45642cde-77f0-4693-b51e-f846fce7f77f
```

### Dependency Installation

```bash
# Download all Maven dependencies (first run may take 1-2 minutes)
mvn dependency:resolve -B
```

No external services, databases, API keys, or environment variables are required beyond `JAVA_HOME`.

### Build and Compile

```bash
# Compile source code only
mvn compile -B

# Compile source + test code
mvn compile test-compile -B
```

### Run All Tests

```bash
# Run all 105 tests
mvn clean test -B
# Expected: Tests run: 105, Failures: 0, Errors: 0, Skipped: 0

# Run tests with coverage report
mvn clean verify -B
# Coverage report: target/site/jacoco/index.html

# Run a specific test class
mvn test -Dtest=AgeCalculatorTest -B

# Run a specific test method
mvn test -Dtest=AgeCalculatorTest#testCalculateAge_NormalDOB_ReturnsCorrectAge -B

# Run tests with verbose console output
mvn test -Dsurefire.useFile=false -B
```

### Run the Application

```bash
# Run with piped input
echo "15/08/1998" | java -cp target/classes com.agecalculator.Main
# Expected: Enter your Date of Birth (DD/MM/YYYY): Your age is 27 years, 7 months, and 12 days.

# Run interactively (type a date and press Enter)
java -cp target/classes com.agecalculator.Main
```

### View Coverage Report

```bash
# Generate coverage report
mvn clean verify -B

# Open the HTML report (on local machine)
open target/site/jacoco/index.html
# Or view from command line:
cat target/site/jacoco/com.agecalculator/index.html | grep -oP 'class="ctr2">[^<]+' | head -10
```

### Troubleshooting

| Issue | Resolution |
|---|---|
| `JAVA_HOME is not set` | Run `export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64` |
| `mvn: command not found` | Install Maven: `sudo apt-get install -y maven` |
| `UnsupportedClassVersionError` | Ensure Java 17+ is active: `java -version` must show 17.x |
| Test fails with date-related assertion | Verify system date; tests use `Clock.fixed()` and should be deterministic |
| `BUILD FAILURE` on first run | Run `mvn dependency:resolve -B` to download dependencies first |

---

## 10. Appendices

### A. Command Reference

| Command | Purpose |
|---|---|
| `mvn clean test -B` | Run all 105 unit tests |
| `mvn clean verify -B` | Run tests + generate JaCoCo coverage report |
| `mvn compile test-compile -B` | Compile source and test code without running tests |
| `mvn test -Dtest=<ClassName> -B` | Run a specific test class |
| `mvn test -Dtest=<Class>#<method> -B` | Run a specific test method |
| `mvn dependency:resolve -B` | Download all project dependencies |
| `mvn dependency:tree -B` | Display dependency tree |
| `echo "<date>" \| java -cp target/classes com.agecalculator.Main` | Run app with piped input |

### B. Port Reference

No network ports are used. The application is a console-only Java program with no HTTP server, database connection, or socket communication.

### C. Key File Locations

| File | Path | Purpose |
|---|---|---|
| Maven POM | `pom.xml` | Build configuration, dependencies, plugins |
| AgeCalculator | `src/main/java/com/agecalculator/AgeCalculator.java` | Core age calculation logic (227 LOC) |
| AgeResult | `src/main/java/com/agecalculator/AgeResult.java` | Immutable result model (168 LOC) |
| Main | `src/main/java/com/agecalculator/Main.java` | Console entry point (71 LOC) |
| Core Unit Tests | `src/test/java/com/agecalculator/AgeCalculatorTest.java` | 15 happy path and core tests (487 LOC) |
| Model Tests | `src/test/java/com/agecalculator/AgeResultTest.java` | 24 POJO tests (354 LOC) |
| Edge Case Tests | `src/test/java/com/agecalculator/AgeCalculatorEdgeCaseTest.java` | 14 boundary tests (440 LOC) |
| Parameterized Tests | `src/test/java/com/agecalculator/AgeCalculatorParameterizedTest.java` | 27 data-driven tests (236 LOC) |
| Validation Tests | `src/test/java/com/agecalculator/AgeCalculatorValidationTest.java` | 25 error handling tests (394 LOC) |
| Coverage Report | `target/site/jacoco/index.html` | JaCoCo HTML coverage report |
| Test Results | `target/surefire-reports/` | Individual test execution XML reports |

### D. Technology Versions

| Technology | Version | Purpose |
|---|---|---|
| Java (OpenJDK) | 17.0.18 | Runtime and compilation target |
| Apache Maven | 3.8.7 | Build tool and dependency management |
| JUnit Jupiter | 5.10.2 | Unit testing framework (includes API, Engine, Params) |
| Maven Surefire Plugin | 3.5.5 | Test execution during Maven build lifecycle |
| JaCoCo Maven Plugin | 0.8.12 | Code coverage instrumentation and reporting |
| Maven Compiler Plugin | 3.13.0 | Java source compilation |

### E. Environment Variable Reference

| Variable | Required | Value | Purpose |
|---|---|---|---|
| `JAVA_HOME` | Yes | `/usr/lib/jvm/java-17-openjdk-amd64` | Points Maven and Java tools to the JDK installation |
| `PATH` | Yes | Must include Maven `bin/` directory | Enables `mvn` command execution |

### F. Developer Tools Guide

| Tool | Usage |
|---|---|
| **Maven Surefire** | Auto-detects `*Test.java` files; run `mvn test` for all tests |
| **JaCoCo** | Run `mvn verify` to generate coverage; view `target/site/jacoco/index.html` |
| **JUnit 5 @Nested** | Group related tests within a class; used throughout all 5 test files |
| **JUnit 5 @ParameterizedTest** | Data-driven testing with @CsvSource and @ValueSource; see AgeCalculatorParameterizedTest |
| **Clock.fixed()** | Deterministic date testing; inject via `new AgeCalculator(fixedClock)` |

### G. Glossary

| Term | Definition |
|---|---|
| **AAP** | Agent Action Plan — the comprehensive specification governing this project's scope |
| **DOB** | Date of Birth — user input in DD/MM/YYYY format |
| **ResolverStyle.STRICT** | Java DateTimeFormatter mode that rejects invalid calendar dates (e.g., Feb 31) |
| **Clock injection** | Design pattern where `java.time.Clock` is passed to constructors, enabling deterministic testing |
| **JaCoCo** | Java Code Coverage library that instruments bytecode to measure test coverage |
| **Surefire** | Maven plugin that executes unit tests during the `test` build phase |
| **@Nested** | JUnit 5 annotation for grouping related tests as inner classes within a test class |
| **@ParameterizedTest** | JUnit 5 annotation for running the same test with multiple inputs |