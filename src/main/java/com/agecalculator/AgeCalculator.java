package com.agecalculator;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Objects;

/**
 * Core business-logic class that computes a user's exact age (years, months,
 * and days) from a Date of Birth (DOB) string in {@code DD/MM/YYYY} format.
 *
 * <p>This class leverages the {@code java.time} API exclusively:
 * <ul>
 *   <li>{@link LocalDate} for date representation and parsing</li>
 *   <li>{@link Period} for date arithmetic (difference between two dates)</li>
 *   <li>{@link DateTimeFormatter} with {@link ResolverStyle#STRICT} for
 *       rigorous input validation that rejects impossible calendar dates such
 *       as {@code 31/02/2020} or {@code 29/02/2001}</li>
 *   <li>{@link Clock} for injectable time source, enabling deterministic
 *       testing without reliance on the system clock</li>
 * </ul>
 *
 * <h3>Exception Hierarchy</h3>
 * <p>All public methods follow a consistent exception hierarchy:</p>
 * <ol>
 *   <li>{@link NullPointerException} — thrown first for {@code null} inputs
 *       (via {@link Objects#requireNonNull(Object, String)})</li>
 *   <li>{@link DateTimeParseException} — thrown by {@link DateTimeFormatter}
 *       for malformed or invalid calendar dates</li>
 *   <li>{@link IllegalArgumentException} — thrown for syntactically valid
 *       dates that fall in the future</li>
 * </ol>
 *
 * <h3>Clock Injection Pattern</h3>
 * <p>The parameterized constructor {@link #AgeCalculator(Clock)} accepts a
 * {@link Clock} instance, enabling test methods to supply
 * {@link Clock#fixed(java.time.Instant, java.time.ZoneId)} for fully
 * deterministic results. The default constructor
 * {@link #AgeCalculator()} uses {@link Clock#systemDefaultZone()}.</p>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * AgeCalculator calculator = new AgeCalculator();
 * AgeResult result = calculator.calculateAge("15/08/1998");
 * System.out.println(result);
 * // Output: Your age is 27 years, 6 months, and 15 days.
 * }</pre>
 *
 * @see AgeResult
 * @see java.time.Period
 * @see java.time.format.ResolverStyle#STRICT
 */
public class AgeCalculator {

    /**
     * Strict date formatter for the {@code DD/MM/YYYY} pattern.
     *
     * <p><strong>CRITICAL:</strong> The pattern uses {@code uuuu} (proleptic year)
     * rather than {@code yyyy} (year-of-era). When {@link ResolverStyle#STRICT}
     * is active, {@code yyyy} requires an accompanying era field ({@code G}),
     * which is absent in the {@code DD/MM/YYYY} format. Using {@code uuuu}
     * avoids this requirement while retaining all strict validation benefits,
     * including rejection of impossible calendar dates such as February 31
     * or February 29 on non-leap years.</p>
     */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Injectable time source used by all date-relative operations.
     *
     * <p>Defaults to {@link Clock#systemDefaultZone()} when created via the
     * default constructor. Test code can inject a fixed clock for deterministic
     * assertions.</p>
     */
    private final Clock clock;

    /**
     * Constructs an {@code AgeCalculator} using the system default time zone
     * clock ({@link Clock#systemDefaultZone()}).
     *
     * <p>Suitable for production use where the "current date" should reflect
     * the actual system time.</p>
     */
    public AgeCalculator() {
        this.clock = Clock.systemDefaultZone();
    }

    /**
     * Constructs an {@code AgeCalculator} with the specified {@link Clock}.
     *
     * <p>This constructor enables deterministic testing by accepting a fixed
     * clock (e.g., {@code Clock.fixed(Instant, ZoneId)}) that controls what
     * {@link LocalDate#now(Clock)} returns.</p>
     *
     * @param clock the time source to use for determining the current date;
     *              must not be {@code null}
     * @throws NullPointerException if {@code clock} is {@code null}
     */
    public AgeCalculator(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "Clock must not be null");
    }

