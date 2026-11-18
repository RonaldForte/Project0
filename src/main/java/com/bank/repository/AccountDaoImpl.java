package com.bank.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bank.model.Account;
import com.bank.model.AccountStatus;
import com.bank.model.AccountType;
import com.bank.util.ConnectionUtil;

public class AccountDaoImpl implements AccountDao {
    //Create -----------------------------

    //Customer applies to open an account
    @Override
    public Account create(Account account) {
        String sql = "INSERT INTO account (account_type, balance, status) VALUES (?, ?, ?)";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getAccountType().name()); //.name because enum
            ps.setBigDecimal(2, account.getBalance());
            ps.setString(3, account.getStatus().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    account.setAccountId(keys.getInt("account_id"));
                }
            }
            return account;
        } catch (SQLException e) {
            throw new RuntimeException("Error creating account", e);
        }
    }
    //------------------------------------


    //Read -------------------------------

    //Customer sees their own accounts
    @Override
    public List<Account> findByCustomerId(int customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT a.account_id, a.account_type, a.balance, a.status, a.reviewed_by "
                   + "FROM account a "
                   + "JOIN account_owner ao ON a.account_id = ao.account_id "
                   + "WHERE ao.customer_id = ? "
                   + "ORDER BY a.account_id";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding accounts for customer " + customerId, e);
        }
        return accounts;
    }

    //Employee sees all pending applications
    @Override
    public List<Account> findPendingAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, account_type, balance, status, reviewed_by "
                   + "FROM account WHERE status = ? ORDER BY account_id";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, AccountStatus.PENDING.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding pending accounts", e);
        }
        return accounts;
    }

    //Employee sees all accounts and balances
    @Override
    public List<Account> findAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, account_type, balance, status, reviewed_by "
                   + "FROM account ORDER BY account_id";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all accounts", e);
        }
        return accounts;
    }

    //Look up one account
    @Override
    public Optional<Account> findByAccountId(int accountId) {
        String sql = "SELECT account_id, account_type, balance, status, reviewed_by "
                   + "FROM account WHERE account_id = ?";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding account " + accountId, e);
        }
        return Optional.empty();
    }
    //------------------------------------


    //Update -----------------------------

    //Deposit, withdraw, transfer change a balance
    @Override
    public boolean updateBalance(int accountId, BigDecimal newBalance) {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, accountId);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating balance for account " + accountId, e);
        }
    }

    //Employee approves or denies
    @Override
    public boolean updateStatus(int accountId, AccountStatus status, int employeeId) {
        String sql = "UPDATE account SET status = ?, reviewed_by = ? WHERE account_id = ?";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, employeeId);
            ps.setInt(3, accountId);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating status for account " + accountId, e);
        }
    }
    //------------------------------------


    //Delete not a part of req


    //Helper -----------------------------

    //Maps one row to an Account (shared by every read)
    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account(
            rs.getInt("account_id"),
            AccountType.valueOf(rs.getString("account_type")),
            rs.getBigDecimal("balance"),
            AccountStatus.valueOf(rs.getString("status")),
            rs.getObject("reviewed_by", Integer.class));
    }
    //------------------------------------
}