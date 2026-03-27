# Technical Specification

# 0. Agent Action Plan

## 0.1 Intent Clarification


### 0.1.1 Core Testing Objective

Based on the provided requirements, the Blitzy platform understands that the testing objective is to **create a comprehensive unit test suite for a greenfield Java Age Calculator application** that computes a user's exact age (in years, months, and days) from a Date of Birth (DOB) entered in `DD/MM/YYYY` format. The application leverages `java.time.LocalDate`, `java.time.Period`, and `java.time.format.DateTimeFormatter` from the Java 17 standard library.

**Request Categorization:** Add new tests (greenfield project — all tests are net-new)

The user's testing requirements, restated with enhanced clarity:

- **Valid Age Calculation Tests** — Verify that given a well-formed DOB string (e.g., `15/08/1998`), the system correctly calculates the exact number of years, months, and days against the current system date and produces output in the format `Your age is X years, Y months, and Z days.`
- **Leap Year Handling Tests** — Confirm that dates such as `29/02/2000` (a valid leap year date) are accepted and calculated correctly, while dates like `29/02/2001` (an invalid leap year date) are properly rejected
- **Invalid Date Rejection Tests** — Ensure that impossible calendar dates (e.g., `31/02/2020`, `32/01/2000`, `00/13/1990`) trigger meaningful error messages rather than silent failures or incorrect calculations
- **Future Date Rejection Tests** — Validate that any DOB occurring after the current system date is rejected with a clear error message
- **Malformed Input Tests** — Confirm that inputs not conforming to the `DD/MM/YYYY` format (e.g., `1998-08-15`, `15/8/98`, `abc`, empty strings, null values) produce descriptive error messages

**Implicit Testing Needs Surfaced:**

- **Boundary condition testing** — DOB equal to today's date (age of zero), DOB exactly one day ago, DOB on month boundaries (e.g., born on the 31st when the current month has 30 days)
- **Leap year edge cases** — Born on February 29th and calculating age when the current year is not a leap year
- **Century leap year logic** — Years divisible by 100 but not 400 (e.g., 1900 was not a leap year, but 2000 was)
- **OOP structure testing** — Verification that the `AgeResult` model object correctly encapsulates years, months, and days fields
- **Output format verification** — The exact string output format must match the user specification: `Your age is X years, Y months, and Z days.`
- **Exception handling paths** — Every `try-catch` block in the application must have corresponding test coverage for both the success and exception paths

### 0.1.2 Special Instructions and Constraints

The user has not specified explicit constraints on test modifications. However, the following directives are inferred from the requirements:

- **Follow standard Maven project conventions** — Tests reside under `src/test/java/` mirroring the `src/main/java/` package structure
- **Use JUnit 5 (Jupiter) annotations** — Standard `@Test`, `@ParameterizedTest`, `@DisplayName`, `@Nested` for organization
- **OOP principles in test design** — Test classes should be well-organized, following the same clean coding standards as the production code
- **Exception handling coverage** — Every `try-catch` block in source code must have a corresponding test exercising the exception path
- **No external mocking required** — The Age Calculator has no external service dependencies; all logic is testable via pure unit tests using `java.time` APIs

**User-Specified Test Cases (preserved exactly):**

- User Example: `✅ Normal DOB (e.g., 15/08/1998)`
- User Example: `✅ Leap year DOB (29/02/2000)`
- User Example: `❌ Invalid date (31/02/2020)`
- User Example: `❌ Future date`
- User Example: `❌ Wrong format input`

**User-Specified Sample Input/Output (preserved exactly):**

- User Example Input: `Enter your Date of Birth (DD/MM/YYYY): 15/08/1998`
- User Example Output: `Your age is 27 years, 6 months, and 15 days.`

### 0.1.3 Technical Interpretation

These testing requirements translate to the following technical test implementation strategy:

- To **test normal DOB calculation**, we will create `AgeCalculatorTest.java` with `@Test` methods that invoke the core calculation method with known DOB strings and assert the resulting years, months, and days against values computed from `java.time.Period.between()`
- To **test leap year handling**, we will create `AgeCalculatorEdgeCaseTest.java` with test methods specifically targeting February 29th DOBs across leap years (2000, 2004, 2024) and non-leap years
- To **test invalid date rejection**, we will create `AgeCalculatorValidationTest.java` with tests that supply impossible dates and assert that `DateTimeParseException` or a custom validation error is thrown with a meaningful message
- To **test future date rejection**, we will add validation tests that supply tomorrow's date and dates far in the future, asserting appropriate exception types and messages
- To **test malformed input**, we will create parameterized tests in `AgeCalculatorParameterizedTest.java` that iterate over multiple invalid format strings and assert consistent error handling
- To **test the result model**, we will create `AgeResultTest.java` to verify the `AgeResult` data class correctly stores and exposes years, months, and days, and produces the expected `toString()` output

### 0.1.4 Coverage Requirements Interpretation

**Explicit coverage targets mentioned by user:** None specified explicitly.

