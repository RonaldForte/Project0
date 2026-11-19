package com.bank.service;

import java.util.List;

import com.bank.exception.InvalidCredentialsException;
import com.bank.exception.InvalidInputException;
import com.bank.exception.UsernameTakenException;
import com.bank.model.Customer;
import com.bank.repository.CustomerDao;

public class CustomerService {

    private final CustomerDao customerDao;

    //DAO handed in (dependency injection) so tests can pass a Mockito mock
    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    //Register ---------------------------

    //Customer registers: required fields filled, username not taken
    public Customer register(Customer customer){
        if (isBlank(customer.getUsername()) || isBlank(customer.getPassword()) || isBlank(customer.getFirstName()) || isBlank(customer.getLastName())) {
            throw new InvalidInputException("Username, password, first name and last name are required.");
        }
        if (customerDao.findByUsername(customer.getUsername()).isPresent()) {
            throw new UsernameTakenException("That username is already taken.");
        }
        return customerDao.create(customer);
    }
    //------------------------------------


    //Login ------------------------------

    //Customer logs in: username exists and password matches (one error for both)
    public Customer login(String username, String password) {
        return customerDao.findByUsername(username)
                .filter(c -> c.getPassword().equals(password))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password."));
    }
    //------------------------------------


    //Employee features ------------------

    //Employee sees all customers' personal information
    public List<Customer> getAllCustomers(){
        return customerDao.findAllCustomers();
    }
    //------------------------------------


    //Helper -----------------------------

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
    //------------------------------------
}