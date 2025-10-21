package com.crudzaso.crudpark.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model class representing a payment
 * Maps to database table: payments
 */
public class Payment {
    private Integer id;
    private Integer ticketId;
    private BigDecimal amount;
    private String paymentMethod;
    private Timestamp paymentDate;
    private Integer operatorId;

    public Payment() {
    }

    public Payment(Integer id, Integer ticketId, BigDecimal amount, String paymentMethod,
                   Timestamp paymentDate, Integer operatorId) {
        this.id = id;
        this.ticketId = ticketId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.operatorId = operatorId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", ticketId=" + ticketId +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentDate=" + paymentDate +
                ", operatorId=" + operatorId +
                '}';
    }
}