**Implicit coverage expectations based on analysis:**

- Industry standard for new Java projects with JUnit 5 targeting utility/calculation classes: **≥ 90% line coverage, ≥ 85% branch coverage**
- The `AgeCalculator` class is purely deterministic with no external dependencies, making 95%+ coverage both achievable and expected
- Every code path through `try-catch` blocks, validation guards, and the calculation logic must be exercised
- To achieve comprehensive testing, coverage should include:
  - All public methods in `AgeCalculator`, `AgeResult`, and `Main`
  - All conditional branches (valid/invalid input, future/past dates, leap year/non-leap year)
  - All exception handling paths (`DateTimeParseException`, `IllegalArgumentException`)
  - Edge cases at date boundaries (month-end, year-end, leap day)


## 0.2 Test Discovery and Analysis


### 0.2.1 Existing Test Infrastructure Assessment

Repository analysis was conducted exhaustively. The repository is a **greenfield project** containing only a single file (`README.md` with content `# 12feb_2`). No source code, test files, build configuration, or dependency manifests exist.

**Discovery Findings:**

- **Test files found:** None — no files matching `*test*`, `*spec*`, `test_*`, `*_test.*`, `*_spec.*`, `*Test.java`, `*IT.java`
- **Testing framework detected:** None — no `pom.xml`, `build.gradle`, `package.json`, or any dependency manifest present
- **Test configuration files found:** None — no `surefire-report.xml`, `jacoco.exec`, `maven-surefire-plugin` configuration
- **Test suites:** None exist
- **Test data fixtures:** None exist
- **Mock/stub libraries:** None detected
- **Coverage tools:** None configured

**Repository analysis reveals:** An empty repository with no testing infrastructure. All test files, test configuration, build tooling, and test dependencies must be created from scratch as part of this implementation.

**Infrastructure to be established:**

- **Testing framework:** JUnit Jupiter 5.10.2 (JUnit 5), fully compatible with Java 17
- **Test runner configuration:** Maven Surefire Plugin 3.5.5, configured in `pom.xml`
- **Coverage tool:** JaCoCo Maven Plugin 0.8.12, with `prepare-agent` and `report` goals
- **Mock/stub libraries:** Not required — the Age Calculator has no external dependencies requiring mocking
- **Test data fixtures:** Embedded within test classes as constants and parameterized test sources; no external fixture files needed for this project's scope

### 0.2.2 Web Search Research Conducted

The following research was completed to validate framework compatibility and inform implementation decisions:

- **JUnit 5 compatibility with Java 17:** JUnit 5.10.2 is fully compatible with Java 17. JUnit 6.0 (released September 30, 2025) raised the minimum Java requirement to 17, confirming that Java 17 is the current long-term baseline for the JUnit ecosystem. JUnit 5.10.2 supports Java 8+ and is the recommended stable choice for new projects.
- **Maven Surefire Plugin version:** Maven Surefire Plugin 3.5.5 is the current latest stable release. It auto-detects JUnit Jupiter when `junit-jupiter-engine` is on the test classpath, requiring no additional provider configuration.
- **JaCoCo version for Java 17:** JaCoCo 0.8.12 officially supports Java 17 and 18 class files. Earlier versions (0.8.6 and below) are incompatible with Java 17 due to ASM library limitations causing `UnsupportedClassVersionError` on class file major version 61.
- **Maven project structure conventions:** Standard Maven directory layout places tests under `src/test/java/` mirroring the main source package structure. Unit test classes follow the naming convention `*Test.java`, and integration tests use `*IT.java`, which Maven Surefire and Failsafe plugins auto-detect respectively.
- **JUnit 5 parameterized test best practices:** `@ParameterizedTest` with `@ValueSource`, `@CsvSource`, and `@MethodSource` annotations provide data-driven testing for multiple input/output scenarios, ideal for validating various DOB format inputs.
- **`java.time` testing patterns:** Testing with `java.time.LocalDate` is straightforward since it is immutable and deterministic. Tests can use `LocalDate.of()` for fixed dates and `LocalDate.now()` for relative assertions. For deterministic testing, using `java.time.Clock` injection is a recommended pattern to control the "current date" in tests.


## 0.3 Testing Scope Analysis


### 0.3.1 Test Target Identification

**Primary code to be tested:**

- **Class:** `AgeCalculator` at `src/main/java/com/agecalculator/AgeCalculator.java` — requires unit tests, parameterized tests, edge case tests, and validation tests
  - `calculateAge(String dobString)` — Core method accepting `DD/MM/YYYY` string, returns `AgeResult`
  - `calculateAge(LocalDate dob)` — Overloaded method accepting `LocalDate`, returns `AgeResult`
  - `validateDate(String dobString)` — Input validation method, throws exceptions for invalid input
  - `parseDateOfBirth(String dobString)` — Parsing method converting string to `LocalDate`
