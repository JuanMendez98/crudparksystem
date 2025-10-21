package com.crudzaso.crudpark.model.enums;

/**
 * Enum representing ticket status
 * Maps to database values: 'OPEN', 'CLOSED', 'GRACE'
 */
public enum TicketStatusEnum {
    OPEN("Abierto"),
    CLOSED("Cerrado"),
    GRACE("En Gracia");

    private final String description;

    TicketStatusEnum(String description) {
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