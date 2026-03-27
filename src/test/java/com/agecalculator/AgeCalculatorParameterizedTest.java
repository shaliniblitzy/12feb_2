package com.agecalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Data-driven parameterized test class for {@link AgeCalculator}.
 *
 * <p>This class exercises the age calculation engine with multiple input
 * variants using JUnit 5's {@code @ParameterizedTest} infrastructure. All
 * test iterations execute against a fixed reference date of
 * {@code 2025-03-15} to guarantee deterministic, date-independent results.</p>
 *
 * <h3>Test Categories</h3>
 * <ul>
 *   <li><strong>Valid DOBs</strong> — 8+ date-of-birth strings with
 *       pre-computed expected years, months, and days verified against
 *       {@link java.time.Period#between(LocalDate, LocalDate)}</li>
 *   <li><strong>Invalid Calendar Dates</strong> — impossible dates such as
 *       {@code 31/02/2020} or {@code 29/02/2001} that must be rejected</li>
 *   <li><strong>Malformed Format Strings</strong> — inputs not conforming to
 *       the {@code DD/MM/YYYY} pattern (ISO format, short year, non-date
 *       strings, etc.)</li>
 *   <li><strong>Future Dates</strong> — dates occurring after the reference
 *       date that must trigger {@link IllegalArgumentException}</li>
 * </ul>
 *
 * <h3>Deterministic Clock Injection</h3>
 * <p>A {@link Clock#fixed(Instant, ZoneId)} instance anchored to
 * {@code 2025-03-15T00:00:00Z} is injected into every {@link AgeCalculator}
 * instance via the {@link AgeCalculator#AgeCalculator(Clock)} constructor.
 * This eliminates flakiness caused by real system clock transitions.</p>
 *
 * @see AgeCalculator
 * @see AgeResult
 */
@DisplayName("AgeCalculator Parameterized Tests")
class AgeCalculatorParameterizedTest {

    /**
     * Fixed reference date against which all expected age values are
     * pre-computed. Chosen to be March 15, 2025 — a non-leap-year date that
     * avoids edge cases in the reference date itself.
     */
    private static final LocalDate REFERENCE_DATE = LocalDate.of(2025, 3, 15);

    /** UTC time zone used for constructing the fixed clock. */
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");

    /**
     * Fixed clock anchored to {@link #REFERENCE_DATE} at midnight UTC.
     * Injected into {@link AgeCalculator} so that {@code LocalDate.now(clock)}
     * always returns {@code 2025-03-15}, regardless of the actual system time.
     */
    private static final Clock FIXED_CLOCK = Clock.fixed(
            REFERENCE_DATE.atStartOfDay(ZONE_ID).toInstant(), ZONE_ID);

    /** The calculator instance under test, re-created before every test. */
    private AgeCalculator calculator;

    /**
     * Creates a fresh {@link AgeCalculator} with the deterministic
     * {@link #FIXED_CLOCK} before every parameterized test invocation.
     * This ensures complete test isolation — no shared mutable state
     * between iterations.
     */
    @BeforeEach
    void setUp() {
        calculator = new AgeCalculator(FIXED_CLOCK);
    }

    // -------------------------------------------------------------------------
    // Parameterized Test — Multiple Valid DOBs
    // -------------------------------------------------------------------------

    /**
     * Verifies that {@link AgeCalculator#calculateAge(String)} returns the
     * correct age components (years, months, days) for a variety of valid
     * date-of-birth strings.
     *
     * <p>Each CSV row contains a DOB string in {@code DD/MM/YYYY} format
     * followed by the expected years, months, and days computed via
     * {@code Period.between(dob, LocalDate.of(2025, 3, 15))} and verified
     * independently using Java 17's {@code java.time} API.</p>
     *
     * @param dob          the date-of-birth string to test
     * @param expectedYears expected years component of the age
     * @param expectedMonths expected months component of the age
     * @param expectedDays  expected days component of the age
     */
    @ParameterizedTest
    @CsvSource({
            // User example DOB: Aug 15, 1998 → 26y 7m 0d
            "15/08/1998, 26, 7, 0",
            // New Year's Day 2000: Jan 1, 2000 → 25y 2m 14d
            "01/01/2000, 25, 2, 14",
            // Exactly 1 year before reference: Mar 15, 2024 → 1y 0m 0d
            "15/03/2024, 1, 0, 0",
            // Same as reference date (zero age): Mar 15, 2025 → 0y 0m 0d
            "15/03/2025, 0, 0, 0",
            // Yesterday relative to reference: Mar 14, 2025 → 0y 0m 1d
            "14/03/2025, 0, 0, 1",
            // Christmas 1990: Dec 25, 1990 → 34y 2m 18d
            "25/12/1990, 34, 2, 18",
            // Leap year DOB: Feb 29, 2000 → 25y 0m 15d
            "29/02/2000, 25, 0, 15",
            // Mid-2020: Jul 1, 2020 → 4y 8m 14d
            "01/07/2020, 4, 8, 14"
    })
    @DisplayName("Should calculate correct age for valid DOBs")
    void testCalculateAge_MultipleValidDOBs(String dob, int expectedYears,
                                            int expectedMonths, int expectedDays) {
        AgeResult result = calculator.calculateAge(dob);

        assertNotNull(result, "AgeResult must not be null for DOB: " + dob);
        assertAll(
                "Age components for DOB: " + dob,
                () -> assertEquals(expectedYears, result.getYears(),
                        "Years mismatch for DOB: " + dob),
                () -> assertEquals(expectedMonths, result.getMonths(),
                        "Months mismatch for DOB: " + dob),
                () -> assertEquals(expectedDays, result.getDays(),
                        "Days mismatch for DOB: " + dob)
        );
    }

    // -------------------------------------------------------------------------
    // Parameterized Test — Multiple Invalid Calendar Dates
    // -------------------------------------------------------------------------

    /**
     * Verifies that {@link AgeCalculator#calculateAge(String)} throws
     * {@link DateTimeParseException} for calendar dates that do not exist.
     *
     * <p>Each value represents an impossible date that the strict
     * {@code DateTimeFormatter} (with {@code ResolverStyle.STRICT}) must
     * reject rather than silently adjusting.</p>
     *
     * @param invalidDate an impossible calendar date string
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "31/02/2020",  // February 31 — impossible in any year
            "29/02/2001",  // February 29 on a non-leap year (2001)
            "29/02/1900",  // February 29 on century non-leap year (1900 ÷ 100 but not ÷ 400)
            "31/04/2020",  // April 31 — April has only 30 days
            "31/06/2020",  // June 31 — June has only 30 days
            "00/01/2020",  // Day zero — invalid day
            "15/00/2020",  // Month zero — invalid month
            "15/13/2020"   // Month 13 — invalid month
    })
    @DisplayName("Should reject invalid calendar dates")
    void testCalculateAge_MultipleInvalidDates(String invalidDate) {
        assertThrows(DateTimeParseException.class,
                () -> calculator.calculateAge(invalidDate),
                "Expected DateTimeParseException for invalid calendar date: " + invalidDate);
    }

    // -------------------------------------------------------------------------
    // Parameterized Test — Multiple Malformed Format Strings
    // -------------------------------------------------------------------------

    /**
     * Verifies that {@link AgeCalculator#calculateAge(String)} throws
     * {@link DateTimeParseException} for inputs that do not conform to the
     * required {@code DD/MM/YYYY} format pattern.
     *
     * <p>This includes ISO-8601 formatted dates, US-style dates, short year
     * formats, non-date text, partial dates, and dates with incorrect
     * separators.</p>
     *
     * @param malformedInput a string that does not match the DD/MM/YYYY format
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "1998-08-15",  // ISO format with dash separators (YYYY-MM-DD)
            "08/15/1998",  // US-style MM/DD/YYYY — month 15 is invalid
            "15/8/98",     // Short year and single-digit month — wrong format
            "abc",         // Non-date string — completely unparseable
            "15/08",       // Partial date — missing year component
            "15-08-1998",  // Dash separators instead of slashes
            "1998/08/15",  // Reversed order YYYY/MM/DD
            "15.08.1998"   // Dot separators instead of slashes
    })
    @DisplayName("Should reject malformed date format strings")
    void testCalculateAge_MultipleMalformedFormats(String malformedInput) {
        assertThrows(DateTimeParseException.class,
                () -> calculator.calculateAge(malformedInput),
                "Expected DateTimeParseException for malformed input: " + malformedInput);
    }

    // -------------------------------------------------------------------------
    // Parameterized Test — Multiple Future Dates
    // -------------------------------------------------------------------------

    /**
     * Verifies that {@link AgeCalculator#calculateAge(String)} throws
     * {@link IllegalArgumentException} for dates that fall after the
     * fixed reference date of {@code 2025-03-15}.
     *
     * <p>The {@link AgeCalculator} must reject future dates with the message
     * {@code "Date of birth cannot be in the future."} to prevent nonsensical
     * negative age calculations.</p>
     *
     * @param futureDate a date string after 2025-03-15 in DD/MM/YYYY format
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "16/03/2025",  // Tomorrow relative to the fixed reference date
            "01/01/2030",  // Far future — 5 years ahead
            "01/01/2099"   // Very far future — 74 years ahead
    })
    @DisplayName("Should reject future dates")
    void testCalculateAge_MultipleFutureDates(String futureDate) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateAge(futureDate),
                "Expected IllegalArgumentException for future date: " + futureDate);

        assertEquals("Date of birth cannot be in the future.", exception.getMessage(),
                "Exception message mismatch for future date: " + futureDate);
    }
}