- **Class:** `AgeResult` at `src/main/java/com/agecalculator/AgeResult.java` — requires model/unit tests
  - Constructor, getter methods for `years`, `months`, `days`
  - `toString()` — Must produce format `Your age is X years, Y months, and Z days.`
  - `getTotalMonths()` — Optional enhancement: total age in months
  - `getTotalDays()` — Optional enhancement: total age in days
- **Class:** `Main` at `src/main/java/com/agecalculator/Main.java` — requires basic integration tests
  - `main(String[] args)` — Entry point with `Scanner`-based input
  - Console output verification

**Existing test file mapping:**

| Source File | Existing Test File | Test Categories Present |
|---|---|---|
| `src/main/java/com/agecalculator/AgeCalculator.java` | None — to be created | None |
| `src/main/java/com/agecalculator/AgeResult.java` | None — to be created | None |
| `src/main/java/com/agecalculator/Main.java` | None — to be created | None |

**Dependencies requiring mocking:**

- **External services to mock:** None — the application is self-contained with no network, database, or API calls
- **Database interactions to stub:** None — no persistence layer exists
- **File system operations to virtualize:** None — the application operates entirely in-memory
- **System clock consideration:** For deterministic test results, `java.time.Clock` can be injected into `AgeCalculator` to control the "current date" during testing, avoiding flaky tests that depend on the real system clock

### 0.3.2 Version Compatibility Research

Based on the project's Java 17.0.18 runtime, the recommended testing stack is as follows:

- **Testing framework:** JUnit Jupiter 5.10.2
  - *Rationale:* Stable release with full Java 17 support. JUnit 5.10.x is the most widely deployed JUnit 5 series with extensive community support and documentation. JUnit 6.0.x (released September 2025) raises the baseline to Java 17 but is too new for production adoption in a greenfield project.
- **Assertion library:** JUnit Jupiter Assertions (built-in)
  - *Rationale:* `org.junit.jupiter.api.Assertions` provides `assertEquals`, `assertThrows`, `assertAll`, `assertNotNull`, and other assertions sufficient for this project's scope. No external assertion library (e.g., AssertJ, Hamcrest) is required.
- **Mocking library:** Not required
  - *Rationale:* The Age Calculator has zero external dependencies. All logic is deterministic and testable through direct method invocation. No mocking framework is needed.
- **Coverage tool:** JaCoCo Maven Plugin 0.8.12
  - *Rationale:* Version 0.8.12 officially supports Java 17 and 18 class files (ASM 9.7). Earlier versions (pre-0.8.7) fail with `UnsupportedClassVersionError` on Java 17.
- **Build plugin:** Maven Surefire Plugin 3.5.5
  - *Rationale:* Latest stable release. Auto-detects JUnit Jupiter engine on the classpath. Requires no additional provider configuration.
- **Compiler plugin:** Maven Compiler Plugin 3.13.0
  - *Rationale:* Latest stable release with Java 17 source/target support.

**Version conflicts to resolve:** None identified. All selected versions are mutually compatible with Java 17.0.18 and Maven 3.8.7.


## 0.4 Test Implementation Design


### 0.4.1 Test Strategy Selection

**Test types to implement:**

- **Unit tests:** Focus on isolated testing of `AgeCalculator` calculation logic, `AgeResult` model behavior, and input validation/parsing methods. Each public method is tested independently with known inputs and expected outputs.
- **Parameterized tests:** Cover data-driven scenarios for multiple valid DOBs, multiple invalid date strings, and multiple malformed format inputs. Uses JUnit 5 `@ParameterizedTest` with `@CsvSource`, `@ValueSource`, and `@MethodSource` annotations.
- **Edge case tests:** Address boundary conditions including DOB equal to today, DOB one day ago, leap year boundaries (Feb 29), month-end boundaries (born on 31st when current month has 30 days), century leap year logic (1900 vs 2000), and very old dates.
- **Error handling tests:** Verify that `DateTimeParseException`, `IllegalArgumentException`, and custom validation errors are thrown with correct messages for invalid inputs, future dates, null values, and empty strings.

### 0.4.2 Test Case Blueprint

```
Component: AgeCalculator
Test Categories:
- Happy path: Normal DOB (15/08/1998), Recent DOB (01/01/2020), Same month DOB, Same year DOB
- Edge cases: DOB is today (zero age), DOB is yesterday (0y 0m 1d), Born on Feb 29 leap year,
              Born on Dec 31 (year boundary), Born on Jan 1, Born on month-end (31st),
              Very old DOB (01/01/1900), Century boundary (29/02/2000 valid, 29/02/1900 invalid)
- Error cases: Future date (tomorrow), Far future date (01/01/2099), Invalid day (31/02/2020),
               Invalid month (15/13/2020), Day zero (00/05/2020), Month zero (15/00/2020),
               Negative values, Wrong format (YYYY-MM-DD, MM/DD/YYYY, DD-MM-YYYY),
               Non-date string ("abc"), Empty string (""), Null input, Partial date ("15/08")
- Clock injection: Verify deterministic results using fixed Clock for "current date"
```

