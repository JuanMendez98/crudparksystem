package com.crudzaso.crudpark.dao;

import com.crudzaso.crudpark.config.DatabaseConnection;
import com.crudzaso.crudpark.dao.interfaces.IOperatorDAO;
import com.crudzaso.crudpark.model.Operator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO implementation for managing operations on the operators table
 */
public class OperatorDAO implements IOperatorDAO {

    @Override
    public Operator findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT id, name, username, password, email, active, created_at " +
                "FROM operators " +
                "WHERE username = ? AND password = ? AND active = true";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOperator(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding operator: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Operator findById(int id) {
        String sql = "SELECT id, name, username, password, email, active, created_at " +
                "FROM operators WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToOperator(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding operator by ID: " + e.getMessage());
        }

        return null;
    }

    private Operator mapResultSetToOperator(ResultSet rs) throws SQLException {
        Operator operator = new Operator();
        operator.setId(rs.getInt("id"));
        operator.setName(rs.getString("name"));
        operator.setUsername(rs.getString("username"));
        operator.setPassword(rs.getString("password"));
        operator.setEmail(rs.getString("email"));
        operator.setActive(rs.getBoolean("active"));
        operator.setCreatedAt(rs.getTimestamp("created_at"));
        return operator;
    }
}