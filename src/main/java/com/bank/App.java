package com.bank;

import java.util.Scanner;

import com.bank.controller.CustomerController;
import com.bank.controller.EmployeeController;
import com.bank.controller.InputHelper;
import com.bank.controller.MainController;
import com.bank.repository.AccountDao;
import com.bank.repository.AccountDaoImpl;
import com.bank.repository.AccountOwnerDao;
import com.bank.repository.AccountOwnerDaoImpl;
import com.bank.repository.CustomerDao;
import com.bank.repository.CustomerDaoImpl;
import com.bank.repository.EmployeeDao;
import com.bank.repository.EmployeeDaoImpl;
import com.bank.repository.TransactionDao;
import com.bank.repository.TransactionDaoImpl;
import com.bank.service.AccountService;
import com.bank.service.CustomerService;
import com.bank.service.EmployeeService;

public class App {
    public static void main(String[] args) {
        //Repository layer: the only place DaoImpls are created
        CustomerDao customerDao = new CustomerDaoImpl();
        EmployeeDao employeeDao = new EmployeeDaoImpl();
        AccountDao accountDao = new AccountDaoImpl();
        AccountOwnerDao accountOwnerDao = new AccountOwnerDaoImpl();
        TransactionDao transactionDao = new TransactionDaoImpl();

        //Service layer: DAOs handed in (dependency injection)
        CustomerService customerService = new CustomerService(customerDao);
        EmployeeService employeeService = new EmployeeService(employeeDao);
        AccountService accountService = new AccountService(accountDao, accountOwnerDao, transactionDao, customerDao);

        //Controller layer: one Scanner for the whole app
        try (Scanner scanner = new Scanner(System.in)) {
            InputHelper input = new InputHelper(scanner);
            CustomerController customerController = new CustomerController(input, accountService);
            EmployeeController employeeController = new EmployeeController(input, customerService, accountService);
            new MainController(input, customerService, employeeService, customerController, employeeController).run();
        }
    }
}