```
Component: AgeResult
Test Categories:
- Happy path: Create with valid years/months/days, Verify getter methods,
              Verify toString() produces "Your age is X years, Y months, and Z days."
- Edge cases: Zero years (infant), Zero months, Zero days, All zeros (born today),
              Large values (125 years old), Single unit (1 year 0 months 0 days)
- Error cases: Negative values (if constructor permits), Null handling
- Optional methods: getTotalMonths() calculation, getTotalDays() estimation
```

```
Component: Main (Integration-level)
Test Categories:
- Happy path: Verify console output for valid DOB input
- Error cases: Verify error message output for invalid inputs
```

### 0.4.3 Existing Test Extension Strategy

Since this is a greenfield project, there are no existing tests to extend, refactor, or fix. All test files listed below are net-new creations:

- **Create** `AgeCalculatorTest.java` — Core unit tests for the primary calculation logic
- **Create** `AgeResultTest.java` — Model/POJO tests for the result data class
- **Create** `AgeCalculatorEdgeCaseTest.java` — Dedicated edge case and boundary condition tests
- **Create** `AgeCalculatorParameterizedTest.java` — Data-driven parameterized tests for multiple inputs
- **Create** `AgeCalculatorValidationTest.java` — Input validation and error handling tests

### 0.4.4 Test Data and Fixtures Design

**Required test data structures:**

- **Valid DOB constants:** A set of known-good DOB strings with pre-computed expected age results relative to a fixed reference date
- **Invalid date constants:** A collection of impossible calendar dates (e.g., `31/02/2020`, `29/02/2001`, `00/13/1990`)
- **Malformed format constants:** A collection of strings that do not match `DD/MM/YYYY` format (e.g., `1998-08-15`, `15/8/98`, `abc`, `""`, `null`)
- **Leap year test dates:** Curated set of Feb 29 dates across leap years (2000, 2004, 2024) and non-leap years (1900, 2001, 2023)

**Fixture organization strategy:**

- Test data is embedded directly within test classes as `private static final` constants and `@MethodSource` provider methods
- No external fixture files (JSON, CSV, XML) are needed given the simplicity of the data
- A shared `TestConstants` interface or class may be used if constants are reused across multiple test classes

**Mock object specifications:**

- `java.time.Clock` — A fixed `Clock` instance (`Clock.fixed(Instant, ZoneId)`) injected into `AgeCalculator` to make "current date" deterministic in tests. This eliminates flaky tests caused by the test running at midnight or across date boundaries.

**Test database/state management approach:**

- Not applicable — the application has no persistence layer. All test state is managed through method-level setup and teardown using JUnit 5 `@BeforeEach` and `@AfterEach` annotations.


## 0.5 Test File Transformation Mapping


### 0.5.1 File-by-File Test Plan

Every test file to be created for this greenfield project is listed below. Since no test files or source files currently exist, all entries use the **CREATE** transformation mode.

| Target Test File | Transformation | Source File/Test | Purpose/Changes |
|---|---|---|---|
| `src/test/java/com/agecalculator/AgeCalculatorTest.java` | CREATE | `src/main/java/com/agecalculator/AgeCalculator.java` | Core unit tests for `calculateAge()`, `parseDateOfBirth()`, and `validateDate()` methods covering happy path scenarios |
| `src/test/java/com/agecalculator/AgeResultTest.java` | CREATE | `src/main/java/com/agecalculator/AgeResult.java` | Unit tests for `AgeResult` model: constructor, getters, `toString()` output format, optional total calculations |
| `src/test/java/com/agecalculator/AgeCalculatorEdgeCaseTest.java` | CREATE | `src/main/java/com/agecalculator/AgeCalculator.java` | Edge case tests: leap years (Feb 29), DOB today, DOB yesterday, month-end boundaries, century leap year rules, very old dates |
| `src/test/java/com/agecalculator/AgeCalculatorParameterizedTest.java` | CREATE | `src/main/java/com/agecalculator/AgeCalculator.java` | Data-driven parameterized tests using `@ParameterizedTest` with `@CsvSource` for multiple valid and invalid DOB inputs |
| `src/test/java/com/agecalculator/AgeCalculatorValidationTest.java` | CREATE | `src/main/java/com/agecalculator/AgeCalculator.java` | Input validation tests: future dates, invalid dates (31/02), wrong format (YYYY-MM-DD), null/empty input, non-date strings |
| `pom.xml` | CREATE | N/A | Maven project configuration with JUnit 5, Surefire, JaCoCo, and compiler plugin configuration |

### 0.5.2 New Test Files Detail

