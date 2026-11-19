package com.bank.controller;

import java.math.BigDecimal;
import java.util.Scanner;

//All console input goes through here: always nextLine(), then convert, re-asking on bad input
public class InputHelper {

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    //Keeps asking until something is typed
    public String readRequired(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field is required.");
        }
    }

    // Blank becomes null (so empty emails don't break the UNIQUE constraint)
    public String readOptional(String prompt) {
        String value = readLine(prompt);
        return value.isEmpty() ? null : value;
    }

    public int readInt(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    public BigDecimal readAmount(String prompt) {
        while (true) {
            String value = readLine(prompt).replace("$", "").replace(",", "");
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                System.out.println("Please enter an amount like 25 or 25.50.");
            }
        }
    }
}