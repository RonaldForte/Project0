package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bank.exception.InvalidCredentialsException;
import com.bank.model.Employee;
import com.bank.repository.EmployeeDao;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    private EmployeeDao employeeDao;

    @InjectMocks
    private EmployeeService employeeService;


    //Login ------------------------------

    @Test
    void login_correctPassword_returnsEmployee() {
        Employee stored = new Employee(1, "test", "test", "Test", "Employee");
        when(employeeDao.findByUsername("test")).thenReturn(Optional.of(stored));
        assertSame(stored, employeeService.login("test", "test"));
    }

    @Test
    void login_wrongPassword_throwsInvalidCredentials() {
        Employee stored = new Employee(1, "test", "test", "Test", "Employee");
        when(employeeDao.findByUsername("test")).thenReturn(Optional.of(stored));
        assertThrows(InvalidCredentialsException.class, () -> employeeService.login("test", "wrong"));
    }

    @Test
    void login_unknownUsername_throwsInvalidCredentials() {
        assertThrows(InvalidCredentialsException.class, () -> employeeService.login("nobody", "test"));
    }
    //------------------------------------
}