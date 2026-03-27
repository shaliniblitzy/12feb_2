package com.agecalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Edge case and boundary condition tests for {@link AgeCalculator}.
 *
 * <p>This test class exercises the following edge-case categories:</p>
 * <ul>
 *   <li><strong>Leap year DOBs</strong> — Feb 29 across multiple leap years
 *       (2000, 2004, 2024) and verification when the reference year is not a
 *       leap year</li>
 *   <li><strong>Century leap year logic</strong> — Year 2000 is a valid leap
 *       year (divisible by 400), while year 1900 is NOT (divisible by 100 but
 *       not 400)</li>
 *   <li><strong>Zero and minimal age</strong> — DOB equal to the reference
 *       date (today) and DOB one day before the reference date</li>
 *   <li><strong>Month-end boundaries</strong> — Born on the 31st when the
 *       reference month has fewer days, and year-boundary crossing from
 *       December to the following March</li>
 *   <li><strong>Extreme ages</strong> — Very old DOB (1900-01-01) and a DOB
 *       exactly one year prior to the reference date</li>
 * </ul>
 *
 * <p>All tests use a fixed {@link Clock} anchored to {@code 2025-03-15 UTC}
 * to guarantee deterministic, repeatable results regardless of when the test
 * suite is executed. Some boundary tests create test-specific clocks with
 * different reference dates (e.g., April 15, 2025 for month-end tests).</p>
 *
 * @see AgeCalculator
 * @see AgeResult
 */
@DisplayName("AgeCalculator Edge Case Tests")
class AgeCalculatorEdgeCaseTest {

    /** The AgeCalculator instance under test, re-created before each test method. */
    private AgeCalculator calculator;

    /** Fixed reference date used as "today" for the majority of edge-case tests. */
    private static final LocalDate REFERENCE_DATE = LocalDate.of(2025, 3, 15);

    /** UTC time zone used for all fixed clock constructions. */
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");

    /**
     * Fixed clock anchored to 2025-03-15T00:00:00Z, injected into the
     * AgeCalculator so that {@code LocalDate.now(clock)} always returns
     * {@code 2025-03-15}.
     */
    private static final Clock FIXED_CLOCK = Clock.fixed(
            REFERENCE_DATE.atStartOfDay(ZONE_ID).toInstant(), ZONE_ID);

    /**
     * Creates a fresh {@link AgeCalculator} with the {@link #FIXED_CLOCK}
     * before every test method, ensuring complete test isolation.
     */
    @BeforeEach
    void setUp() {
        calculator = new AgeCalculator(FIXED_CLOCK);
    }

    // =========================================================================
    // Leap Year DOB Tests
    // =========================================================================

    /**
     * Tests for DOBs falling on February 29 across multiple leap years.
     * Verifies that {@link AgeCalculator} correctly accepts and computes ages
     * for valid leap-day dates when the reference year (2025) is NOT a leap year.
     */
    @Nested
    @DisplayName("Leap Year DOB Tests")
    class LeapYearDOBTests {

        /**
         * Verifies correct age calculation for DOB February 29, 2000.
         * Year 2000 is a century leap year (divisible by 400).
         *
         * <p>Expected: {@code Period.between(2000-02-29, 2025-03-15)} yields
         * 25 years, 0 months, 15 days (verified via Java runtime).</p>
         */
        @Test
        @DisplayName("Feb 29, 2000 DOB returns correct age — 2000 is divisible by 400")
        void testCalculateAge_LeapYearDOB_Feb29_2000_ReturnsCorrectAge() {
            // First assert that this valid leap year date does not throw
            assertDoesNotThrow(() -> calculator.calculateAge("29/02/2000"),
                    "29/02/2000 is a valid leap year date and must not throw");

            AgeResult result = calculator.calculateAge("29/02/2000");
            assertNotNull(result, "Result must not be null for a valid DOB");

            // Period.between(2000-02-29, 2025-03-15) = 25 years, 0 months, 15 days
            assertAll("Age for DOB 29/02/2000 relative to 2025-03-15",
                    () -> assertEquals(25, result.getYears(), "Years should be 25"),
                    () -> assertEquals(0, result.getMonths(), "Months should be 0"),
                    () -> assertEquals(15, result.getDays(), "Days should be 15")
            );
        }

