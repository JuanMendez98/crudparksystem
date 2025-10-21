package com.crudzaso.crudpark.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Model class representing a monthly subscription
 * Maps to database table: subscriptions
 */
public class Subscription {
    private Integer id;
    private String name;
    private String email;
    private String licensePlate;
    private Date startDate;
    private Date endDate;
    private Boolean active;
    private Boolean notificationSent;
    private Timestamp createdAt;

    public Subscription() {
    }

    public Subscription(Integer id, String name, String email, String licensePlate, Date startDate, Date endDate,
                       Boolean active, Boolean notificationSent, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.licensePlate = licensePlate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.notificationSent = notificationSent;
        this.createdAt = createdAt;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(Boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        if (!active) return false;
        LocalDate today = LocalDate.now();
        LocalDate start = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();
        return !today.isBefore(start) && !today.isAfter(end);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", active=" + active +
                '}';
    }
}