    /**
     * Parses a date-of-birth string in {@code DD/MM/YYYY} format into a
     * {@link LocalDate}.
     *
     * <p>The method applies strict validation via
     * {@link ResolverStyle#STRICT}, meaning:</p>
     * <ul>
     *   <li>Invalid calendar dates (e.g., {@code 31/02/2020},
     *       {@code 29/02/2001}) are rejected</li>
     *   <li>Malformed formats (e.g., {@code 1998-08-15}, {@code 15/8/98},
     *       {@code abc}) are rejected</li>
     *   <li>Partial dates (e.g., {@code 15/08}) are rejected</li>
     * </ul>
     *
     * @param dobString the date-of-birth string in {@code DD/MM/YYYY} format;
     *                  must not be {@code null}, empty, or blank
     * @return the parsed {@link LocalDate} representing the date of birth
     * @throws NullPointerException    if {@code dobString} is {@code null}
     * @throws IllegalArgumentException if {@code dobString} is empty or blank
     * @throws DateTimeParseException  if {@code dobString} does not conform to
     *                                 the {@code DD/MM/YYYY} format or
     *                                 represents an invalid calendar date
     */
    public LocalDate parseDateOfBirth(String dobString) {
        Objects.requireNonNull(dobString, "Date of birth string must not be null");

        if (dobString.isBlank()) {
            throw new IllegalArgumentException(
                    "Date of birth string must not be empty or blank");
        }

        return LocalDate.parse(dobString, FORMATTER);
    }

    /**
     * Validates that the given date-of-birth string is a well-formed, valid
     * calendar date that does not fall in the future.
     *
     * <p>This method combines parsing (via {@link #parseDateOfBirth(String)})
     * and business-rule validation (date must not be after the current date)
     * in a single call.</p>
     *
     * @param dobString the date-of-birth string in {@code DD/MM/YYYY} format;
     *                  must not be {@code null}, empty, or blank
     * @throws NullPointerException     if {@code dobString} is {@code null}
     * @throws IllegalArgumentException if {@code dobString} is empty/blank or
     *                                  represents a date in the future
     * @throws DateTimeParseException   if {@code dobString} does not conform to
     *                                  the expected format or is an invalid date
     */
    public void validateDate(String dobString) {
        final LocalDate dob = parseDateOfBirth(dobString);
        final LocalDate currentDate = LocalDate.now(clock);

        if (dob.isAfter(currentDate)) {
            throw new IllegalArgumentException(
                    "Date of birth cannot be in the future.");
        }
    }

    /**
     * Calculates the exact age from a date-of-birth string in
     * {@code DD/MM/YYYY} format.
     *
     * <p>The age is computed as the {@link Period} between the parsed DOB and
     * the current date (as determined by the injected {@link Clock}), then
     * decomposed into years, months, and days encapsulated in an
     * {@link AgeResult}.</p>
     *
     * @param dobString the date-of-birth string in {@code DD/MM/YYYY} format;
     *                  must not be {@code null}, empty, or blank
     * @return an {@link AgeResult} containing the exact age in years, months,
     *         and days
     * @throws NullPointerException     if {@code dobString} is {@code null}
     * @throws IllegalArgumentException if {@code dobString} is empty/blank or
     *                                  represents a date in the future
     * @throws DateTimeParseException   if {@code dobString} does not conform to
     *                                  the expected format or is an invalid date
     */
    public AgeResult calculateAge(String dobString) {
        final LocalDate dob = parseDateOfBirth(dobString);
        final LocalDate currentDate = LocalDate.now(clock);

        if (dob.isAfter(currentDate)) {
            throw new IllegalArgumentException(
                    "Date of birth cannot be in the future.");
        }

        final Period period = Period.between(dob, currentDate);
        return new AgeResult(period.getYears(), period.getMonths(), period.getDays());
    }

    /**
     * Calculates the exact age from a {@link LocalDate} date of birth.
     *
     * <p>This overloaded method accepts a pre-parsed {@link LocalDate} directly,
     * bypassing the string-parsing step. It is useful when the caller already
     * has a {@link LocalDate} instance.</p>
     *
     * @param dob the date of birth as a {@link LocalDate}; must not be
     *            {@code null} and must not be after the current date
     * @return an {@link AgeResult} containing the exact age in years, months,
     *         and days
     * @throws NullPointerException     if {@code dob} is {@code null}
     * @throws IllegalArgumentException if {@code dob} is in the future
     */
    public AgeResult calculateAge(LocalDate dob) {
        Objects.requireNonNull(dob, "Date of birth must not be null");

        final LocalDate currentDate = LocalDate.now(clock);

        if (dob.isAfter(currentDate)) {
            throw new IllegalArgumentException(
                    "Date of birth cannot be in the future.");
        }

        final Period period = Period.between(dob, currentDate);
        return new AgeResult(period.getYears(), period.getMonths(), period.getDays());
    }
}
