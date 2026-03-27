package com.agecalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Comprehensive validation and error handling tests for {@link AgeCalculator}.
 *
 * <p>This test class verifies that the {@code AgeCalculator} correctly rejects
 * invalid inputs and produces meaningful exception messages. Tests are organized
 * into six logical groups using JUnit 5 {@link Nested} inner classes:</p>
 * <ol>
 *   <li><strong>Future Date Rejection</strong> — dates after the reference date</li>
 *   <li><strong>Invalid Calendar Date Rejection</strong> — impossible calendar dates</li>
 *   <li><strong>Wrong Format Input Rejection</strong> — non-DD/MM/YYYY formats</li>
 *   <li><strong>Non-Date and Special Input Handling</strong> — null, empty, non-date strings</li>
 *   <li><strong>validateDate() Direct Tests</strong> — direct validation method testing</li>
 *   <li><strong>parseDateOfBirth() Direct Tests</strong> — direct parsing method testing</li>
 * </ol>
 *
 * <p>All tests use a fixed {@link Clock} anchored to 2025-03-15 (UTC) to ensure
 * deterministic behavior regardless of when the tests are executed.</p>
 *
 * @see AgeCalculator
 * @see AgeResult
 */
@DisplayName("AgeCalculator Validation Tests")
class AgeCalculatorValidationTest {

    /** Reference date for deterministic testing: March 15, 2025. */
    private static final LocalDate REFERENCE_DATE = LocalDate.of(2025, 3, 15);

    /** Time zone used for the fixed clock — UTC for consistency across environments. */
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");

    /**
     * Fixed clock anchored to the REFERENCE_DATE at midnight UTC.
     * Injected into AgeCalculator to control what constitutes "today" and "future".
     */
    private static final Clock FIXED_CLOCK = Clock.fixed(
            REFERENCE_DATE.atStartOfDay(ZONE_ID).toInstant(), ZONE_ID);

    /** The AgeCalculator instance under test, re-created before each test method. */
    private AgeCalculator calculator;

    /**
     * Creates a fresh AgeCalculator with the fixed clock before every test,
     * ensuring complete test isolation and deterministic results.
     */
    @BeforeEach
    void setUp() {
        calculator = new AgeCalculator(FIXED_CLOCK);
    }

    // =========================================================================
    // Nested Class 1: Future Date Rejection Tests
    // =========================================================================

    /**
     * Tests that dates occurring after the reference date (2025-03-15) are
     * properly rejected with {@link IllegalArgumentException}.
     */
    @Nested
    @DisplayName("Future Date Rejection")
    class FutureDateRejectionTests {

        @Test
        @DisplayName("Tomorrow's date (16/03/2025) throws IllegalArgumentException with 'future' in message")
        void testValidateDate_FutureDate_ThrowsIllegalArgumentException() {
            // 16/03/2025 is one day after the reference date 2025-03-15
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateAge("16/03/2025"));

            // Verify the exception message mentions "future" for user clarity
            assertTrue(ex.getMessage().contains("future"),
                    "Exception message should mention 'future', but was: " + ex.getMessage());
        }

