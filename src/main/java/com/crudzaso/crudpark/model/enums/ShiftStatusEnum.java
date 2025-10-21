package com.crudzaso.crudpark.model.enums;

/**
 * Enum representing shift status
 * Maps to database values: 'OPEN', 'CLOSED'
 */
public enum ShiftStatusEnum {
    OPEN("Abierto"),
    CLOSED("Cerrado");

    private final String description;

    ShiftStatusEnum(String description) {
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