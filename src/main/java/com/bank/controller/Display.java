package com.bank.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.Transaction;

//Printing tables to the console
public class Display {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Display() {}

    public static void accounts(List<Account> accounts ) {
        if (accounts.isEmpty()) {
            System.out.println("  (no accounts)");
            return;
        }
        System.out.printf("  %-6s %-10s %14s  %-9s%n", "ID", "Type", "Balance", "Status");
        for (Account a : accounts) {
            System.out.printf("  %-6d %-10s %14s  %-9s%n",
                    a.getAccountId(), a.getAccountType(), money(a), a.getStatus());
        }
    }

    public static void customer(Customer c) {
        System.out.printf("  #%d  %s %s  (username: %s)%n",
                c.getCustomerId(), c.getFirstName(), c.getLastName(), c.getUsername());
        System.out.printf("      Address: %s | Phone: %s | Email: %s%n",
                orDash(c.getAddress()), orDash(c.getPhone()), orDash(c.getEmail()));
    }

    public static void transactions(List<Transaction> history) {
        if (history.isEmpty()) {
            System.out.println("  (no transactions)");
            return;
        }
        System.out.printf("  %-16s %-11s %6s %6s %14s%n", "Date", "Type", "From", "To", "Amount");
        for (Transaction t : history) {
            System.out.printf("  %-16s %-11s %6s %6s %14s%n",
                    t.getCreatedAt() == null ? "-" : t.getCreatedAt().format(DATE),
                    t.getType(),
                    t.getFromAccountId() == null ? "-" : t.getFromAccountId(),
                    t.getToAccountId() == null ? "-" : t.getToAccountId(),
                    String.format("$%,.2f", t.getAmount()));
        }
    }

    private static String money(Account a) {
        return String.format("$%,.2f", a.getBalance());
    }

    private static String orDash(String value) {
        return value == null ? "-" : value;
    }
}