package com.bank.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.util.ConnectionUtil;

public class TransactionDaoImpl implements TransactionDao {
    //Create -----------------------------

    //Record a deposit, withdrawal or transfer
    @Override
    public Transaction create(Transaction transaction) {
        String sql = "INSERT INTO transaction (from_account_id, to_account_id, type, amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, transaction.getFromAccountId(), Types.INTEGER); //nullable
            ps.setObject(2, transaction.getToAccountId(), Types.INTEGER);   //nullable
            ps.setString(3, transaction.getType().name());
            ps.setBigDecimal(4, transaction.getAmount());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    transaction.setTransactionId(keys.getInt("transaction_id"));
                    Timestamp created = keys.getTimestamp("created_at");
                    if (created != null) {
                        transaction.setCreatedAt(created.toLocalDateTime());
                    }
                }
            }
            return transaction;
        } catch (SQLException e) {
            throw new RuntimeException("Error recording transaction", e);
        }
    }
    //------------------------------------


    //Read -------------------------------

    //Full history of one account
    @Override
    public List<Transaction> findByAccountId(int accountId) {
        List<Transaction> history = new ArrayList<>();
        String sql = "SELECT transaction_id, from_account_id, to_account_id, type, amount, created_at "
                   + "FROM transaction WHERE from_account_id = ? OR to_account_id = ? "
                   + "ORDER BY created_at DESC";

        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setInt(2, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    history.add(new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getObject("from_account_id", Integer.class),
                        rs.getObject("to_account_id", Integer.class),
                        TransactionType.valueOf(rs.getString("type")),
                        rs.getBigDecimal("amount"),
                        rs.getTimestamp("created_at").toLocalDateTime()));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding transactions for account " + accountId, e);
        }
        return history;
    }
    //------------------------------------


    //Update not a part of req

    //Delete not a part of req
}