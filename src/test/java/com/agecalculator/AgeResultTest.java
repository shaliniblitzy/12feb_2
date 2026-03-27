package com.agecalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Comprehensive JUnit 5 unit tests for the {@link AgeResult} model class.
 *
 * <p>{@code AgeResult} is an immutable POJO that encapsulates computed age as
 * years, months, and days. This test class verifies the constructor, getter
 * methods, {@code toString()} output format, zero and edge value handling,
 * convenience methods ({@code getTotalMonths()}, {@code getTotalDays()}),
 * and immutability guarantees.</p>
 *
 * <p>No {@code @BeforeEach} is needed because {@code AgeResult} is an immutable
 * value object — each test creates its own instance directly.</p>
 *
 * <p>No Clock or date dependencies are required because {@code AgeResult} is a
 * pure value holder with no relation to system time.</p>
 */
@DisplayName("AgeResult Model Tests")
class AgeResultTest {

    // =========================================================================
    // Nested Test Class: Constructor and Getter Tests
    // =========================================================================

    @Nested
    @DisplayName("Constructor and Getter Tests")
    class ConstructorAndGetterTests {

        @Test
        @DisplayName("Constructor stores user example values (27, 6, 15) correctly via getters")
        void testAgeResult_ConstructorAndGetters() {
            // User's primary example: 27 years, 6 months, 15 days
            AgeResult result = new AgeResult(27, 6, 15);

            assertAll("Constructor stores values correctly",
                    () -> assertEquals(27, result.getYears(), "Years"),
                    () -> assertEquals(6, result.getMonths(), "Months"),
                    () -> assertEquals(15, result.getDays(), "Days")
            );
        }

        @Test
        @DisplayName("Constructor stores different values (5, 2, 14) correctly via getters")
        void testAgeResult_ConstructorWithDifferentValues() {
            AgeResult result = new AgeResult(5, 2, 14);

            assertAll("Constructor stores different values correctly",
                    () -> assertEquals(5, result.getYears(), "Years"),
                    () -> assertEquals(2, result.getMonths(), "Months"),
                    () -> assertEquals(14, result.getDays(), "Days")
            );
        }

        @Test
        @DisplayName("getYears() returns the correct year component")
        void testAgeResult_GetYears_ReturnsCorrectValue() {
            AgeResult result = new AgeResult(30, 3, 10);

            assertEquals(30, result.getYears());
        }

        @Test
        @DisplayName("getMonths() returns the correct month component")
        void testAgeResult_GetMonths_ReturnsCorrectValue() {
            AgeResult result = new AgeResult(30, 3, 10);

            assertEquals(3, result.getMonths());
        }

        @Test
        @DisplayName("getDays() returns the correct day component")
        void testAgeResult_GetDays_ReturnsCorrectValue() {
            AgeResult result = new AgeResult(30, 3, 10);

            assertEquals(10, result.getDays());
        }
    }

    // =========================================================================
    // Nested Test Class: toString() Format Verification
    // =========================================================================

    @Nested
    @DisplayName("toString() Format Verification")
    class ToStringFormatTests {

        @Test
        @DisplayName("toString() matches exact user-specified format for (27, 6, 15)")
        void testAgeResult_ToStringFormat() {
            // User-specified output format: "Your age is X years, Y months, and Z days."
            AgeResult result = new AgeResult(27, 6, 15);

            assertEquals("Your age is 27 years, 6 months, and 15 days.", result.toString());
        }

        @Test
        @DisplayName("toString() matches exact format for different values (5, 2, 14)")
        void testAgeResult_ToStringFormat_WithDifferentValues() {
            AgeResult result = new AgeResult(5, 2, 14);

            assertEquals("Your age is 5 years, 2 months, and 14 days.", result.toString());
        }

        @Test
        @DisplayName("toString() uses 'years', 'months', 'days' even for single-digit values — no pluralization logic")
        void testAgeResult_ToStringFormat_SingleDigitValues() {
            // The format does NOT singularize — always uses "years", "months", "days"
            AgeResult result = new AgeResult(1, 1, 1);

            assertEquals("Your age is 1 years, 1 months, and 1 days.", result.toString());
        }
    }

    // =========================================================================
    // Nested Test Class: Zero and Edge Value Tests
    // =========================================================================

    @Nested
    @DisplayName("Zero and Edge Value Tests")
    class ZeroAndEdgeValueTests {

        @Test
        @DisplayName("Zero age (born today): all components are 0, toString() reflects zeros")
        void testAgeResult_ZeroAge() {
            AgeResult result = new AgeResult(0, 0, 0);

            assertAll("Zero age (born today)",
                    () -> assertEquals(0, result.getYears()),
                    () -> assertEquals(0, result.getMonths()),
                    () -> assertEquals(0, result.getDays())
            );
            assertEquals("Your age is 0 years, 0 months, and 0 days.", result.toString());
        }

        @Test
        @DisplayName("Single unit: exactly 1 year, 0 months, 0 days")
        void testAgeResult_SingleUnit_OneYear() {
            AgeResult result = new AgeResult(1, 0, 0);

            assertEquals(1, result.getYears());
            assertEquals("Your age is 1 years, 0 months, and 0 days.", result.toString());
        }

