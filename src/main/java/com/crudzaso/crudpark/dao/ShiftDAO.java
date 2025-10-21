package com.crudzaso.crudpark.dao;

import com.crudzaso.crudpark.config.DatabaseConnection;
import com.crudzaso.crudpark.dao.interfaces.IShiftDAO;
import com.crudzaso.crudpark.model.Shift;
import com.crudzaso.crudpark.model.enums.ShiftStatusEnum;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * DAO implementation for managing operations on the shifts table
 */
public class ShiftDAO implements IShiftDAO {

    @Override
    public Integer insert(Shift shift) {
        String sql = "INSERT INTO shifts (operator_id, opening_date, opening_cash, status) " +
                "VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, shift.getOperatorId());
            stmt.setTimestamp(2, shift.getOpeningDate());
            stmt.setBigDecimal(3, shift.getOpeningCash() != null ? shift.getOpeningCash() : BigDecimal.ZERO);
            stmt.setString(4, shift.getStatus() != null ? shift.getStatus().name() : "OPEN");

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error inserting shift: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Shift findOpenByOperatorId(int operatorId) {
        String sql = "SELECT id, operator_id, opening_date, closing_date, opening_cash, closing_cash, " +
                "total_revenue, total_tickets, status, notes " +
                "FROM shifts " +
                "WHERE operator_id = ? AND status = 'OPEN'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, operatorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToShift(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding open shift: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean updateClose(int shiftId, Timestamp closingDate, BigDecimal closingCash,
                              BigDecimal totalRevenue, int totalTickets) {
        String sql = "UPDATE shifts SET closing_date = ?, closing_cash = ?, total_revenue = ?, " +
                "total_tickets = ?, status = 'CLOSED' WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, closingDate);
            stmt.setBigDecimal(2, closingCash);
            stmt.setBigDecimal(3, totalRevenue);
            stmt.setInt(4, totalTickets);
            stmt.setInt(5, shiftId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating shift close: " + e.getMessage());
            return false;
        }
    }

    private Shift mapResultSetToShift(ResultSet rs) throws SQLException {
        Shift shift = new Shift();
        shift.setId(rs.getInt("id"));
        shift.setOperatorId(rs.getInt("operator_id"));
        shift.setOpeningDate(rs.getTimestamp("opening_date"));
        shift.setClosingDate(rs.getTimestamp("closing_date"));
        shift.setOpeningCash(rs.getBigDecimal("opening_cash"));
        shift.setClosingCash(rs.getBigDecimal("closing_cash"));
        shift.setTotalRevenue(rs.getBigDecimal("total_revenue"));
        shift.setTotalTickets(rs.getInt("total_tickets"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            shift.setStatus(ShiftStatusEnum.valueOf(statusStr));
        }

        shift.setNotes(rs.getString("notes"));
        return shift;
    }
}