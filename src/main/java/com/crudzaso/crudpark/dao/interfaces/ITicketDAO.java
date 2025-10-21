package com.crudzaso.crudpark.dao.interfaces;

import com.crudzaso.crudpark.model.Ticket;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Interface for Ticket DAO operations
 */
public interface ITicketDAO {

    /**
     * Inserts a new ticket into the database
     * @param ticket ticket to insert
     * @return generated ticket ID, or null if failed
     */
    Integer insert(Ticket ticket);

    /**
     * Finds an open ticket by license plate
     * @param licensePlate vehicle's license plate
     * @return Ticket if found and open, null otherwise
     */
    Ticket findOpenTicketByLicensePlate(String licensePlate);

    /**
     * Finds a ticket by ID
     * @param id ticket's ID
     * @return Ticket if found, null otherwise
     */
    Ticket findById(int id);

    /**
     * Finds all open tickets
     * Uses the vehicles_inside view for optimized query
     * @return List of open tickets
     */
    List<Ticket> findAllOpen();

    /**
     * Updates ticket with exit information
     * @param ticketId ticket's ID
     * @param exitDate exit timestamp
     * @param exitOperatorId operator who processed the exit
     * @param amountCharged amount charged to the customer
     * @return true if updated successfully, false otherwise
     */
    boolean updateExit(int ticketId, Timestamp exitDate, int exitOperatorId, BigDecimal amountCharged);

    /**
     * Gets the next folio number for tickets
     * @return next folio number
     */
    int getNextFolioNumber();
}