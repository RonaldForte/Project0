package com.bank.repository;

import java.util.Optional;

import com.bank.model.Employee;

public interface EmployeeDao {
  Optional<Employee> findByUsername(String username);
}
