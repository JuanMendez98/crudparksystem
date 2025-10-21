package com.crudzaso.crudpark.dao;

import com.crudzaso.crudpark.config.DatabaseConnection;
import com.crudzaso.crudpark.dao.interfaces.ITicketDAO;
import com.crudzaso.crudpark.model.Ticket;
import com.crudzaso.crudpark.model.enums.CustomerTypeEnum;
import com.crudzaso.crudpark.model.enums.TicketStatusEnum;
import com.crudzaso.crudpark.model.enums.VehicleTypeEnum;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for managing operations on the tickets table
 */
public class TicketDAO implements ITicketDAO {

    @Override
    public Integer insert(Ticket ticket) {
        String sql = "INSERT INTO tickets (folio, license_plate, customer_type, vehicle_type, " +
                "entry_date, entry_operator_id, status, qr_code) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ticket.getFolio());
            stmt.setString(2, ticket.getLicensePlate());
            stmt.setString(3, ticket.getCustomerType().name());
            stmt.setString(4, ticket.getVehicleType() != null ? ticket.getVehicleType().name() : "CAR");
            stmt.setTimestamp(5, ticket.getEntryDate());
            stmt.setInt(6, ticket.getEntryOperatorId());
            stmt.setString(7, ticket.getStatus() != null ? ticket.getStatus().name() : "OPEN");
            stmt.setString(8, ticket.getQrCode());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error inserting ticket: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Ticket findOpenTicketByLicensePlate(String licensePlate) {
        String sql = "SELECT id, folio, license_plate, customer_type, vehicle_type, entry_date, " +
                "exit_date, entry_operator_id, exit_operator_id, status, qr_code, amount_charged " +
                "FROM tickets " +
                "WHERE license_plate = ? AND status = 'OPEN'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTicket(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding open ticket: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Ticket findById(int id) {
        String sql = "SELECT id, folio, license_plate, customer_type, vehicle_type, entry_date, " +
                "exit_date, entry_operator_id, exit_operator_id, status, qr_code, amount_charged " +
                "FROM tickets WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTicket(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding ticket by ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Ticket> findAllOpen() {
        // Using vehicles_inside view would be ideal, but we need all ticket fields
        String sql = "SELECT id, folio, license_plate, customer_type, vehicle_type, entry_date, " +
                "exit_date, entry_operator_id, exit_operator_id, status, qr_code, amount_charged " +
                "FROM tickets " +
                "WHERE status = 'OPEN' " +
                "ORDER BY entry_date DESC";

        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding open tickets: " + e.getMessage());
        }

        return tickets;
    }

    @Override
    public boolean updateExit(int ticketId, Timestamp exitDate, int exitOperatorId, BigDecimal amountCharged) {
        String sql = "UPDATE tickets SET exit_date = ?, exit_operator_id = ?, " +
                "status = 'CLOSED', amount_charged = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, exitDate);
            stmt.setInt(2, exitOperatorId);
            stmt.setBigDecimal(3, amountCharged != null ? amountCharged : BigDecimal.ZERO);
            stmt.setInt(4, ticketId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating ticket exit: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int getNextFolioNumber() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(folio FROM 5) AS INTEGER)), 0) + 1 AS next_number " +
                     "FROM tickets WHERE folio LIKE 'TKT-%'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("next_number");
            }

        } catch (SQLException e) {
            System.err.println("Error getting next folio number: " + e.getMessage());
        }

        return 1;
    }

    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setFolio(rs.getString("folio"));
        ticket.setLicensePlate(rs.getString("license_plate"));
        ticket.setCustomerType(CustomerTypeEnum.valueOf(rs.getString("customer_type")));

        String vehicleTypeStr = rs.getString("vehicle_type");
        if (vehicleTypeStr != null) {
            ticket.setVehicleType(VehicleTypeEnum.valueOf(vehicleTypeStr));
        }

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            ticket.setStatus(TicketStatusEnum.valueOf(statusStr));
        }

        ticket.setEntryDate(rs.getTimestamp("entry_date"));
        ticket.setExitDate(rs.getTimestamp("exit_date"));
        ticket.setEntryOperatorId(rs.getInt("entry_operator_id"));

        int exitOperatorId = rs.getInt("exit_operator_id");
        if (!rs.wasNull()) {
            ticket.setExitOperatorId(exitOperatorId);
        }

        ticket.setQrCode(rs.getString("qr_code"));
        ticket.setAmountCharged(rs.getBigDecimal("amount_charged"));

        return ticket;
    }
}