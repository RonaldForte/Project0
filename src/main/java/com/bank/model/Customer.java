package com.bank.model;

public class Customer {
  /*   CREATE TABLE customer (
    customer_id  SERIAL        PRIMARY KEY,
    username     VARCHAR(50)   NOT NULL UNIQUE,
    password     VARCHAR(100)  NOT NULL,
    first_name   VARCHAR(50)   NOT NULL,
    last_name    VARCHAR(50)   NOT NULL,
    address      VARCHAR(200),
    phone        VARCHAR(20),
    email        VARCHAR(100)  UNIQUE
  ); */
    private int customerId;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
    private String email;

    // No-args: empty object, filled in with setters
    public Customer() {}

    // No id: a new customer that isn't saved yet (the DB generates the id)
    public Customer(String username, String password, String firstName, String lastName,
                    String address, String phone, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    // All-args: a row read back from the DB
    public Customer(int customerId, String username, String password, String firstName,
                    String lastName, String address, String phone, String email) {
        this.customerId = customerId;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Password intentionally left out so it never shows up in console output or logs
    @Override
    public String toString() {
        return "Customer [customerId=" + customerId + ", username=" + username
                + ", firstName=" + firstName + ", lastName=" + lastName
                + ", address=" + address + ", phone=" + phone + ", email=" + email + "]";
    }

    // Identity = primary key. Same id means same row, even if other fields changed.
    @Override
    public int hashCode() {
        return Integer.hashCode(customerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Customer other = (Customer) obj;
        return customerId == other.customerId;
    }
}