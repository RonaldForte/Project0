package com.bank.controller;

import java.util.List;

import com.bank.exception.BankException;
import com.bank.model.Account;
import com.bank.model.AccountOwner;
import com.bank.model.Customer;
import com.bank.model.Employee;
import com.bank.service.AccountService;
import com.bank.service.CustomerService;

public class EmployeeController {

    private final InputHelper input;
    private final CustomerService customerService;
    private final AccountService accountService;

    public EmployeeController(InputHelper input, CustomerService customerService, AccountService accountService) {
        this.input = input;
        this.customerService = customerService;
        this.accountService = accountService;
    }

    //Employee menu ----------------------

    public void run(Employee employee ) {
        System.out.println();
        System.out.println("Welcome, " + employee.getFirstName() + " (employee).");
        while (true) {
            System.out.println();
            System.out.println("--- Employee Menu ---");
            System.out.println("1. View all customers and their accounts");
            System.out.println("2. View all accounts");
            System.out.println("3. View pending applications");
            System.out.println("4. Approve an application");
            System.out.println("5. Deny an application");
            System.out.println("6. View owners of an account");
            System.out.println("0. Log out");

            String choice = input.readLine("Choose: ");
            if (choice.equals("0")) {
                System.out.println("Logged out.");
                return;
            }
            try {
                switch (choice) {
                    case "1" -> viewCustomers();
                    case "2" -> Display.accounts(accountService.getAllAccounts());
                    case "3" -> Display.accounts(accountService.getPendingAccounts());
                    case "4" -> approve(employee);
                    case "5" -> deny(employee);
                    case "6" -> viewOwners();
                    default -> System.out.println("Invalid option.");
                }
            } catch (BankException e) {
                System.out.println("[!] " + e.getMessage());
            }
        }
    }
    
    //------------------------------------


    //Actions ----------------------------

    //Personal info + accounts + balances for every customer
    private void viewCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("  (no customers)");
            return;
        }
        for (Customer c : customers) {
            System.out.println();
            Display.customer(c);
            List<Account> accounts = accountService.getMyAccounts(c.getCustomerId());
            Display.accounts(accounts);
        }
    }

    private void approve(Employee employee) {
        int accountId = input.readInt("Account id to approve: ");
        accountService.approve(employee.getEmployeeId(), accountId);
        System.out.println("Account #" + accountId + " approved.");
    }

    private void deny(Employee employee) {
        int accountId = input.readInt("Account id to deny: ");
        accountService.deny(employee.getEmployeeId(), accountId);
        System.out.println("Account #" + accountId + " denied.");
    }

    private void viewOwners() {
        int accountId = input.readInt("Account id: ");
        List<AccountOwner> owners = accountService.getOwners(accountId);
        if (owners.isEmpty()) {
            System.out.println("  (no owners found)");
            return;
        }
        for (AccountOwner o : owners) {
            System.out.println("  Customer #" + o.getCustomerId());
        }
    }
    //------------------------------------
}