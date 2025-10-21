package com.crudzaso.crudpark.dao.interfaces;

import com.crudzaso.crudpark.model.Payment;

/**
 * Interface for Payment DAO operations
 */
public interface IPaymentDAO {

    /**
     * Inserts a new payment into the database
     * @param payment payment to insert
     * @return generated payment ID, or null if failed
     */
    Integer insert(Payment payment);

    /**
     * Finds a payment by ticket ID
     * @param ticketId ticket's ID
     * @return Payment if found, null otherwise
     */
    Payment findByTicketId(int ticketId);
}