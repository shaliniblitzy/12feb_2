package com.agecalculator;

import java.util.Objects;

/**
 * Immutable model class encapsulating a computed age result.
 *
 * <p>An {@code AgeResult} stores the exact age broken down into complete years,
 * complete months (beyond the years), and remaining days (beyond the months).
 * These values correspond directly to the components returned by
 * {@link java.time.Period#getYears()}, {@link java.time.Period#getMonths()},
 * and {@link java.time.Period#getDays()} when computing the period between
 * a date of birth and the current date.</p>
 *
 * <p>This class is immutable and thread-safe. All fields are {@code private final}
 * with no setter methods. Instances are created exclusively through the constructor.</p>
 *
 * <p>The {@link #toString()} method produces output in the exact format specified
 * by the user requirement:
 * {@code Your age is X years, Y months, and Z days.}</p>
 *
 * @see java.time.Period
 * @see java.time.LocalDate
 */
public class AgeResult {

    /** The number of complete years in the computed age. */
    private final int years;

    /** The number of complete months beyond the years in the computed age. */
    private final int months;

    /** The number of remaining days beyond the months in the computed age. */
    private final int days;

    /**
     * Constructs an {@code AgeResult} with the specified years, months, and days.
     *
     * <p>The values are accepted as-is from {@link java.time.Period#getYears()},
     * {@link java.time.Period#getMonths()}, and {@link java.time.Period#getDays()}.
     * When computing age forward (DOB in the past), these values are always
     * non-negative.</p>
     *
     * @param years  the number of complete years in the age
     * @param months the number of complete months beyond the years
     * @param days   the number of remaining days beyond the months
     */
    public AgeResult(int years, int months, int days) {
        this.years = years;
        this.months = months;
        this.days = days;
    }

    /**
     * Returns the number of complete years in the computed age.
     *
     * @return the years component of this age result
     */
    public int getYears() {
        return years;
    }

    /**
     * Returns the number of complete months beyond the years in the computed age.
     *
     * @return the months component of this age result (0–11)
     */
    public int getMonths() {
        return months;
    }

    /**
     * Returns the number of remaining days beyond the months in the computed age.
     *
     * @return the days component of this age result (0–30)
     */
    public int getDays() {
        return days;
    }

    /**
     * Returns a formatted string representation of this age result.
     *
     * <p>The format is exactly:
     * {@code Your age is X years, Y months, and Z days.}</p>
     *
     * <p>Examples:</p>
     * <ul>
     *   <li>{@code new AgeResult(27, 6, 15).toString()} returns
     *       {@code "Your age is 27 years, 6 months, and 15 days."}</li>
     *   <li>{@code new AgeResult(0, 0, 0).toString()} returns
     *       {@code "Your age is 0 years, 0 months, and 0 days."}</li>
     *   <li>{@code new AgeResult(1, 0, 0).toString()} returns
     *       {@code "Your age is 1 years, 0 months, and 0 days."}</li>
     * </ul>
     *
     * @return the formatted age string matching the user-specified output format
     */
    @Override
    public String toString() {
        return String.format("Your age is %d years, %d months, and %d days.", years, months, days);
    }

    /**
     * Returns the total age expressed in complete months, ignoring remaining days.
     *
     * <p>This is a convenience method that computes {@code (years * 12) + months}.
     * The remaining days beyond the last complete month are not included.</p>
     *
     * @return the total number of complete months in this age result
     */
    public int getTotalMonths() {
        return (years * 12) + months;
    }

    /**
     * Returns an approximate total age in days.
     *
     * <p><strong>Note:</strong> This is an approximation because calendar months
     * have varying lengths (28–31 days). The calculation uses a simplified formula:
     * {@code (years * 365) + (months * 30) + days}.</p>
     *
     * <p>For precise day counts, use
     * {@link java.time.temporal.ChronoUnit#DAYS} with the original
     * {@link java.time.LocalDate} values instead.</p>
     *
     * @return the estimated total number of days in this age result
     */
    public long getTotalDays() {
        return (years * 365L) + (months * 30L) + days;
    }

    /**
     * Indicates whether some other object is "equal to" this {@code AgeResult}.
     *
     * <p>Two {@code AgeResult} instances are equal if and only if they have
     * the same years, months, and days values.</p>
     *
     * @param o the reference object with which to compare
     * @return {@code true} if this object is equal to the argument; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AgeResult ageResult = (AgeResult) o;
        return years == ageResult.years
                && months == ageResult.months
                && days == ageResult.days;
    }

    /**
     * Returns a hash code value for this {@code AgeResult}.
     *
     * <p>The hash code is computed from the years, months, and days fields
     * using {@link Objects#hash(Object...)}.</p>
     *
     * @return a hash code value consistent with {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return Objects.hash(years, months, days);
    }
}
