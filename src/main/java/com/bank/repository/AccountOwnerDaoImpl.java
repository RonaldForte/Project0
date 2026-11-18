package com.bank.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bank.model.AccountOwner;
import com.bank.util.ConnectionUtil;

public class AccountOwnerDaoImpl implements AccountOwnerDao {
    //Create -----------------------------

    //Link a customer to an account (applying, joint owner)
    @Override
    public AccountOwner create(AccountOwner accountOwner) {
        String sql = "INSERT INTO account_owner (customer_id, account_id) VALUES (?, ?)";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountOwner.getCustomerId());
            ps.setInt(2, accountOwner.getAccountId());

            ps.executeUpdate();
            return accountOwner;
        } catch (SQLException e) {
            throw new RuntimeException("Error linking customer " + accountOwner.getCustomerId()
                    + " to account " + accountOwner.getAccountId(), e);
        }
    }
    //------------------------------------


    //Read -------------------------------

    //Check a customer owns an account before they use it
    @Override
    public boolean isOwner(int customerId, int accountId) {
        String sql = "SELECT 1 FROM account_owner WHERE customer_id = ? AND account_id = ?";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking ownership", e);
        }
    }

    //Everyone who owns an account
    @Override
    public List<AccountOwner> findByAccountId(int accountId) {
        List<AccountOwner> owners = new ArrayList<>();
        String sql = "SELECT customer_id, account_id FROM account_owner WHERE account_id = ?";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    owners.add(new AccountOwner(rs.getInt("customer_id"), rs.getInt("account_id")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding owners of account " + accountId, e);
        }
        return owners;
    }
    //------------------------------------


    //Update not a part of req

    //Delete not a part of req
}