- **`src/test/java/com/agecalculator/AgeCalculatorTest.java`** — Core unit test coverage
  - Test categories: Happy path age calculations, method return type verification
  - Mock dependencies: `java.time.Clock` (fixed clock for deterministic date control)
  - Assertions focus: `assertEquals` on years, months, days; `assertNotNull` on result objects
  - Key test methods:
    - `testCalculateAge_NormalDOB_ReturnsCorrectAge()` — Validates the user example: DOB `15/08/1998`
    - `testCalculateAge_RecentDOB_ReturnsCorrectAge()` — DOB within last few years
    - `testCalculateAge_SameMonthDOB_ReturnsCorrectAge()` — DOB in the current month
    - `testCalculateAge_WithLocalDate_ReturnsCorrectAge()` — Tests the `LocalDate` overload directly
    - `testParseDateOfBirth_ValidFormat_ReturnsLocalDate()` — Verifies string-to-LocalDate parsing
    - `testCalculateAge_WithFixedClock_ReturnsDeterministicResult()` — Uses `Clock.fixed()` for reproducibility

- **`src/test/java/com/agecalculator/AgeResultTest.java`** — Model/POJO test coverage
  - Test categories: Constructor validation, getter verification, `toString()` format
  - Mock dependencies: None
  - Assertions focus: `assertEquals` on field values, `assertEquals` on formatted string output
  - Key test methods:
    - `testAgeResult_ConstructorAndGetters()` — Verifies years, months, days are stored correctly
    - `testAgeResult_ToStringFormat()` — Asserts output matches `Your age is X years, Y months, and Z days.`
    - `testAgeResult_ZeroAge()` — Born today: `0 years, 0 months, and 0 days`
    - `testAgeResult_SingleUnit()` — Exactly 1 year, 0 months, 0 days
    - `testAgeResult_GetTotalMonths()` — Optional: verifies total months calculation
    - `testAgeResult_GetTotalDays()` — Optional: verifies total days estimation

- **`src/test/java/com/agecalculator/AgeCalculatorEdgeCaseTest.java`** — Edge case and boundary coverage
  - Test categories: Leap year dates, date boundaries, extreme ages
  - Mock dependencies: `java.time.Clock` (fixed clock)
  - Assertions focus: `assertEquals` on calculated age components, `assertDoesNotThrow` for valid edge dates
  - Key test methods:
    - `testCalculateAge_LeapYearDOB_Feb29_ReturnsCorrectAge()` — DOB `29/02/2000`
    - `testCalculateAge_LeapYearDOB_CurrentYearNotLeap()` — Feb 29 DOB tested against non-leap current year
    - `testCalculateAge_CenturyLeapYear_2000IsLeap()` — Validates year 2000 is a valid leap year
    - `testCalculateAge_CenturyNonLeapYear_1900IsNotLeap()` — Validates 29/02/1900 is invalid
    - `testCalculateAge_DOBIsToday_ReturnsZeroAge()` — Age is exactly zero
    - `testCalculateAge_DOBIsYesterday_ReturnsOneDayAge()` — Exactly 0y, 0m, 1d
    - `testCalculateAge_BornOnDec31_CrossesYearBoundary()` — Year boundary crossing
    - `testCalculateAge_BornOn31st_CurrentMonthHas30Days()` — Month-end day mismatch
    - `testCalculateAge_VeryOldDOB_1900()` — Extreme age scenario

- **`src/test/java/com/agecalculator/AgeCalculatorParameterizedTest.java`** — Parameterized data-driven coverage
  - Test categories: Multiple valid DOBs, multiple invalid DOBs, multiple malformed formats
  - Mock dependencies: `java.time.Clock` (fixed clock)
  - Assertions focus: `assertEquals` for valid cases, `assertThrows` for invalid cases
  - Key test methods:
    - `testCalculateAge_MultipleValidDOBs(String dob, int years, int months, int days)` — `@CsvSource` with 8+ valid DOB/expected-result pairs
    - `testCalculateAge_MultipleInvalidDates(String invalidDate)` — `@ValueSource` with 6+ impossible calendar dates
    - `testCalculateAge_MultipleMalformedFormats(String malformed)` — `@ValueSource` with 8+ malformed input strings

- **`src/test/java/com/agecalculator/AgeCalculatorValidationTest.java`** — Validation and error handling coverage
  - Test categories: Future date rejection, invalid date rejection, format errors, null/empty handling
  - Mock dependencies: None
  - Assertions focus: `assertThrows` with exception type and message verification
  - Key test methods:
    - `testValidateDate_FutureDate_ThrowsIllegalArgumentException()` — Tomorrow's date
    - `testValidateDate_FarFutureDate_ThrowsIllegalArgumentException()` — Year 2099
    - `testValidateDate_InvalidDate_31Feb_ThrowsDateTimeParseException()` — `31/02/2020`
    - `testValidateDate_InvalidDate_29FebNonLeap_ThrowsDateTimeParseException()` — `29/02/2001`
    - `testValidateDate_WrongFormat_ISO_ThrowsDateTimeParseException()` — `1998-08-15`
    - `testValidateDate_WrongFormat_USStyle_ThrowsDateTimeParseException()` — `08/15/1998` (ambiguous but wrong)
    - `testValidateDate_WrongFormat_ShortYear_ThrowsDateTimeParseException()` — `15/08/98`
    - `testValidateDate_NonDateString_ThrowsDateTimeParseException()` — `"abc"`
    - `testValidateDate_EmptyString_ThrowsException()` — `""`
    - `testValidateDate_NullInput_ThrowsNullPointerException()` — `null`
    - `testValidateDate_PartialDate_ThrowsDateTimeParseException()` — `"15/08"`

