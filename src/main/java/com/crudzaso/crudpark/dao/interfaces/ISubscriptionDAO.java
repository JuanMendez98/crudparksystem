package com.crudzaso.crudpark.dao.interfaces;

import com.crudzaso.crudpark.model.Subscription;

/**
 * Interface for Subscription DAO operations
 */
public interface ISubscriptionDAO {

    /**
     * Finds an active subscription by license plate
     * Uses the active_subscriptions view for optimized query
     * @param licensePlate vehicle's license plate
     * @return Subscription if found and active, null otherwise
     */
    Subscription findActiveByLicensePlate(String licensePlate);

    /**
     * Finds a subscription by ID
     * @param id subscription's ID
     * @return Subscription if found, null otherwise
     */
    Subscription findById(int id);
}