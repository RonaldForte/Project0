package com.bank.model;

public class Employee {
  /*   CREATE TABLE employee (
    employee_id  SERIAL        PRIMARY KEY,
    username     VARCHAR(50)   NOT NULL UNIQUE,
    password     VARCHAR(100)  NOT NULL,
    first_name   VARCHAR(50)   NOT NULL,
    last_name    VARCHAR(50)   NOT NULL
  ); */

  private int employeeId;
  private String username;
  private String password;
  private String firstName;
  private String lastName;

  // Constructors: no args, no id, all args
  public Employee() {}
  public Employee(String username, String password, String firstName, String lastName) {
    this.username = username;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
  }
  public Employee(int employeeId, String username, String password, String firstName, String lastName) {
    this.employeeId = employeeId;
    this.username = username;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public int getEmployeeId() {
    return employeeId;
  }
  public void setEmployeeId(int employeeId) {
    this.employeeId = employeeId;
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
  @Override
  public String toString() {
    return "Employee [employeeId=" + employeeId + ", username=" + username + ", firstName=" + firstName
        + ", lastName=" + lastName + "]";
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + employeeId;
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Employee other = (Employee) obj;
    if (employeeId != other.employeeId)
      return false;
    return true;
  }

}