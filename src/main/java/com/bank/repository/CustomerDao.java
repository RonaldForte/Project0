package com.bank.repository;

import java.util.List;
import java.util.Optional;

import com.bank.model.Customer;

// CRUD
public interface CustomerDao {
  // Create
  Customer create(Customer customer);
  

  // Read
  Optional<Customer> findByUsername(String username); //Optional: May not return a customer

  List<Customer> findAllCustomers(); // May not have any customers but lists handles it already (can be empty) so don't need Optional

  Optional<Customer> findById(int customerId); //Optional: May not return a customer

  //Update: Not apart of the req

  //Delete: Not apart of the req

}
