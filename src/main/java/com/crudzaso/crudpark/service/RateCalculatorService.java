package com.crudzaso.crudpark.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import com.crudzaso.crudpark.config.AppConfig;
import com.crudzaso.crudpark.dao.RateDAO;
import com.crudzaso.crudpark.model.Rate;
import com.crudzaso.crudpark.model.enums.VehicleTypeEnum;

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

        // Calculate hours to charge (excluding grace period, rounding up to nearest hour)
        // Example: 80 min = 1h 20min = 2 hours (because 80-30=50min, which rounds to 1 hour)
        // But according to requirements: 80 min should charge 2 hours
        // So we need to calculate from total time, not from time after grace period
        double totalHours = minutesStay / 60.0;
        int hoursToCharge = (int) Math.ceil(totalHours);
        BigDecimal hours = BigDecimal.valueOf(hoursToCharge);

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
