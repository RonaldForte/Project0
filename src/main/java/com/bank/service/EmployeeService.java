package com.bank.service;

import com.bank.exception.InvalidCredentialsException;
import com.bank.model.Employee;
import com.bank.repository.EmployeeDao;

public class EmployeeService {

    private final EmployeeDao employeeDao;

    public EmployeeService(EmployeeDao employeeDao){
        this.employeeDao = employeeDao;
    }

    //Login ------------------------------

    //Employee logs in (employees are seeded, never registered)
    public Employee login(String username, String password){
        return employeeDao.findByUsername(username)
                .filter(e -> e.getPassword().equals(password))
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password."));
    }
    //------------------------------------
}