        /**
         * Verifies correct age calculation for DOB February 29, 2004.
         * Year 2004 is a standard leap year (divisible by 4, not by 100).
         *
         * <p>Expected: {@code Period.between(2004-02-29, 2025-03-15)} yields
         * 21 years, 0 months, 15 days.</p>
         */
        @Test
        @DisplayName("Feb 29, 2004 DOB returns correct age — standard leap year")
        void testCalculateAge_LeapYearDOB_Feb29_2004_ReturnsCorrectAge() {
            AgeResult result = calculator.calculateAge("29/02/2004");
            assertNotNull(result, "Result must not be null for a valid DOB");

            // Period.between(2004-02-29, 2025-03-15) = 21 years, 0 months, 15 days
            assertAll("Age for DOB 29/02/2004 relative to 2025-03-15",
                    () -> assertEquals(21, result.getYears(), "Years should be 21"),
                    () -> assertEquals(0, result.getMonths(), "Months should be 0"),
                    () -> assertEquals(15, result.getDays(), "Days should be 15")
            );
        }

        /**
         * Verifies correct age calculation for DOB February 29, 2024.
         * Year 2024 is the most recent leap year before the reference date.
         *
         * <p>Expected: {@code Period.between(2024-02-29, 2025-03-15)} yields
         * 1 year, 0 months, 15 days.</p>
         */
        @Test
        @DisplayName("Feb 29, 2024 DOB returns correct age — most recent leap year")
        void testCalculateAge_LeapYearDOB_Feb29_2024_ReturnsCorrectAge() {
            AgeResult result = calculator.calculateAge("29/02/2024");
            assertNotNull(result, "Result must not be null for a valid DOB");

            // Period.between(2024-02-29, 2025-03-15) = 1 year, 0 months, 15 days
            assertAll("Age for DOB 29/02/2024 relative to 2025-03-15",
                    () -> assertEquals(1, result.getYears(), "Years should be 1"),
                    () -> assertEquals(0, result.getMonths(), "Months should be 0"),
                    () -> assertEquals(15, result.getDays(), "Days should be 15")
            );
        }

        /**
         * Verifies that a person born on Feb 29 has their age calculated
         * correctly when the reference year (2025) is NOT a leap year.
         *
         * <p>{@code Period.between} handles this by computing the period from
         * Feb 29 of the DOB year forward. This test confirms non-null result
         * with valid non-negative components.</p>
         */
        @Test
        @DisplayName("Feb 29 DOB calculated correctly when reference year (2025) is not a leap year")
        void testCalculateAge_LeapYearDOB_CurrentYearNotLeap() {
            // 2025 is not a leap year — verify Feb 29 DOB is still handled
            AgeResult result = calculator.calculateAge("29/02/2000");
            assertNotNull(result, "Result should not be null for valid leap year DOB");

            assertAll("Feb 29 DOB with non-leap reference year",
                    () -> assertTrue(result.getYears() >= 0,
                            "Years should be non-negative"),
                    () -> assertTrue(result.getMonths() >= 0,
                            "Months should be non-negative"),
                    () -> assertTrue(result.getDays() >= 0,
                            "Days should be non-negative"),
                    () -> assertEquals(25, result.getYears(),
                            "Years should be 25 for DOB 29/02/2000")
            );
        }
    }

    // =========================================================================
    // Century Leap Year Logic Tests
    // =========================================================================

    /**
     * Tests verifying the Gregorian century leap year rule:
     * <ul>
     *   <li>Years divisible by 400 ARE leap years (e.g., 2000)</li>
     *   <li>Years divisible by 100 but NOT by 400 are NOT leap years (e.g., 1900)</li>
     * </ul>
     * The {@code DateTimeFormatter} with {@code ResolverStyle.STRICT} enforces
     * this rule, rejecting Feb 29 on non-leap century years.
     */
    @Nested
    @DisplayName("Century Leap Year Logic")
    class CenturyLeapYearLogicTests {

        /**
         * Confirms that year 2000 is recognized as a valid leap year.
         * Feb 29, 2000 must be accepted without throwing any exception.
         */
        @Test
        @DisplayName("Year 2000 IS a leap year — divisible by 400")
        void testCalculateAge_CenturyLeapYear_2000IsLeap() {
            assertDoesNotThrow(() -> calculator.calculateAge("29/02/2000"),
                    "29/02/2000 should be a valid date — year 2000 is a leap year (divisible by 400)");

            AgeResult result = calculator.calculateAge("29/02/2000");
            assertNotNull(result, "Result must not be null for valid century leap year DOB");
            assertTrue(result.getYears() > 0,
                    "Years should be positive for year 2000 DOB — confirms date was accepted and age computed");
        }

        /**
         * Confirms that year 1900 is correctly identified as NOT a leap year.
         * Feb 29, 1900 must be rejected by the STRICT resolver with a
         * {@link DateTimeParseException}, since 1900 is divisible by 100 but
         * not by 400.
         */
        @Test
        @DisplayName("Year 1900 is NOT a leap year — divisible by 100 but not 400")
        void testCalculateAge_CenturyNonLeapYear_1900IsNotLeap() {
            assertThrows(DateTimeParseException.class,
                    () -> calculator.calculateAge("29/02/1900"),
                    "29/02/1900 should be rejected — 1900 is not a leap year (divisible by 100 but not 400)");
        }
    }

