package com.crudzaso.crudpark.model;

import com.crudzaso.crudpark.model.enums.ShiftStatusEnum;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model class representing an operator work shift
 * Maps to database table: shifts
 */
public class Shift {
    private Integer id;
    private Integer operatorId;
    private Timestamp openingDate;
    private Timestamp closingDate;
    private BigDecimal openingCash;
    private BigDecimal closingCash;
    private BigDecimal totalRevenue;
    private Integer totalTickets;
    private ShiftStatusEnum status;
    private String notes;

    public Shift() {
    }

    public Shift(Integer id, Integer operatorId, Timestamp openingDate, Timestamp closingDate,
                 BigDecimal openingCash, BigDecimal closingCash, BigDecimal totalRevenue,
                 Integer totalTickets, ShiftStatusEnum status, String notes) {
        this.id = id;
        this.operatorId = operatorId;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.openingCash = openingCash;
        this.closingCash = closingCash;
        this.totalRevenue = totalRevenue;
        this.totalTickets = totalTickets;
        this.status = status;
        this.notes = notes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Timestamp getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Timestamp openingDate) {
        this.openingDate = openingDate;
    }

    public Timestamp getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(Timestamp closingDate) {
        this.closingDate = closingDate;
    }

    public BigDecimal getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(BigDecimal openingCash) {
        this.openingCash = openingCash;
    }

    public BigDecimal getClosingCash() {
        return closingCash;
    }

    public void setClosingCash(BigDecimal closingCash) {
        this.closingCash = closingCash;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public ShiftStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ShiftStatusEnum status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", operatorId=" + operatorId +
                ", openingDate=" + openingDate +
                ", closingDate=" + closingDate +
                ", totalRevenue=" + totalRevenue +
                ", totalTickets=" + totalTickets +
                ", status=" + status +
                '}';
    }
}