### 0.5.3 Test Configuration Updates

- **`pom.xml`** — Create the entire Maven project configuration from scratch:
  - Set `groupId` to `com.agecalculator`, `artifactId` to `age-calculator`, `version` to `1.0.0`
  - Configure `maven-compiler-plugin` 3.13.0 with `source` and `target` set to `17`
  - Configure `maven-surefire-plugin` 3.5.5 for JUnit 5 test execution
  - Configure `jacoco-maven-plugin` 0.8.12 with `prepare-agent` and `report` goals
  - Add `junit-jupiter` 5.10.2 as test dependency
  - Set `<maven.compiler.source>17</maven.compiler.source>` and `<maven.compiler.target>17</maven.compiler.target>` in properties

### 0.5.4 Cross-File Test Dependencies

- **Shared fixtures:** All test data constants are embedded within individual test classes. If reuse is needed, a `TestConstants.java` utility class under `src/test/java/com/agecalculator/` can consolidate shared DOB strings and expected results.
- **Mock objects:** The `Clock.fixed()` pattern is used directly within test methods or `@BeforeEach` setup. No shared mock factory class is required.
- **Test utilities:** No cross-cutting test helper functions are anticipated. Each test class is self-contained.
- **Import updates required:** All test files import from `org.junit.jupiter.api.*` and `java.time.*`. No cross-file import dependencies exist between test classes.


## 0.6 Dependency Inventory


### 0.6.1 Testing Dependencies

All testing packages required for this project are listed below. Since this is a greenfield project, every dependency is net-new.

| Registry | Package Name | Version | Purpose |
|---|---|---|---|
| Maven Central | `org.junit.jupiter:junit-jupiter` | 5.10.2 | JUnit 5 aggregate dependency (includes junit-jupiter-api, junit-jupiter-engine, and junit-jupiter-params for parameterized tests) |
| Maven Central | `org.junit.jupiter:junit-jupiter-api` | 5.10.2 | JUnit 5 test API with annotations (`@Test`, `@ParameterizedTest`, `@DisplayName`, `@Nested`, `@BeforeEach`) and assertions (`assertEquals`, `assertThrows`, `assertAll`) — transitive via `junit-jupiter` |
| Maven Central | `org.junit.jupiter:junit-jupiter-engine` | 5.10.2 | JUnit 5 test execution engine for Maven Surefire integration — transitive via `junit-jupiter` |
| Maven Central | `org.junit.jupiter:junit-jupiter-params` | 5.10.2 | JUnit 5 parameterized test support (`@CsvSource`, `@ValueSource`, `@MethodSource`) — transitive via `junit-jupiter` |
| Maven Central | `org.apache.maven.plugins:maven-surefire-plugin` | 3.5.5 | Maven plugin for executing unit tests during the `test` phase |
| Maven Central | `org.jacoco:jacoco-maven-plugin` | 0.8.12 | Maven plugin for code coverage measurement and HTML/XML report generation |
| Maven Central | `org.apache.maven.plugins:maven-compiler-plugin` | 3.13.0 | Maven plugin for compiling Java 17 source and test code |

**Version Verification Notes:**

- `junit-jupiter:5.10.2` — Released October 2023. Supports Java 8+. Fully compatible with Java 17.0.18. This is a well-tested stable release in the 5.10.x series.
- `maven-surefire-plugin:3.5.5` — Confirmed latest stable release from the official Apache Maven documentation. Supports JUnit 5 auto-detection.
- `jacoco-maven-plugin:0.8.12` — Supports Java 17 and 18 class files natively. Uses ASM 9.7. Requires Java 8+ for the Maven runtime.
- `maven-compiler-plugin:3.13.0` — Latest stable release supporting `source` and `target` configuration for Java 17.

### 0.6.2 Import Updates

Since all test files are net-new, there are no import migration requirements. The standard import patterns for all test files are:

- **Core JUnit 5 imports** (used by all test classes):
  - `org.junit.jupiter.api.Test`
  - `org.junit.jupiter.api.DisplayName`
  - `org.junit.jupiter.api.BeforeEach`
  - `org.junit.jupiter.api.Assertions.*` (static import)

- **Parameterized test imports** (used by `AgeCalculatorParameterizedTest.java`):
  - `org.junit.jupiter.params.ParameterizedTest`
  - `org.junit.jupiter.params.provider.CsvSource`
  - `org.junit.jupiter.params.provider.ValueSource`
  - `org.junit.jupiter.params.provider.MethodSource`

- **Java time imports** (used by all AgeCalculator test classes):
  - `java.time.LocalDate`
  - `java.time.Period`
  - `java.time.Clock`
  - `java.time.ZoneId`
  - `java.time.Instant`
  - `java.time.format.DateTimeParseException`

