package com.crudzaso.crudpark.model;

import com.crudzaso.crudpark.model.enums.CustomerTypeEnum;
import com.crudzaso.crudpark.model.enums.TicketStatusEnum;
import com.crudzaso.crudpark.model.enums.VehicleTypeEnum;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model class representing an entry/exit ticket
 * Maps to database table: tickets
 */
public class Ticket {
    private Integer id;
    private String licensePlate;
    private CustomerTypeEnum customerType;
    private VehicleTypeEnum vehicleType;
    private Timestamp entryDate;
    private Timestamp exitDate;
    private String folio;
    private Integer entryOperatorId;
    private Integer exitOperatorId;
    private TicketStatusEnum status;
    private String qrCode;
    private BigDecimal amountCharged;

    public Ticket() {
    }

    public Ticket(Integer id, String licensePlate, CustomerTypeEnum customerType, VehicleTypeEnum vehicleType,
                  Timestamp entryDate, Timestamp exitDate, String folio, Integer entryOperatorId,
                  Integer exitOperatorId, TicketStatusEnum status, String qrCode, BigDecimal amountCharged) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.customerType = customerType;
        this.vehicleType = vehicleType;
        this.entryDate = entryDate;
        this.exitDate = exitDate;
        this.folio = folio;
        this.entryOperatorId = entryOperatorId;
        this.exitOperatorId = exitOperatorId;
        this.status = status;
        this.qrCode = qrCode;
        this.amountCharged = amountCharged;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public CustomerTypeEnum getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerTypeEnum customerType) {
        this.customerType = customerType;
    }

    public VehicleTypeEnum getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleTypeEnum vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Timestamp getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Timestamp entryDate) {
        this.entryDate = entryDate;
    }

    public Timestamp getExitDate() {
        return exitDate;
    }

    public void setExitDate(Timestamp exitDate) {
        this.exitDate = exitDate;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public Integer getEntryOperatorId() {
        return entryOperatorId;
    }

    public void setEntryOperatorId(Integer entryOperatorId) {
        this.entryOperatorId = entryOperatorId;
    }

    public Integer getExitOperatorId() {
        return exitOperatorId;
    }

    public void setExitOperatorId(Integer exitOperatorId) {
        this.exitOperatorId = exitOperatorId;
    }

    public TicketStatusEnum getStatus() {
        return status;
    }

    public void setStatus(TicketStatusEnum status) {
        this.status = status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public BigDecimal getAmountCharged() {
        return amountCharged;
    }

    public void setAmountCharged(BigDecimal amountCharged) {
        this.amountCharged = amountCharged;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", licensePlate='" + licensePlate + '\'' +
                ", customerType=" + customerType +
                ", vehicleType=" + vehicleType +
                ", entryDate=" + entryDate +
                ", exitDate=" + exitDate +
                ", folio='" + folio + '\'' +
                ", status=" + status +
                '}';
    }
}