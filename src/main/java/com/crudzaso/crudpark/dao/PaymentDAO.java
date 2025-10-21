package com.crudzaso.crudpark.dao;

import com.crudzaso.crudpark.config.DatabaseConnection;
import com.crudzaso.crudpark.dao.interfaces.IPaymentDAO;
import com.crudzaso.crudpark.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO implementation for managing operations on the payments table
 */
public class PaymentDAO implements IPaymentDAO {

    @Override
    public Integer insert(Payment payment) {
        String sql = "INSERT INTO payments (ticket_id, amount, payment_method, payment_date, operator_id) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, payment.getTicketId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setTimestamp(4, payment.getPaymentDate());
            stmt.setInt(5, payment.getOperatorId());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error inserting payment: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Payment findByTicketId(int ticketId) {
        String sql = "SELECT id, ticket_id, amount, payment_method, payment_date, operator_id " +
                "FROM payments WHERE ticket_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPayment(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding payment by ticket ID: " + e.getMessage());
        }

        return null;
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setTicketId(rs.getInt("ticket_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setPaymentDate(rs.getTimestamp("payment_date"));
        payment.setOperatorId(rs.getInt("operator_id"));
        return payment;
    }
}