- **Application imports** (used by all test classes):
  - `com.agecalculator.AgeCalculator`
  - `com.agecalculator.AgeResult`


## 0.7 Coverage and Quality Targets


### 0.7.1 Coverage Metrics

- **Current coverage:** 0% — the repository contains no source code or tests
- **Target coverage:** ≥ 90% line coverage, ≥ 85% branch coverage based on industry best practices for utility/calculation classes in Java
- **Coverage gaps to address (by class):**

| Component | Current Coverage | Target Coverage | Focus Areas |
|---|---|---|---|
| `AgeCalculator.java` | 0% | ≥ 95% line, ≥ 90% branch | All public methods, all conditional branches (valid/invalid input, future/past dates, leap year/non-leap year), exception handling paths |
| `AgeResult.java` | 0% | ≥ 95% line, ≥ 90% branch | Constructor, all getters, `toString()`, optional total calculation methods |
| `Main.java` | 0% | ≥ 70% line | Console input/output flow; lower target due to `Scanner`-based I/O being harder to unit test |

- **Per-method coverage targets:**
  - `calculateAge(String)` — 100% line, 100% branch (core business logic)
  - `calculateAge(LocalDate)` — 100% line, 100% branch (core business logic)
  - `validateDate(String)` — 100% line, 100% branch (all validation paths exercised)
  - `parseDateOfBirth(String)` — 100% line, 100% branch (valid + invalid format paths)
  - `AgeResult.toString()` — 100% line (output format verification)
  - `AgeResult` getters — 100% line (trivial but necessary for completeness)

### 0.7.2 Test Quality Criteria

- **Assertion density expectations:** Each test method must contain at least one meaningful assertion. Complex test methods (e.g., full age calculation) should use `assertAll()` to group related assertions (years, months, days) for comprehensive failure reporting.
- **Test isolation requirements:** Every test method must be independently executable. No test may depend on the execution order or side effects of another test. The `AgeCalculator` under test must be instantiated fresh in each test method or via `@BeforeEach`.
- **Performance constraints:** The entire test suite must complete execution in under 5 seconds. Individual test methods should execute in under 100 milliseconds. No I/O-bound or network-dependent operations exist, so this target is easily achievable.
- **Determinism requirements:** All tests must produce identical results regardless of the date on which they are run. This is achieved by injecting a fixed `Clock` instance into `AgeCalculator` rather than relying on `LocalDate.now()`.
- **Maintainability standards:**
  - Every test class uses `@DisplayName` annotations for human-readable test names
  - Test method names follow the pattern `testMethodName_Scenario_ExpectedBehavior()`
  - Related tests are grouped using JUnit 5 `@Nested` classes where appropriate
  - Test data is declared as `private static final` constants at the top of test classes
  - No magic numbers — all expected values are documented with inline comments or named constants
- **Repository test pattern compliance:** Since this is a greenfield project, these tests establish the repository's testing conventions. All subsequent tests added to this project should follow the patterns established in these initial test files.


## 0.8 Scope Boundaries


### 0.8.1 Exhaustively In Scope

**New test files:**
- `src/test/java/com/agecalculator/AgeCalculatorTest.java` — Core unit tests for age calculation logic
- `src/test/java/com/agecalculator/AgeResultTest.java` — Model class unit tests
- `src/test/java/com/agecalculator/AgeCalculatorEdgeCaseTest.java` — Boundary condition and leap year tests
- `src/test/java/com/agecalculator/AgeCalculatorParameterizedTest.java` — Data-driven parameterized tests
- `src/test/java/com/agecalculator/AgeCalculatorValidationTest.java` — Input validation and error handling tests

**Source files (required to be created for tests to compile and execute):**
- `src/main/java/com/agecalculator/AgeCalculator.java` — Core calculation class using `java.time.LocalDate`, `java.time.Period`, `java.time.format.DateTimeFormatter`
- `src/main/java/com/agecalculator/AgeResult.java` — Result model class encapsulating years, months, days with `toString()` formatting
- `src/main/java/com/agecalculator/Main.java` — Entry point with `Scanner`-based console I/O

**Test configuration:**
- `pom.xml` — Full Maven project configuration including:
  - Java 17 compiler settings
  - JUnit Jupiter 5.10.2 test dependency
  - Maven Surefire Plugin 3.5.5 configuration
  - JaCoCo Maven Plugin 0.8.12 with `prepare-agent` and `report` goals
  - Project metadata (`groupId`, `artifactId`, `version`)

**Test utilities and helpers:**
- `java.time.Clock.fixed()` usage within test `@BeforeEach` methods for deterministic date control
- Inline test data constants within each test class
- `@MethodSource` provider methods for parameterized test data

### 0.8.2 Explicitly Out of Scope