        @Test
        @DisplayName("Far future date (01/01/2099) throws IllegalArgumentException")
        void testValidateDate_FarFutureDate_ThrowsIllegalArgumentException() {
            // A date far in the future should be rejected identically to tomorrow
            assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateAge("01/01/2099"));
        }

        @Test
        @DisplayName("Future date via calculateAge(String) throws IllegalArgumentException")
        void testCalculateAge_FutureDate_ThrowsIllegalArgumentException() {
            // Verify calculateAge(String) rejects future dates consistently
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateAge("16/03/2025"));

            assertTrue(ex.getMessage().contains("future"),
                    "Exception message should mention 'future', but was: " + ex.getMessage());
        }

        @Test
        @DisplayName("Future LocalDate via calculateAge(LocalDate) throws IllegalArgumentException")
        void testCalculateAge_FutureLocalDate_ThrowsIllegalArgumentException() {
            // Test the LocalDate overload with a future date (one day after reference)
            LocalDate futureDate = LocalDate.of(2025, 3, 16);

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.calculateAge(futureDate));

            assertTrue(ex.getMessage().contains("future"),
                    "Exception message should mention 'future', but was: " + ex.getMessage());
        }
    }

    // =========================================================================
    // Nested Class 2: Invalid Calendar Date Rejection Tests
    // =========================================================================

    /**
     * Tests that impossible calendar dates are rejected with
     * {@link DateTimeParseException} by the strict DateTimeFormatter.
     */
    @Nested
    @DisplayName("Invalid Calendar Date Rejection")
    class InvalidCalendarDateRejectionTests {

        @Test
        @DisplayName("February 31 (31/02/2020) — impossible date — throws DateTimeParseException")
        void testValidateDate_InvalidDate_31Feb_ThrowsDateTimeParseException() {
            // February never has 31 days in any year
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.calculateAge("31/02/2020"));
        }

        @Test
        @DisplayName("February 29 on non-leap year 2001 (29/02/2001) throws DateTimeParseException")
        void testValidateDate_InvalidDate_29FebNonLeap_ThrowsDateTimeParseException() {
            // 2001 is not a leap year (not divisible by 4)
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.calculateAge("29/02/2001"));
        }

        @Test
        @DisplayName("February 29 on century non-leap year 1900 (29/02/1900) throws DateTimeParseException")
        void testValidateDate_InvalidDate_29Feb1900_ThrowsDateTimeParseException() {
            // 1900 is divisible by 100 but NOT by 400, so it is NOT a leap year
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.calculateAge("29/02/1900"));
        }

        @Test
        @DisplayName("Day zero (00/01/2020) — no zeroth day — throws DateTimeParseException")
        void testValidateDate_InvalidDate_DayZero_ThrowsDateTimeParseException() {
            // Day 0 is not a valid calendar day
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.calculateAge("00/01/2020"));
        }

        @Test
        @DisplayName("Month zero (15/00/2020) — no zeroth month — throws DateTimeParseException")
        void testValidateDate_InvalidDate_MonthZero_ThrowsDateTimeParseException() {
            // Month 0 is not a valid calendar month
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.calculateAge("15/00/2020"));
        }

        @Test
        @DisplayName("Month 13 (00/13/1990) — user-specified invalid case — throws DateTimeParseException")
        void testValidateDate_InvalidDate_Month13_ThrowsDateTimeParseException() {
            // Month 13 does not exist; also day 00 is invalid — double invalidity
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.calculateAge("00/13/1990"));
        }

        @Test
        @DisplayName("Day 32 (32/01/2000) — user-specified invalid case — throws DateTimeParseException")
        void testValidateDate_InvalidDate_Day32_ThrowsDateTimeParseException() {
            // No month has 32 days
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.calculateAge("32/01/2000"));
        }
    }

    // =========================================================================
    // Nested Class 3: Wrong Format Input Rejection Tests
    // =========================================================================

    /**
     * Tests that inputs not conforming to the {@code DD/MM/YYYY} format are
     * rejected with {@link DateTimeParseException}.
     */
    @Nested
    @DisplayName("Wrong Format Input Rejection")
    class WrongFormatInputRejectionTests {

        @Test
        @DisplayName("ISO format (1998-08-15) — YYYY-MM-DD instead of DD/MM/YYYY — throws DateTimeParseException")
        void testValidateDate_WrongFormat_ISO_ThrowsDateTimeParseException() {
            // ISO 8601 format with dash separators is not DD/MM/YYYY
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.calculateAge("1998-08-15"));
        }

        @Test
        @DisplayName("US-style (08/15/1998) — MM/DD/YYYY — throws DateTimeParseException")
        void testValidateDate_WrongFormat_USStyle_ThrowsDateTimeParseException() {
            // With STRICT resolver, month 15 is invalid — correctly rejected
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.calculateAge("08/15/1998"));
        }

        @Test
        @DisplayName("Short year (15/08/98) — 2-digit year — throws DateTimeParseException")
        void testValidateDate_WrongFormat_ShortYear_ThrowsDateTimeParseException() {
            // The formatter expects exactly 4-digit year (uuuu pattern)
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.calculateAge("15/08/98"));
        }

        @Test
        @DisplayName("Dash separators (15-08-1998) — dashes instead of slashes — throws DateTimeParseException")
        void testValidateDate_WrongFormat_DashSeparators_ThrowsDateTimeParseException() {
            // The formatter expects '/' separators, not '-'
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.calculateAge("15-08-1998"));
        }
    }

    // =========================================================================
    // Nested Class 4: Non-Date and Special Input Handling Tests
    // =========================================================================

    /**
     * Tests handling of non-date strings, empty strings, null values,
     * and partial dates.
     */
    @Nested
    @DisplayName("Non-Date and Special Input Handling")
    class NonDateAndSpecialInputTests {

        @Test
        @DisplayName("Non-date string 'abc' throws DateTimeParseException")
        void testValidateDate_NonDateString_ThrowsDateTimeParseException() {
            // A random alphabetic string cannot be parsed as a date
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.calculateAge("abc"));
        }

        @Test
        @DisplayName("Empty string throws exception (DateTimeParseException or IllegalArgumentException)")
        void testValidateDate_EmptyString_ThrowsException() {
            // The production code may throw IllegalArgumentException (for blank check)
            // or DateTimeParseException (from formatter). We accept either.
            Exception ex = assertThrows(
                    Exception.class,
                    () -> calculator.calculateAge(""));

            // Verify the exception is one of the two expected types
            assertTrue(
                    ex instanceof DateTimeParseException || ex instanceof IllegalArgumentException,
                    "Expected DateTimeParseException or IllegalArgumentException, but got: "
                            + ex.getClass().getName());
        }

        @Test
        @DisplayName("Null String input throws NullPointerException")
        void testValidateDate_NullInput_ThrowsNullPointerException() {
            // Cast to (String) to disambiguate from calculateAge(LocalDate) overload
            assertThrows(
                    NullPointerException.class,
                    () -> calculator.calculateAge((String) null));
        }

        @Test
        @DisplayName("Partial date (15/08) — missing year — throws DateTimeParseException")
        void testValidateDate_PartialDate_ThrowsDateTimeParseException() {
            // The year portion is absent; the formatter requires all three components
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.calculateAge("15/08"));
        }

        @Test
        @DisplayName("Null LocalDate input throws NullPointerException")
        void testValidateDate_NullLocalDate_ThrowsNullPointerException() {
            // Cast to (LocalDate) to disambiguate from calculateAge(String) overload
            assertThrows(
                    NullPointerException.class,
                    () -> calculator.calculateAge((LocalDate) null));
        }
    }

    // =========================================================================
    // Nested Class 5: validateDate() Direct Tests
    // =========================================================================

    /**
     * Tests that directly invoke the {@link AgeCalculator#validateDate(String)}
     * method to verify its validation behavior independently of calculateAge().
     */
    @Nested
    @DisplayName("validateDate() Direct Tests")
    class ValidateDateDirectTests {

        @Test
        @DisplayName("Valid past date (15/08/1998) does not throw any exception")
        void testValidateDate_ValidDate_DoesNotThrow() {
            // A well-formed date in the past should pass validation without errors
            assertDoesNotThrow(
                    () -> calculator.validateDate("15/08/1998"));
        }

        @Test
        @DisplayName("Future date via validateDate() directly throws IllegalArgumentException")
        void testValidateDate_FutureDate_DirectCall_ThrowsIllegalArgumentException() {
            // 16/03/2025 is one day after the fixed reference date 2025-03-15
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> calculator.validateDate("16/03/2025"));

            assertTrue(ex.getMessage().contains("future"),
                    "Exception message should mention 'future', but was: " + ex.getMessage());
        }
    }

    // =========================================================================
    // Nested Class 6: parseDateOfBirth() Direct Tests
    // =========================================================================

    /**
     * Tests that directly invoke {@link AgeCalculator#parseDateOfBirth(String)}
     * to verify parsing behavior independently of validation and calculation.
     */
    @Nested
    @DisplayName("parseDateOfBirth() Direct Tests")
    class ParseDateOfBirthDirectTests {

        @Test
        @DisplayName("Valid date (15/08/1998) returns correct LocalDate")
        void testParseDateOfBirth_ValidDate_ReturnsLocalDate() {
            // Parse a well-formed date and verify the resulting LocalDate
            LocalDate result = calculator.parseDateOfBirth("15/08/1998");
            assertEquals(LocalDate.of(1998, 8, 15), result,
                    "Parsed LocalDate should be 1998-08-15");
        }

        @Test
        @DisplayName("Null input to parseDateOfBirth() throws NullPointerException")
        void testParseDateOfBirth_NullInput_ThrowsNullPointerException() {
            // Null input should be rejected before any parsing is attempted
            NullPointerException ex = assertThrows(
                    NullPointerException.class,
                    () -> calculator.parseDateOfBirth(null));

            assertTrue(ex.getMessage().contains("null"),
                    "Exception message should mention 'null', but was: " + ex.getMessage());
        }

        @Test
        @DisplayName("Invalid date (31/02/2020) via parseDateOfBirth() throws DateTimeParseException")
        void testParseDateOfBirth_InvalidDate_ThrowsDateTimeParseException() {
            // February 31 is impossible — the strict formatter must reject it
            assertThrows(
                    DateTimeParseException.class,
                    () -> calculator.parseDateOfBirth("31/02/2020"));
        }
    }
}
