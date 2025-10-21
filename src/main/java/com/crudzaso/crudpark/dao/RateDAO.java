package com.crudzaso.crudpark.dao;

import com.crudzaso.crudpark.config.DatabaseConnection;
import com.crudzaso.crudpark.dao.interfaces.IRateDAO;
import com.crudzaso.crudpark.model.Rate;
import com.crudzaso.crudpark.model.enums.VehicleTypeEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO implementation for managing operations on the rates table
 */
public class RateDAO implements IRateDAO {

    @Override
    public Rate findActiveByVehicleType(VehicleTypeEnum vehicleType) {
        String sql = "SELECT id, name, vehicle_type, hourly_rate, grace_period, " +
                "fraction_value, daily_limit, active " +
                "FROM rates " +
                "WHERE vehicle_type = ? AND active = true";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicleType.name());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToRate(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding active rate: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Rate findById(int id) {
        String sql = "SELECT id, name, vehicle_type, hourly_rate, grace_period, " +
                "fraction_value, daily_limit, active " +
                "FROM rates WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToRate(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding rate by ID: " + e.getMessage());
        }

        return null;
    }

    private Rate mapResultSetToRate(ResultSet rs) throws SQLException {
        Rate rate = new Rate();
        rate.setId(rs.getInt("id"));
        rate.setName(rs.getString("name"));
        rate.setVehicleType(VehicleTypeEnum.valueOf(rs.getString("vehicle_type")));
        rate.setHourlyRate(rs.getBigDecimal("hourly_rate"));
        rate.setGracePeriod(rs.getInt("grace_period"));
        rate.setFractionValue(rs.getBigDecimal("fraction_value"));
        rate.setDailyLimit(rs.getBigDecimal("daily_limit"));
        rate.setActive(rs.getBoolean("active"));
        return rate;
    }
}
