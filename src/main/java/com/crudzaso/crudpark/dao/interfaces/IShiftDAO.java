package com.crudzaso.crudpark.dao.interfaces;

import com.crudzaso.crudpark.model.Shift;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Interface for Shift DAO operations
 */
public interface IShiftDAO {

    /**
     * Inserts a new shift into the database
     * @param shift shift to insert
     * @return generated shift ID, or null if failed
     */
    Integer insert(Shift shift);

    /**
     * Finds an open shift by operator ID
     * @param operatorId operator's ID
     * @return Shift if found and open, null otherwise
     */
    Shift findOpenByOperatorId(int operatorId);

    /**
     * Updates shift with closing information
     * @param shiftId shift's ID
     * @param closingDate closing timestamp
     * @param closingCash cash at closing
     * @param totalRevenue total revenue during shift
     * @param totalTickets total tickets processed
     * @return true if updated successfully, false otherwise
     */
    boolean updateClose(int shiftId, Timestamp closingDate, BigDecimal closingCash,
                       BigDecimal totalRevenue, int totalTickets);
}