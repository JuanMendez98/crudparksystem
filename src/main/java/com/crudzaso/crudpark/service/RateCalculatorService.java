package com.crudzaso.crudpark.service;

import com.crudzaso.crudpark.config.AppConfig;
import com.crudzaso.crudpark.dao.RateDAO;
import com.crudzaso.crudpark.model.Rate;
import com.crudzaso.crudpark.model.enums.VehicleTypeEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service for calculating parking rates
 */
public class RateCalculatorService {
    private final RateDAO rateDAO;
    private final AppConfig config;

    public RateCalculatorService() {
        this.rateDAO = new RateDAO();
        this.config = AppConfig.getInstance();
    }

    public BigDecimal calculateAmount(int minutesStay, VehicleTypeEnum vehicleType) {
        int gracePeriod = config.getGracePeriod();

        // Within grace period - no charge
        if (minutesStay <= gracePeriod) {
            return BigDecimal.ZERO;
        }

        Rate rate = rateDAO.findActiveByVehicleType(vehicleType);

        if (rate == null) {
            System.err.println("No active rate found for: " + vehicleType);
            return BigDecimal.ZERO;
        }

        // Calculate minutes to charge (excluding grace period)
        int minutesToCharge = minutesStay - gracePeriod;

        // Convert minutes to hours (rounding up)
        BigDecimal hours = BigDecimal.valueOf(minutesToCharge)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.CEILING);

        // Calculate amount
        BigDecimal amount = rate.getHourlyRate().multiply(hours);

        // Apply daily limit if configured
        if (rate.getDailyLimit() != null && amount.compareTo(rate.getDailyLimit()) > 0) {
            amount = rate.getDailyLimit();
        }

        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isWithinGracePeriod(int minutesStay) {
        return minutesStay <= config.getGracePeriod();
    }
}