package com.crudzaso.crudpark.service;

import com.crudzaso.crudpark.dao.SubscriptionDAO;
import com.crudzaso.crudpark.dao.interfaces.ISubscriptionDAO;
import com.crudzaso.crudpark.model.Subscription;

/**
 * Business logic service for subscription management
 * Handles validation and operations related to subscriptions
 */
public class SubscriptionService {

    private final ISubscriptionDAO subscriptionDAO;

    public SubscriptionService() {
        this.subscriptionDAO = new SubscriptionDAO();
    }

    /**
     * Checks if a vehicle has an active subscription
     * @param licensePlate The license plate to check
     * @return true if there is an active subscription, false otherwise
     */
    public boolean hasActiveSubscription(String licensePlate) {
        Subscription subscription = subscriptionDAO.findActiveByLicensePlate(licensePlate);
        return subscription != null && subscription.isActive();
    }

    /**
     * Gets the active subscription for a license plate
     * @param licensePlate The license plate to search
     * @return The active subscription or null if not found
     */
    public Subscription getActiveSubscription(String licensePlate) {
        Subscription subscription = subscriptionDAO.findActiveByLicensePlate(licensePlate);
        if (subscription != null && subscription.isActive()) {
            return subscription;
        }
        return null;
    }

    /**
     * Validates if a subscription allows vehicle entry
     * @param licensePlate The license plate to validate
     * @return true if the subscription is valid for entry, false otherwise
     */
    public boolean isValidForEntry(String licensePlate) {
        return hasActiveSubscription(licensePlate);
    }
}