package com.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bank.exception.InvalidCredentialsException;
import com.bank.exception.InvalidInputException;
import com.bank.exception.UsernameTakenException;
import com.bank.model.Customer;
import com.bank.repository.CustomerDao;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerDao customerDao;

    @InjectMocks
    private CustomerService customerService;

    //Helper: a real customer that isn't saved yet
    private Customer newCustomer(String username) {
        return new Customer(username, "pw", "Ronald", "Forte", null, null, null);
    }


    //Register ---------------------------

    @Test
    void register_newUsername_savesCustomer() {
        //Arrange
        Customer customer = newCustomer("ronald");
        Customer saved = new Customer(1, "ronald", "pw", "Ronald", "Forte", null, null, null);
        when(customerDao.findByUsername("ronald")).thenReturn(Optional.empty());
        when(customerDao.create(customer)).thenReturn(saved);
        //Act
        Customer result = customerService.register(customer);
        //Assert
        assertEquals(1, result.getCustomerId());
    }

    @Test
    void register_takenUsername_throwsUsernameTaken() {
        //Arrange
        when(customerDao.findByUsername("ronald")).thenReturn(Optional.of(newCustomer("ronald")));
        //Act + Assert
        assertThrows(UsernameTakenException.class, () -> customerService.register(newCustomer("ronald")));
        verify(customerDao, never()).create(any());
    }

    @Test
    void register_blankUsername_throwsInvalidInput() {
        assertThrows(InvalidInputException.class, () -> customerService.register(newCustomer("   ")));
        verify(customerDao, never()).create(any());
    }

    @Test
    void register_missingPassword_throwsInvalidInput() {
        Customer customer = new Customer("ronald", null, "Ronald", "Forte", null, null, null);
        assertThrows(InvalidInputException.class, () -> customerService.register(customer));
        verify(customerDao, never()).create(any());
    }
    //------------------------------------


    //Login ------------------------------

    @Test
    void login_correctPassword_returnsCustomer() {
        Customer stored = new Customer(1, "ronald", "pw", "Ronald", "Forte", null, null, null);
        when(customerDao.findByUsername("ronald")).thenReturn(Optional.of(stored));
        assertSame(stored, customerService.login("ronald", "pw"));
    }

    @Test
    void login_wrongPassword_throwsInvalidCredentials() {
        Customer stored = new Customer(1, "ronald", "pw", "Ronald", "Forte", null, null, null);
        when(customerDao.findByUsername("ronald")).thenReturn(Optional.of(stored));
        assertThrows(InvalidCredentialsException.class, () -> customerService.login("ronald", "wrong"));
    }

    @Test
    void login_unknownUsername_throwsSameInvalidCredentials() {
        //Arrange: no when(...) -> Optional.empty()
        //Act + Assert: same exception as a wrong password, so attackers can't tell which usernames exist
        assertThrows(InvalidCredentialsException.class, () -> customerService.login("nobody", "pw"));
    }
    //------------------------------------


    //Employee features ------------------

    @Test
    void getAllCustomers_returnsWhatDaoReturns() {
        List<Customer> customers = List.of(newCustomer("a"), newCustomer("b"));
        when(customerDao.findAllCustomers()).thenReturn(customers);
        assertSame(customers, customerService.getAllCustomers());
    }
    //------------------------------------
}