    // =========================================================================
    // Zero and Minimal Age Tests
    // =========================================================================

    /**
     * Tests for the smallest possible age values: zero age (DOB equals the
     * reference date) and minimal age (DOB is one day before the reference date).
     */
    @Nested
    @DisplayName("Zero and Minimal Age Tests")
    class ZeroAndMinimalAgeTests {

        /**
         * Verifies that when DOB equals the reference date, the age is exactly
         * 0 years, 0 months, and 0 days.
         */
        @Test
        @DisplayName("DOB is today (reference date) — returns zero age")
        void testCalculateAge_DOBIsToday_ReturnsZeroAge() {
            AgeResult result = calculator.calculateAge("15/03/2025");
            assertNotNull(result, "Result must not be null for today's date as DOB");

            assertAll("Zero age — DOB equals reference date 2025-03-15",
                    () -> assertEquals(0, result.getYears(), "Years should be 0"),
                    () -> assertEquals(0, result.getMonths(), "Months should be 0"),
                    () -> assertEquals(0, result.getDays(), "Days should be 0")
            );
        }

        /**
         * Verifies that when DOB is exactly one day before the reference date,
         * the age is 0 years, 0 months, and 1 day.
         */
        @Test
        @DisplayName("DOB is yesterday — returns exactly 0 years, 0 months, 1 day")
        void testCalculateAge_DOBIsYesterday_ReturnsOneDayAge() {
            AgeResult result = calculator.calculateAge("14/03/2025");
            assertNotNull(result, "Result must not be null for yesterday's DOB");

            assertAll("Minimal age — DOB is one day before reference date",
                    () -> assertEquals(0, result.getYears(), "Years should be 0"),
                    () -> assertEquals(0, result.getMonths(), "Months should be 0"),
                    () -> assertEquals(1, result.getDays(), "Days should be 1")
            );
        }

        /**
         * Verifies zero age using the {@code calculateAge(LocalDate)} overload.
         * Passing a {@code LocalDate} equal to the reference date must produce
         * exactly zero for all components.
         */
        @Test
        @DisplayName("DOB is today using LocalDate overload — returns zero age")
        void testCalculateAge_DOBIsToday_WithLocalDate_ReturnsZeroAge() {
            AgeResult result = calculator.calculateAge(LocalDate.of(2025, 3, 15));
            assertNotNull(result, "Result must not be null for LocalDate DOB equal to today");

            assertAll("Zero age via LocalDate overload",
                    () -> assertEquals(0, result.getYears(), "Years should be 0"),
                    () -> assertEquals(0, result.getMonths(), "Months should be 0"),
                    () -> assertEquals(0, result.getDays(), "Days should be 0")
            );
        }
    }

    // =========================================================================
    // Month-End Boundary Tests
    // =========================================================================

    /**
     * Tests for month-end boundary conditions where the DOB day-of-month
     * exceeds the number of days in the reference month, as well as
     * year-boundary crossing scenarios.
     */
    @Nested
    @DisplayName("Month-End Boundary Tests")
    class MonthEndBoundaryTests {

        /**
         * Verifies age calculation when born on Jan 31 and the reference date
         * falls in a 30-day month (April). Uses a test-specific clock set to
         * April 15, 2025 to isolate this boundary condition.
         *
         * <p>Expected: {@code Period.between(2025-01-31, 2025-04-15)} yields
         * 0 years, 2 months, 15 days (verified via Java runtime).</p>
         */
        @Test
        @DisplayName("Born on Jan 31 — reference date in 30-day month (April)")
        void testCalculateAge_BornOn31st_CurrentMonthHas30Days() {
            // Create a test-specific clock anchored to April 15, 2025 (a 30-day month)
            LocalDate aprilReference = LocalDate.of(2025, 4, 15);
            Clock aprilClock = Clock.fixed(
                    aprilReference.atStartOfDay(ZONE_ID).toInstant(), ZONE_ID);
            AgeCalculator aprilCalculator = new AgeCalculator(aprilClock);

            AgeResult result = aprilCalculator.calculateAge("31/01/2025");
            assertNotNull(result, "Result must not be null for valid month-end DOB");

            // Dynamically compute expected values to avoid hardcoding errors
            Period expected = Period.between(LocalDate.of(2025, 1, 31), aprilReference);
            assertAll("Month-end boundary — born on 31st, reference in 30-day month",
                    () -> assertEquals(expected.getYears(), result.getYears(),
                            "Years should be " + expected.getYears()),
                    () -> assertEquals(expected.getMonths(), result.getMonths(),
                            "Months should be " + expected.getMonths()),
                    () -> assertEquals(expected.getDays(), result.getDays(),
                            "Days should be " + expected.getDays())
            );
        }

