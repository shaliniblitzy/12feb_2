package com.agecalculator;

import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Application entry point for the Age Calculator console application.
 *
 * <p>This class is responsible exclusively for console I/O operations:
 * reading the user's date of birth from standard input, delegating the
 * age calculation to {@link AgeCalculator}, and displaying the formatted
 * result produced by {@link AgeResult#toString()}.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * $ java com.agecalculator.Main
 * Enter your Date of Birth (DD/MM/YYYY): 15/08/1998
 * Your age is 27 years, 6 months, and 15 days.
 * }</pre>
 *
 * <h3>Error Handling</h3>
 * <p>The main method handles three categories of errors:</p>
 * <ol>
 *   <li>{@link DateTimeParseException} — malformed or invalid calendar dates
 *       (e.g., {@code 31/02/2020}, {@code abc}, {@code 1998-08-15})</li>
 *   <li>{@link IllegalArgumentException} — semantically invalid input such as
 *       future dates or empty/blank strings</li>
 *   <li>{@link Exception} — safety net for any unexpected runtime errors</li>
 * </ol>
 *
 * <p>All calculation logic resides in {@link AgeCalculator}. All result
 * formatting resides in {@link AgeResult#toString()}. This class performs
 * no date computation or formatting of its own.</p>
 *
 * @see AgeCalculator
 * @see AgeResult
 */
public class Main {

    /**
     * Console entry point that prompts the user for a date of birth,
     * computes the exact age, and displays the formatted result.
     *
     * <p>The method uses a try-with-resources block to ensure the
     * {@link Scanner} is properly closed regardless of whether the
     * operation succeeds or an exception is thrown.</p>
     *
     * <p>Expected input format: {@code DD/MM/YYYY} (e.g., {@code 15/08/1998}).</p>
     * <p>Expected output format: {@code Your age is X years, Y months, and Z days.}</p>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter your Date of Birth (DD/MM/YYYY): ");
            String dobString = scanner.nextLine();

            AgeCalculator calculator = new AgeCalculator();
            AgeResult result = calculator.calculateAge(dobString);

            System.out.println(result.toString());
        } catch (DateTimeParseException e) {
            System.out.println(
                    "Invalid date format. Please enter date in DD/MM/YYYY format.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}
