package com.bank.controller;

import com.bank.exception.BankException;
import com.bank.model.Customer;
import com.bank.model.Employee;
import com.bank.service.CustomerService;
import com.bank.service.EmployeeService;

public class MainController {

    private final InputHelper input;
    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final CustomerController customerController;
    private final EmployeeController employeeController;

    public MainController(InputHelper input, CustomerService customerService, EmployeeService employeeService,
                          CustomerController customerController, EmployeeController employeeController) {
        this.input = input;
        this.customerService = customerService;
        this.employeeService = employeeService;
        this.customerController = customerController;
        this.employeeController = employeeController;
    }

    //Main menu --------------------------

    public void run() {
        System.out.println("=== Welcome to the Bank ===");
        while (true) {
            System.out.println();
            System.out.println("1. Customer login");
            System.out.println("2. Register as a customer");
            System.out.println("3. Employee login");
            System.out.println("0. Exit");

            switch (input.readLine("Choose: ")) {
                case "1" -> customerLogin();
                case "2" -> register();
                case "3" -> employeeLogin();
                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
    //------------------------------------


    //Actions ----------------------------

    private void customerLogin() {
        String username = input.readRequired("Username: ");
        String password = input.readRequired("Password: ");
        try {
            Customer customer = customerService.login(username, password);
            customerController.run(customer);
        } catch (BankException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }

    private void register() {
        System.out.println("--- Register ---");
        Customer customer = new Customer(
                input.readRequired("Username: "),
                input.readRequired("Password: "),
                input.readRequired("First name: "),
                input.readRequired("Last name: "),
                input.readOptional("Address (optional): "),
                input.readOptional("Phone (optional): "),
                input.readOptional("Email (optional): "));
        try {
            Customer saved = customerService.register(customer);
            System.out.println("Registered! Your customer id is " + saved.getCustomerId() + ". You can log in now.");
        } catch (BankException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }

    private void employeeLogin() {
        String username = input.readRequired("Employee username: ");
        String password = input.readRequired("Password: ");
        try {
            Employee employee = employeeService.login(username, password);
            employeeController.run(employee);
        } catch (BankException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }
    //------------------------------------
}