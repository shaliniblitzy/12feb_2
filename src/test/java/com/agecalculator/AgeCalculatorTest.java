package com.agecalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Core unit test class for {@link AgeCalculator}.
 *
 * <p>This is the primary JUnit 5 test class covering the Age Calculator's core
 * business logic. It validates happy-path age calculations, the
 * {@code calculateAge(String)} and {@code calculateAge(LocalDate)} method
 * overloads, the {@code parseDateOfBirth(String)} parsing method, Clock
 * injection determinism, and result object correctness.</p>
 *
 * <h3>Deterministic Testing Strategy</h3>
 * <p>All tests use a fixed {@link Clock} anchored to a constant reference date
 * ({@code 2025-03-15} UTC) so that computed age results are independent of the
 * real system clock. This eliminates flaky tests caused by date transitions
 * during test execution.</p>
 *
 * <h3>Test Organization</h3>
 * <p>Tests are grouped into five {@link Nested} inner classes:</p>
 * <ol>
 *   <li><strong>Happy Path Age Calculations</strong> — normal DOB strings with
 *       known expected ages</li>
 *   <li><strong>LocalDate Overload Tests</strong> — direct {@link LocalDate}
 *       parameter overload</li>
 *   <li><strong>ParseDateOfBirth Tests</strong> — string-to-{@link LocalDate}
 *       parsing verification</li>
 *   <li><strong>Clock Injection Tests</strong> — determinism and constructor
 *       verification</li>
 *   <li><strong>Result Object Verification</strong> — {@link AgeResult}
 *       completeness and format checks</li>
 * </ol>
 *
 * @see AgeCalculator
 * @see AgeResult
 */
@DisplayName("AgeCalculator Core Unit Tests")
class AgeCalculatorTest {

    // ──────────────────────────────────────────────────────────────────────
    // Fixed reference date and clock for deterministic testing
    // ──────────────────────────────────────────────────────────────────────

    /**
     * The fixed reference date against which all ages are calculated.
     * Every expected value in this test class is pre-computed relative
     * to this date.
     */
    private static final LocalDate REFERENCE_DATE = LocalDate.of(2025, 3, 15);

    /** UTC time zone used for the fixed clock. */
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");

    /**
     * A fixed {@link Clock} that always reports the current date as
     * {@link #REFERENCE_DATE}. Injected into {@link AgeCalculator} via
     * its parameterized constructor to guarantee deterministic results.
     */
    private static final Clock FIXED_CLOCK =
            Clock.fixed(REFERENCE_DATE.atStartOfDay(ZONE_ID).toInstant(), ZONE_ID);

    /** The calculator instance under test, re-created before each test. */
    private AgeCalculator calculator;

    /**
     * Creates a fresh {@link AgeCalculator} with the {@link #FIXED_CLOCK}
     * before every test method, ensuring complete test isolation and
     * deterministic behaviour.
     */
    @BeforeEach
    void setUp() {
        calculator = new AgeCalculator(FIXED_CLOCK);
    }

