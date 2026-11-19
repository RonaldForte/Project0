package com.bank.controller;

import java.math.BigDecimal;

import com.bank.exception.BankException;
import com.bank.model.Account;
import com.bank.model.AccountType;
import com.bank.model.Customer;
import com.bank.service.AccountService;

public class CustomerController {

    private final InputHelper input;
    private final AccountService accountService;

    public CustomerController(InputHelper input, AccountService accountService) {
        this.input = input;
        this.accountService = accountService;
    }

    //Customer menu ----------------------

    public void run(Customer customer) {
        System.out.println();
        System.out.println("Welcome, " + customer.getFirstName() + "!");
        while (true){
            System.out.println();
            System.out.println("--- Customer Menu ---");
            System.out.println("1. View my accounts");
            System.out.println("2. Apply for a new account");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Transfer between my accounts");
            System.out.println("6. View account history");
            System.out.println("0. Log out");

            String choice = input.readLine("Choose: ");
            if (choice.equals("0")) {
                System.out.println("Logged out.");
                return;
            }
            try {
                switch (choice) {
                    case "1" -> viewAccounts(customer);
                    case "2" -> apply(customer);
                    case "3" -> deposit(customer);
                    case "4" -> withdraw(customer);
                    case "5" -> transfer(customer);
                    case "6" -> history(customer);
                    default -> System.out.println("Invalid option.");
                }
            } catch (BankException e) {
                System.out.println("[!] " + e.getMessage());
            }
        }
    }
    //------------------------------------


    //Actions-----------------------------

    private void viewAccounts(Customer customer) {
        Display.accounts(accountService.getMyAccounts(customer.getCustomerId()));
    }

    private void apply(Customer customer ) {
        System.out.println("1. Checking");
        System.out.println("2. Savings");
        String choice = input.readLine("Account type: ");
        AccountType type;
        if (choice.equals("1")) {
            type = AccountType.CHECKING;
        } else if (choice.equals("2")) {
            type = AccountType.SAVINGS;
        } else {
            System.out.println("Invalid option.");
            return;
        }
        Account account = accountService.apply(customer.getCustomerId(), type);
        System.out.println("Application submitted! Account #" + account.getAccountId()
                + " is PENDING until an employee reviews it.");
    }

    private void deposit(Customer customer) {
        int accountId = input.readInt("Account id: ");
        BigDecimal amount = input.readAmount("Amount: $");
        Account account = accountService.deposit(customer.getCustomerId(), accountId, amount);
        System.out.printf("Deposited. New balance: $%,.2f%n", account.getBalance());
    }

    private void withdraw(Customer customer ) {
        int accountId = input.readInt("Account id: ");
        BigDecimal amount = input.readAmount("Amount: $");
        Account account = accountService.withdraw(customer.getCustomerId(), accountId, amount);
        System.out.printf("Withdrawn. New balance: $%,.2f%n", account.getBalance());
    }

    private void transfer(Customer customer) {
        int fromId = input.readInt("From account id: ");
        int toId = input.readInt("To account id: ");
        BigDecimal amount = input.readAmount("Amount: $");
        accountService.transfer(customer.getCustomerId(), fromId, toId, amount);
        System.out.printf("Transferred $%,.2f from #%d to #%d.%n", amount, fromId, toId);
    }

    private void history(Customer customer ) {
        int accountId = input.readInt("Account id: ");
        Display.transactions(accountService.getHistory(customer.getCustomerId(), accountId));
    }
    //------------------------------------
}