        @Test
        @DisplayName("Single unit: 0 years, 1 month, 0 days")
        void testAgeResult_SingleUnit_OneMonth() {
            AgeResult result = new AgeResult(0, 1, 0);

            assertEquals("Your age is 0 years, 1 months, and 0 days.", result.toString());
        }

        @Test
        @DisplayName("Single unit: 0 years, 0 months, 1 day")
        void testAgeResult_SingleUnit_OneDay() {
            AgeResult result = new AgeResult(0, 0, 1);

            assertEquals("Your age is 0 years, 0 months, and 1 days.", result.toString());
        }

        @Test
        @DisplayName("Large values: extreme age of 125 years, 11 months, 30 days")
        void testAgeResult_LargeValues() {
            AgeResult result = new AgeResult(125, 11, 30);

            assertAll("Large values",
                    () -> assertEquals(125, result.getYears()),
                    () -> assertEquals(11, result.getMonths()),
                    () -> assertEquals(30, result.getDays())
            );
            assertEquals("Your age is 125 years, 11 months, and 30 days.", result.toString());
        }
    }

    // =========================================================================
    // Nested Test Class: getTotalMonths() Tests
    // =========================================================================

    @Nested
    @DisplayName("getTotalMonths() Tests")
    class GetTotalMonthsTests {

        @Test
        @DisplayName("getTotalMonths() computes (27*12)+6 = 330 for user example values")
        void testAgeResult_GetTotalMonths() {
            AgeResult result = new AgeResult(27, 6, 15);

            // Formula: (years * 12) + months = (27 * 12) + 6 = 324 + 6 = 330
            assertEquals(330, result.getTotalMonths());
        }

        @Test
        @DisplayName("getTotalMonths() returns 0 for zero age (born today)")
        void testAgeResult_GetTotalMonths_ZeroAge() {
            AgeResult result = new AgeResult(0, 0, 0);

            assertEquals(0, result.getTotalMonths());
        }

        @Test
        @DisplayName("getTotalMonths() computes correctly when only years are present (no months)")
        void testAgeResult_GetTotalMonths_OnlyYears() {
            AgeResult result = new AgeResult(5, 0, 10);

            // Formula: (5 * 12) + 0 = 60
            assertEquals(60, result.getTotalMonths());
        }

        @Test
        @DisplayName("getTotalMonths() computes correctly when only months are present (no years)")
        void testAgeResult_GetTotalMonths_OnlyMonths() {
            AgeResult result = new AgeResult(0, 7, 10);

            // Formula: (0 * 12) + 7 = 7
            assertEquals(7, result.getTotalMonths());
        }
    }

    // =========================================================================
    // Nested Test Class: getTotalDays() Tests
    // =========================================================================

    @Nested
    @DisplayName("getTotalDays() Tests")
    class GetTotalDaysTests {

        @Test
        @DisplayName("getTotalDays() computes approximate (27*365)+(6*30)+15 = 10050 for user example")
        void testAgeResult_GetTotalDays() {
            AgeResult result = new AgeResult(27, 6, 15);

            // Approximation formula: (years * 365L) + (months * 30L) + days
            // = (27 * 365) + (6 * 30) + 15
            // = 9855 + 180 + 15
            // = 10050
            assertEquals(10050L, result.getTotalDays());
        }

        @Test
        @DisplayName("getTotalDays() returns 0 for zero age (born today)")
        void testAgeResult_GetTotalDays_ZeroAge() {
            AgeResult result = new AgeResult(0, 0, 0);

            assertEquals(0L, result.getTotalDays());
        }

        @Test
        @DisplayName("getTotalDays() computes approximate total for extreme age (125, 11, 30)")
        void testAgeResult_GetTotalDays_LargeAge() {
            AgeResult result = new AgeResult(125, 11, 30);

            // Approximation formula: (years * 365L) + (months * 30L) + days
            // = (125 * 365) + (11 * 30) + 30
            // = 45625 + 330 + 30
            // = 45985
            assertEquals(45985L, result.getTotalDays());
        }
    }

    // =========================================================================
    // Nested Test Class: Immutability Verification
    // =========================================================================

    @Nested
    @DisplayName("Immutability Verification")
    class ImmutabilityTests {

        @Test
        @DisplayName("Multiple getter calls return the same values, verifying immutability")
        void testAgeResult_IsImmutable_MultipleGetterCalls() {
            AgeResult result = new AgeResult(27, 6, 15);

            // Calling getters multiple times must return identical values
            // because AgeResult is immutable (private final fields, no setters)
            assertEquals(result.getYears(), result.getYears(),
                    "getYears() should return consistent value across multiple calls");
            assertEquals(result.getMonths(), result.getMonths(),
                    "getMonths() should return consistent value across multiple calls");
            assertEquals(result.getDays(), result.getDays(),
                    "getDays() should return consistent value across multiple calls");

            // Also verify toString() consistency
            assertEquals(result.toString(), result.toString(),
                    "toString() should return consistent value across multiple calls");
        }
    }
}