    // ══════════════════════════════════════════════════════════════════════
    // 1. Happy Path Age Calculations
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Grouped tests that exercise {@link AgeCalculator#calculateAge(String)}
     * with well-formed, valid DOB strings and assert the resulting years,
     * months, and days against pre-computed expected values.
     */
    @Nested
    @DisplayName("Happy Path Age Calculations")
    class HappyPathAgeCalculations {

        /**
         * THE USER'S PRIMARY EXAMPLE.
         *
         * <p>DOB {@code 15/08/1998} against reference date {@code 2025-03-15}:
         * {@link Period#between(LocalDate, LocalDate)} yields
         * {@code P26Y7M0D} (26 years, 7 months, 0 days).</p>
         *
         * <p>Verification path:
         * <br>{@code 1998-08-15 + 26Y = 2024-08-15}
         * <br>{@code 2024-08-15 + 7M  = 2025-03-15}
         * <br>{@code 0 days remaining}</p>
         */
        @Test
        @DisplayName("Normal DOB (15/08/1998) returns 26 years, 7 months, 0 days")
        void testCalculateAge_NormalDOB_ReturnsCorrectAge() {
            // Independently verify expected values using Period.between()
            Period expectedPeriod = Period.between(
                    LocalDate.of(1998, 8, 15), REFERENCE_DATE);
            AgeResult result = calculator.calculateAge("15/08/1998");

            assertAll("Normal DOB age calculation",
                    () -> assertEquals(expectedPeriod.getYears(), result.getYears(), "Years"),
                    () -> assertEquals(expectedPeriod.getMonths(), result.getMonths(), "Months"),
                    () -> assertEquals(expectedPeriod.getDays(), result.getDays(), "Days"),
                    () -> assertEquals(26, result.getYears(), "Expected 26 years"),
                    () -> assertEquals(7, result.getMonths(), "Expected 7 months"),
                    () -> assertEquals(0, result.getDays(), "Expected 0 days")
            );
        }

        /**
         * Recent DOB within the last few years.
         *
         * <p>DOB {@code 01/01/2020} against reference date {@code 2025-03-15}:
         * {@code Period.between(2020-01-01, 2025-03-15)} = 5 years, 2 months,
         * 14 days.</p>
         *
         * <p>Verification path:
         * <br>{@code 2020-01-01 + 5Y  = 2025-01-01}
         * <br>{@code 2025-01-01 + 2M  = 2025-03-01}
         * <br>{@code 2025-03-01 + 14D = 2025-03-15}</p>
         */
        @Test
        @DisplayName("Recent DOB (01/01/2020) returns 5 years, 2 months, 14 days")
        void testCalculateAge_RecentDOB_ReturnsCorrectAge() {
            AgeResult result = calculator.calculateAge("01/01/2020");

            assertAll("Recent DOB age calculation",
                    () -> assertEquals(5, result.getYears(), "Years"),
                    () -> assertEquals(2, result.getMonths(), "Months"),
                    () -> assertEquals(14, result.getDays(), "Days")
            );
        }

        /**
         * DOB in the same calendar month (March) as the reference date.
         *
         * <p>DOB {@code 01/03/2020} against reference date {@code 2025-03-15}:
         * {@code Period.between(2020-03-01, 2025-03-15)} = 5 years, 0 months,
         * 14 days.</p>
         *
         * <p>Verification path:
         * <br>{@code 2020-03-01 + 5Y  = 2025-03-01}
         * <br>{@code 0 additional months}
         * <br>{@code 2025-03-01 + 14D = 2025-03-15}</p>
         */
        @Test
        @DisplayName("Same month DOB (01/03/2020) returns 5 years, 0 months, 14 days")
        void testCalculateAge_SameMonthDOB_ReturnsCorrectAge() {
            AgeResult result = calculator.calculateAge("01/03/2020");

            assertAll("Same-month DOB age calculation",
                    () -> assertEquals(5, result.getYears(), "Years"),
                    () -> assertEquals(0, result.getMonths(), "Months"),
                    () -> assertEquals(14, result.getDays(), "Days")
            );
        }

        /**
         * DOB in the same calendar year as the reference date.
         *
         * <p>DOB {@code 01/01/2025} against reference date {@code 2025-03-15}:
         * {@code Period.between(2025-01-01, 2025-03-15)} = 0 years, 2 months,
         * 14 days.</p>
         *
         * <p>Verification path:
         * <br>{@code 0 additional years}
         * <br>{@code 2025-01-01 + 2M  = 2025-03-01}
         * <br>{@code 2025-03-01 + 14D = 2025-03-15}</p>
         */
        @Test
        @DisplayName("Same year DOB (01/01/2025) returns 0 years, 2 months, 14 days")
        void testCalculateAge_SameYearDOB_ReturnsCorrectAge() {
            AgeResult result = calculator.calculateAge("01/01/2025");

            assertAll("Same-year DOB age calculation",
                    () -> assertEquals(0, result.getYears(), "Years"),
                    () -> assertEquals(2, result.getMonths(), "Months"),
                    () -> assertEquals(14, result.getDays(), "Days")
            );
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // 2. LocalDate Overload Tests
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Grouped tests that exercise the {@link AgeCalculator#calculateAge(LocalDate)}
     * overload, verifying that it produces correct results identical to the
     * {@code String}-based overload for the same date of birth.
     */
    @Nested
    @DisplayName("LocalDate Overload Tests")
    class LocalDateOverloadTests {

        /**
         * Same DOB as the user's primary example, supplied as a {@link LocalDate}.
         *
         * <p>DOB {@code 1998-08-15} against reference date {@code 2025-03-15}:
         * expected 26 years, 7 months, 0 days.</p>
         */
        @Test
        @DisplayName("LocalDate DOB (1998-08-15) returns 26 years, 7 months, 0 days")
        void testCalculateAge_WithLocalDate_ReturnsCorrectAge() {
            AgeResult result = calculator.calculateAge(LocalDate.of(1998, 8, 15));

            assertAll("LocalDate DOB age calculation",
                    () -> assertEquals(26, result.getYears(), "Years"),
                    () -> assertEquals(7, result.getMonths(), "Months"),
                    () -> assertEquals(0, result.getDays(), "Days")
            );
        }

        /**
         * Recent DOB supplied as a {@link LocalDate}.
         *
         * <p>DOB {@code 2020-06-15} against reference date {@code 2025-03-15}:
         * {@code Period.between(2020-06-15, 2025-03-15)} = 4 years, 9 months,
         * 0 days.</p>
         *
         * <p>Verification path:
         * <br>{@code 2020-06-15 + 4Y = 2024-06-15}
         * <br>{@code 2024-06-15 + 9M = 2025-03-15}
         * <br>{@code 0 days remaining}</p>
         */
        @Test
        @DisplayName("LocalDate recent DOB (2020-06-15) returns 4 years, 9 months, 0 days")
        void testCalculateAge_WithLocalDate_RecentDOB() {
            AgeResult result = calculator.calculateAge(LocalDate.of(2020, 6, 15));

            assertAll("LocalDate recent DOB age calculation",
                    () -> assertEquals(4, result.getYears(), "Years"),
                    () -> assertEquals(9, result.getMonths(), "Months"),
                    () -> assertEquals(0, result.getDays(), "Days")
            );
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // 3. ParseDateOfBirth Tests
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Grouped tests that verify {@link AgeCalculator#parseDateOfBirth(String)}
     * correctly converts well-formed DOB strings into {@link LocalDate}
     * instances.
     */
    @Nested
    @DisplayName("ParseDateOfBirth Tests")
    class ParseDateOfBirthTests {

        /**
         * Standard valid date — the user's primary example.
         *
         * <p>Input {@code "15/08/1998"} must parse to
         * {@code LocalDate.of(1998, 8, 15)}.</p>
         */
        @Test
        @DisplayName("Valid format '15/08/1998' parses to 1998-08-15")
        void testParseDateOfBirth_ValidFormat_ReturnsLocalDate() {
            LocalDate result = calculator.parseDateOfBirth("15/08/1998");

            assertEquals(LocalDate.of(1998, 8, 15), result,
                    "Parsed date should be 1998-08-15");
        }

        /**
         * Leap year date — February 29 in a valid leap year (2000).
         *
         * <p>Year 2000 is a century leap year (divisible by 400).
         * Input {@code "29/02/2000"} must parse to
         * {@code LocalDate.of(2000, 2, 29)}.</p>
         */
        @Test
        @DisplayName("Leap year date '29/02/2000' parses to 2000-02-29")
        void testParseDateOfBirth_LeapYear_ReturnsLocalDate() {
            LocalDate result = calculator.parseDateOfBirth("29/02/2000");

            assertEquals(LocalDate.of(2000, 2, 29), result,
                    "Parsed date should be 2000-02-29 (valid leap year)");
        }

        /**
         * New Year's Day — common boundary date.
         *
         * <p>Input {@code "01/01/2000"} must parse to
         * {@code LocalDate.of(2000, 1, 1)}.</p>
         */
        @Test
        @DisplayName("New Year's Day '01/01/2000' parses to 2000-01-01")
        void testParseDateOfBirth_NewYearsDay_ReturnsLocalDate() {
            LocalDate result = calculator.parseDateOfBirth("01/01/2000");

            assertEquals(LocalDate.of(2000, 1, 1), result,
                    "Parsed date should be 2000-01-01");
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // 4. Clock Injection Tests
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Grouped tests that verify the Clock injection pattern works correctly,
     * ensuring that identical clocks produce identical results and different
     * clocks produce different results, and that the default constructor
     * creates a functional instance.
     */
    @Nested
    @DisplayName("Clock Injection Tests")
    class ClockInjectionTests {

        /**
         * Two {@link AgeCalculator} instances initialized with the SAME fixed
         * clock must return identical age results for the same DOB.
         *
         * <p>This verifies that the Clock injection produces fully
         * deterministic, reproducible results.</p>
         */
        @Test
        @DisplayName("Same fixed clock produces identical results across instances")
        void testCalculateAge_WithFixedClock_ReturnsDeterministicResult() {
            AgeCalculator calc1 = new AgeCalculator(FIXED_CLOCK);
            AgeCalculator calc2 = new AgeCalculator(FIXED_CLOCK);

            AgeResult r1 = calc1.calculateAge("15/08/1998");
            AgeResult r2 = calc2.calculateAge("15/08/1998");

            assertAll("Deterministic results with same clock",
                    () -> assertEquals(r1.getYears(), r2.getYears(),
                            "Years must be identical"),
                    () -> assertEquals(r1.getMonths(), r2.getMonths(),
                            "Months must be identical"),
                    () -> assertEquals(r1.getDays(), r2.getDays(),
                            "Days must be identical")
            );
        }

        /**
         * Two {@link AgeCalculator} instances initialized with DIFFERENT fixed
         * clocks (one year apart) must return different age results for the
         * same DOB.
         *
         * <p>Clock 1: {@code 2025-03-15} → 26 years for DOB 15/08/1998
         * <br>Clock 2: {@code 2026-03-15} → 27 years for DOB 15/08/1998</p>
         */
        @Test
        @DisplayName("Different fixed clocks produce different year results")
        void testCalculateAge_WithDifferentClocks_ReturnsDifferentResults() {
            LocalDate laterDate = LocalDate.of(2026, 3, 15);
            Clock laterClock = Clock.fixed(
                    laterDate.atStartOfDay(ZONE_ID).toInstant(), ZONE_ID);

            AgeCalculator calcEarlier = new AgeCalculator(FIXED_CLOCK);
            AgeCalculator calcLater = new AgeCalculator(laterClock);

            AgeResult r1 = calcEarlier.calculateAge("15/08/1998");
            AgeResult r2 = calcLater.calculateAge("15/08/1998");

            assertNotEquals(r1.getYears(), r2.getYears(),
                    "Years should differ when clocks are one year apart");
        }

        /**
         * The default (no-arg) constructor creates a functional instance that
         * uses the real system clock.
         *
         * <p>Since the real system date is not fixed, this test only verifies:
         * <ul>
         *   <li>The instance is non-null</li>
         *   <li>It can calculate an age without throwing</li>
         *   <li>The resulting age is at least 26 years (the DOB 15/08/1998
         *       guarantees ≥26 as of any date in 2025 or later)</li>
         * </ul></p>
         */
        @Test
        @DisplayName("Default constructor creates a working instance with system clock")
        void testAgeCalculator_DefaultConstructor_CreatesInstance() {
            AgeCalculator defaultCalc = new AgeCalculator();

            assertNotNull(defaultCalc,
                    "Default constructor should produce a non-null instance");

            AgeResult result = defaultCalc.calculateAge("15/08/1998");

            assertNotNull(result,
                    "calculateAge should return a non-null AgeResult");
            assertTrue(result.getYears() >= 26,
                    "Person born 15/08/1998 should be at least 26 years old "
                            + "(test is running in 2025 or later)");
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // 5. Result Object Verification
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Grouped tests that verify the {@link AgeResult} object returned by
     * {@link AgeCalculator#calculateAge(String)} is correctly constructed,
     * non-null, produces the expected {@link AgeResult#toString()} format,
     * and that both method overloads produce identical results.
     */
    @Nested
    @DisplayName("Result Object Verification")
    class ResultObjectVerification {

        /**
         * The result of {@code calculateAge} must never be {@code null} for
         * a valid DOB input.
         */
        @Test
        @DisplayName("calculateAge returns non-null AgeResult")
        void testCalculateAge_ResultNotNull() {
            AgeResult result = calculator.calculateAge("15/08/1998");

            assertNotNull(result, "Result should not be null");
        }

        /**
         * The {@link AgeResult#toString()} output must match the exact
         * user-specified format: {@code "Your age is X years, Y months, and Z days."}
         *
         * <p>For DOB {@code 15/08/1998} with reference date {@code 2025-03-15},
         * the expected output is:
         * {@code "Your age is 26 years, 7 months, and 0 days."}</p>
         */
        @Test
        @DisplayName("AgeResult.toString() matches 'Your age is 26 years, 7 months, and 0 days.'")
        void testCalculateAge_ResultToString_MatchesExpectedFormat() {
            AgeResult result = calculator.calculateAge("15/08/1998");

            assertEquals(
                    "Your age is 26 years, 7 months, and 0 days.",
                    result.toString(),
                    "toString() must match the exact user-specified format");
        }

        /**
         * Both overloads — {@code calculateAge(String)} and
         * {@code calculateAge(LocalDate)} — must produce identical results
         * for the same date of birth.
         *
         * <p>DOB {@code "15/08/1998"} (string) and
         * {@code LocalDate.of(1998, 8, 15)} (LocalDate) must yield the same
         * years, months, and days.</p>
         */
        @Test
        @DisplayName("String and LocalDate overloads produce identical results")
        void testCalculateAge_StringAndLocalDate_ProduceSameResult() {
            AgeResult fromString = calculator.calculateAge("15/08/1998");
            AgeResult fromLocalDate = calculator.calculateAge(LocalDate.of(1998, 8, 15));

            assertAll("String and LocalDate overloads produce same result",
                    () -> assertEquals(fromString.getYears(), fromLocalDate.getYears(),
                            "Years must match"),
                    () -> assertEquals(fromString.getMonths(), fromLocalDate.getMonths(),
                            "Months must match"),
                    () -> assertEquals(fromString.getDays(), fromLocalDate.getDays(),
                            "Days must match")
            );
        }
    }
}
