package com.crudzaso.crudpark.dao;

import com.crudzaso.crudpark.config.DatabaseConnection;
import com.crudzaso.crudpark.dao.interfaces.ISubscriptionDAO;
import com.crudzaso.crudpark.model.Subscription;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO implementation for managing operations on the subscriptions table
 */
public class SubscriptionDAO implements ISubscriptionDAO {

    @Override
    public Subscription findActiveByLicensePlate(String licensePlate) {
        // Using the active_subscriptions view for optimized query
        String sql = "SELECT id, name, email, license_plate, start_date, end_date, active, " +
                "notification_sent, created_at " +
                "FROM active_subscriptions " +
                "WHERE license_plate = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSubscription(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding active subscription: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Subscription findById(int id) {
        String sql = "SELECT id, name, email, license_plate, start_date, end_date, active, " +
                "notification_sent, created_at " +
                "FROM subscriptions WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSubscription(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding subscription by ID: " + e.getMessage());
        }

        return null;
    }

    private Subscription mapResultSetToSubscription(ResultSet rs) throws SQLException {
        Subscription subscription = new Subscription();
        subscription.setId(rs.getInt("id"));
        subscription.setName(rs.getString("name"));
        subscription.setEmail(rs.getString("email"));
        subscription.setLicensePlate(rs.getString("license_plate"));
        subscription.setStartDate(rs.getDate("start_date"));
        subscription.setEndDate(rs.getDate("end_date"));
        subscription.setActive(rs.getBoolean("active"));
        subscription.setNotificationSent(rs.getBoolean("notification_sent"));
        subscription.setCreatedAt(rs.getTimestamp("created_at"));
        return subscription;
    }
}