        /**
         * Verifies age calculation when born on December 31, 2024 — crossing
         * the year boundary to the reference date March 15, 2025.
         *
         * <p>Uses {@code Period.between()} dynamically for expected value
         * generation, as month-end cross-year calculations are error-prone
         * when hardcoded.</p>
         */
        @Test
        @DisplayName("Born on Dec 31, 2024 — crosses year boundary to Mar 15, 2025")
        void testCalculateAge_BornOnDec31_CrossesYearBoundary() {
            AgeResult result = calculator.calculateAge("31/12/2024");
            assertNotNull(result, "Result must not be null for valid Dec 31 DOB");

            // Dynamically compute expected values — Dec 31 → Mar 15 is tricky
            // Period.between(2024-12-31, 2025-03-15) = 0y 2m 15d (verified via Java runtime)
            Period expected = Period.between(LocalDate.of(2024, 12, 31), REFERENCE_DATE);
            assertAll("Year boundary crossing — born Dec 31, reference Mar 15",
                    () -> assertEquals(expected.getYears(), result.getYears(),
                            "Years should be " + expected.getYears()),
                    () -> assertEquals(expected.getMonths(), result.getMonths(),
                            "Months should be " + expected.getMonths()),
                    () -> assertEquals(expected.getDays(), result.getDays(),
                            "Days should be " + expected.getDays())
            );
        }

        /**
         * Verifies age calculation when born on January 1, 2025 — within the
         * same year as the reference date, crossing two complete months.
         *
         * <p>Expected: {@code Period.between(2025-01-01, 2025-03-15)} yields
         * 0 years, 2 months, 14 days (verified via Java runtime).</p>
         */
        @Test
        @DisplayName("Born on Jan 1, 2025 — within same year to Mar 15, 2025")
        void testCalculateAge_BornOnJan1_CrossesYearBoundary() {
            AgeResult result = calculator.calculateAge("01/01/2025");
            assertNotNull(result, "Result must not be null for valid Jan 1 DOB");

            // Period.between(2025-01-01, 2025-03-15) = 0 years, 2 months, 14 days
            assertAll("Same-year boundary — born Jan 1, reference Mar 15",
                    () -> assertEquals(0, result.getYears(), "Years should be 0"),
                    () -> assertEquals(2, result.getMonths(), "Months should be 2"),
                    () -> assertEquals(14, result.getDays(), "Days should be 14")
            );
        }
    }

    // =========================================================================
    // Extreme Age Tests
    // =========================================================================

    /**
     * Tests for extreme age scenarios: very old DOBs and exact-year boundaries.
     */
    @Nested
    @DisplayName("Extreme Age Tests")
    class ExtremeAgeTests {

        /**
         * Verifies age calculation for a very old DOB: January 1, 1900.
         * This tests the system's ability to handle dates spanning 125+ years.
         *
         * <p>Expected: {@code Period.between(1900-01-01, 2025-03-15)} yields
         * 125 years, 2 months, 14 days (verified via Java runtime).</p>
         */
        @Test
        @DisplayName("Very old DOB Jan 1, 1900 — 125+ years old")
        void testCalculateAge_VeryOldDOB_1900() {
            AgeResult result = calculator.calculateAge("01/01/1900");
            assertNotNull(result, "Result must not be null for very old DOB");

            // Period.between(1900-01-01, 2025-03-15) = 125 years, 2 months, 14 days
            assertAll("Extreme age — born January 1, 1900",
                    () -> assertEquals(125, result.getYears(), "Years should be 125"),
                    () -> assertEquals(2, result.getMonths(), "Months should be 2"),
                    () -> assertEquals(14, result.getDays(), "Days should be 14")
            );
        }

        /**
         * Verifies age calculation when the DOB is exactly one year before the
         * reference date. This confirms that the period between the same
         * month-and-day across consecutive years produces exactly 1 year with
         * zero months and zero days.
         */
        @Test
        @DisplayName("DOB exactly one year ago — returns exactly 1 year, 0 months, 0 days")
        void testCalculateAge_ExactlyOneYearAgo() {
            AgeResult result = calculator.calculateAge("15/03/2024");
            assertNotNull(result, "Result must not be null for DOB exactly one year ago");

            // Period.between(2024-03-15, 2025-03-15) = 1 year, 0 months, 0 days
            assertAll("Exact one-year boundary",
                    () -> assertEquals(1, result.getYears(), "Years should be 1"),
                    () -> assertEquals(0, result.getMonths(), "Months should be 0"),
                    () -> assertEquals(0, result.getDays(), "Days should be 0")
            );
        }
    }
}