- **GUI implementation** — The optional Java Swing/JavaFX GUI enhancement mentioned by the user is out of scope for this testing exercise. Tests target only the core console-based calculation logic.
- **Next birthday countdown feature** — The optional "countdown to next birthday" enhancement is out of scope. No test files will be created for this feature.
- **Total age in months/days feature** — While `AgeResult` may include `getTotalMonths()` and `getTotalDays()` as optional convenience methods, exhaustive testing of these methods is secondary to the core age calculation tests.
- **Web or API layer** — No REST API, web controller, or HTTP endpoint tests. The application is a console-only Java program.
- **Database or persistence tests** — The application has no database layer. No repository, DAO, or ORM tests.
- **Performance/load testing** — No JMH benchmarks, stress tests, or concurrent execution tests. The application processes a single DOB input.
- **Security testing** — No input sanitization, injection prevention, or authentication tests. The application accepts only a simple date string.
- **CI/CD pipeline configuration** — No GitHub Actions, Jenkins, or other CI/CD workflow files. Testing is executed locally via Maven.
- **IDE-specific configuration** — No `.idea/`, `.vscode/`, or Eclipse `.project` files.
- **Source code refactoring** — Source files are created minimally to support test compilation. No refactoring of production code beyond what is necessary for testability (e.g., `Clock` injection).


## 0.9 Execution Parameters


### 0.9.1 Testing-Specific Instructions

- **Test execution command:**
  ```
  mvn clean test
  ```

- **Coverage measurement command:**
  ```
  mvn clean verify jacoco:report
  ```
  Coverage report is generated at `target/site/jacoco/index.html`

- **Single test class execution pattern:**
  ```
  mvn test -Dtest=AgeCalculatorTest
  ```

- **Single test method execution pattern:**
  ```
  mvn test -Dtest=AgeCalculatorTest#testCalculateAge_NormalDOB_ReturnsCorrectAge
  ```

- **Run all tests with verbose output:**
  ```
  mvn test -Dsurefire.useFile=false
  ```

- **Debug mode execution:**
  ```
  mvn test -Dmaven.surefire.debug
  ```
  This suspends the JVM on port 5005 waiting for a debugger connection.

- **Compile without running tests (verify build):**
  ```
  mvn compile test-compile
  ```

- **Specific test patterns followed in the repository:**
  - Test classes are named `*Test.java` (detected by Maven Surefire Plugin by default)
  - Test classes mirror the package structure of the source classes under `src/test/java/`
  - Test method names follow `testMethodName_Scenario_ExpectedBehavior()` convention
  - `@DisplayName` annotations provide human-readable descriptions for test reports
  - `@Nested` inner classes may group related test scenarios within a single test file

- **Excluded test categories:** None — all test files are in scope for every test execution

- **Environment setup requirements for tests:**
  - Java 17 (OpenJDK 17.0.18) must be installed and `JAVA_HOME` set to `/usr/lib/jvm/java-17-openjdk-amd64`
  - Maven 3.8.7+ must be installed and available on `PATH`
  - No additional environment variables required
  - No external services, databases, or network connectivity required
  - Tests are fully self-contained and execute entirely in-process


## 0.10 Special Instructions for Testing


### 0.10.1 Testing-Specific Requirements

The following directives govern the implementation of all test files in this project:

- **Greenfield establishment principle:** Since this is an empty repository, the test files created here establish the project's testing conventions. All patterns, naming conventions, and organizational structures defined in these initial test files serve as the template for any future test additions.

- **OOP compliance in tests:** Test classes must follow Object-Oriented Programming principles consistent with the production code. Each test class should have a clear, single responsibility. Shared setup logic uses `@BeforeEach` methods. Common test data uses `private static final` constants.

- **Deterministic clock injection:** All tests that rely on the "current date" must inject a fixed `java.time.Clock` into the `AgeCalculator` instance rather than using `LocalDate.now()` directly. This prevents test flakiness due to date transitions during test execution.

- **Exception message verification:** When testing error conditions using `assertThrows()`, the test should also verify the exception message content where applicable, ensuring that users receive meaningful feedback for invalid inputs.

- **`java.time` API usage only:** All date operations in both source and test code must use the `java.time` package (`LocalDate`, `Period`, `DateTimeFormatter`). Legacy `java.util.Date` and `java.text.SimpleDateFormat` classes must not be used anywhere.

- **DD/MM/YYYY format enforcement:** The `DateTimeFormatter` pattern must be `dd/MM/yyyy` with `ResolverStyle.STRICT` to ensure that invalid dates like `31/02/2020` are properly rejected rather than silently adjusted.

- **Test isolation:** All tests must run independently and in any order. No shared mutable state between test methods. The `AgeCalculator` instance must be created fresh for each test.

- **Clean coding standards in tests:** Test code must be as clean and readable as production code. Use descriptive method names, meaningful variable names, and avoid inline comments where the code is self-explanatory.

- **User-provided test cases as mandatory minimums:** The five test scenarios explicitly listed by the user (Normal DOB, Leap year DOB, Invalid date, Future date, Wrong format input) are mandatory test cases. The additional edge cases and parameterized tests extend beyond these minimums but do not replace them.

- **Maven standard directory layout:** All source code resides under `src/main/java/` and all test code resides under `src/test/java/`, following the standard Maven convention without deviation.


