package com.vectorbank.cli;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Robust console input reader handling type parsing and validation.
 */
public class InputReader {
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a valid integer.");
            }
        }
    }

    public BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                BigDecimal val = new BigDecimal(line);
                if (val.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("❌ Amount cannot be negative. Please enter a positive amount.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid amount. Please enter a valid decimal number (e.g. 150.50).");
            }
        }
    }

    public String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("❌ Input cannot be empty. Please try again.");
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return LocalDate.parse(line, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format. Please use YYYY-MM-DD (e.g., 2000-01-15).");
            }
        }
    }

    public static String capitalizeWords(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        String[] parts = input.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)))
                  .append(part.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
