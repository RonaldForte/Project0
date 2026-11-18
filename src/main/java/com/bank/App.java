package com.bank;

import com.bank.model.Customer;
import com.bank.repository.CustomerDao;
import com.bank.repository.CustomerDaoImpl;

public class App {
    public static void main(String[] args) {
        CustomerDao dao = new CustomerDaoImpl();

        Customer saved = dao.create(new Customer("ronald", "pass123", "Ronald", "Forte",
                                                 "Brooklyn", "555-1234", "ron@test.com"));
        System.out.println(saved);                          // real id, not 0?
        System.out.println(dao.findByUsername("ronald"));   // Optional[Customer [...]]
        System.out.println(dao.findByUsername("nobody"));   // Optional.empty
        System.out.println(dao.findAllCustomers());
    }
}