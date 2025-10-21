package com.crudzaso.crudpark.model.enums;

/**
 * Enum representing customer types in the parking lot
 * Maps to database values: 'SUBSCRIPTION', 'GUEST'
 */
public enum CustomerTypeEnum {
    SUBSCRIPTION("Mensualidad"),
    GUEST("Invitado");

    private final String description;

    CustomerTypeEnum(String description) {
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