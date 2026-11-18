package com.bank.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.bank.model.Employee;
import com.bank.util.ConnectionUtil;


public class EmployeeDaoImpl implements EmployeeDao {
  @Override
  public Optional<Employee> findByUsername(String username) {
    String sql = "SELECT employee_id, username, password, first_name, last_name FROM employee WHERE username = ?";

    try(Connection conn = ConnectionUtil.getConnection(); // Open the connection
      PreparedStatement ps = conn.prepareStatement(sql)) { // Set the query which we called the connection for
        ps.setString(1, username); // Fill the fields

        try (ResultSet rs = ps.executeQuery()) { // The result set of what happens after we get the query. Below is if it executes
          if(rs.next()) { //Step through the table
            return Optional.of(new Employee( //return an employee object and fill the fields with the data in the db below
              rs.getInt("employee_id"),
              rs.getString("username"),
              rs.getString("password"),
              rs.getString("first_name"),
              rs.getString("last_name")));
          }
        } 
    } catch (SQLException e) {
        throw new RuntimeException("Error looking up employee " + username, e);
    }
    return Optional.empty();
  }


}
