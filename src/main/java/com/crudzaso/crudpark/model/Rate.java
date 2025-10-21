package com.crudzaso.crudpark.model;

import com.crudzaso.crudpark.model.enums.VehicleTypeEnum;
import java.math.BigDecimal;

/**
 * Model class representing a parking rate/pricing
 * Maps to database table: rates
 */
public class Rate {
    private Integer id;
    private String name;
    private VehicleTypeEnum vehicleType;
    private BigDecimal hourlyRate;
    private Integer gracePeriod;
    private BigDecimal fractionValue;
    private BigDecimal dailyLimit;
    private Boolean active;

    public Rate() {
    }

    public Rate(Integer id, String name, VehicleTypeEnum vehicleType, BigDecimal hourlyRate,
                Integer gracePeriod, BigDecimal fractionValue, BigDecimal dailyLimit, Boolean active) {
        this.id = id;
        this.name = name;
        this.vehicleType = vehicleType;
        this.hourlyRate = hourlyRate;
        this.gracePeriod = gracePeriod;
        this.fractionValue = fractionValue;
        this.dailyLimit = dailyLimit;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VehicleTypeEnum getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleTypeEnum vehicleType) {
        this.vehicleType = vehicleType;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Integer getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(Integer gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public BigDecimal getFractionValue() {
        return fractionValue;
    }

    public void setFractionValue(BigDecimal fractionValue) {
        this.fractionValue = fractionValue;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Rate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vehicleType=" + vehicleType +
                ", hourlyRate=" + hourlyRate +
                ", gracePeriod=" + gracePeriod +
                ", active=" + active +
                '}';
    }
}