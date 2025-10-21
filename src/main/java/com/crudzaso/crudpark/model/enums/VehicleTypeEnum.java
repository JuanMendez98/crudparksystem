package com.crudzaso.crudpark.model.enums;

/**
 * Enum representing vehicle types in the parking lot
 * Maps to database values: 'CAR', 'MOTORCYCLE'
 */
public enum VehicleTypeEnum {
    CAR("Carro"),
    MOTORCYCLE("Moto");

    private final String description;

    VehicleTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
