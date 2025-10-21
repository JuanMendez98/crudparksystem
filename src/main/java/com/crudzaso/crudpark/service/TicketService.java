package com.crudzaso.crudpark.service;

import com.crudzaso.crudpark.dao.PaymentDAO;
import com.crudzaso.crudpark.dao.TicketDAO;
import com.crudzaso.crudpark.dao.interfaces.IPaymentDAO;
import com.crudzaso.crudpark.dao.interfaces.ITicketDAO;
import com.crudzaso.crudpark.model.Payment;
import com.crudzaso.crudpark.model.Ticket;
import com.crudzaso.crudpark.model.enums.CustomerTypeEnum;
import com.crudzaso.crudpark.model.enums.TicketStatusEnum;
import com.crudzaso.crudpark.model.enums.VehicleTypeEnum;
import com.crudzaso.crudpark.util.DateUtils;
import com.crudzaso.crudpark.util.QRGenerator;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Business logic service for ticket management
 * Handles vehicle entry, exit, and ticket lifecycle
 */
public class TicketService {

    private final ITicketDAO ticketDAO;
    private final IPaymentDAO paymentDAO;
    private final RateCalculatorService rateCalculator;
    private final QRGenerator qrGenerator;

    public TicketService() {
        this.ticketDAO = new TicketDAO();
        this.paymentDAO = new PaymentDAO();
        this.rateCalculator = new RateCalculatorService();
        this.qrGenerator = new QRGenerator();
    }

    /**
     * Registers vehicle entry and creates a new ticket
     * @param licensePlate The vehicle license plate
     * @param customerType Type of customer (SUBSCRIPTION or GUEST)
     * @param vehicleType Type of vehicle (CAR or MOTORCYCLE)
     * @param operatorId ID of the operator registering the entry
     * @return The created ticket or null if error
     */
    public Ticket registerEntry(String licensePlate, CustomerTypeEnum customerType,
                                VehicleTypeEnum vehicleType, int operatorId) {

        // Check if there's already an open ticket for this plate
        Ticket existingTicket = ticketDAO.findOpenTicketByLicensePlate(licensePlate);
        if (existingTicket != null) {
            System.err.println("Vehicle already has an open ticket: " + licensePlate);
            return null;
        }

        // Generate folio
        int folioNumber = ticketDAO.getNextFolioNumber();
        String folio = String.format("TKT-%06d", folioNumber);

        // Create ticket
        Ticket ticket = new Ticket();
        ticket.setFolio(folio);
        ticket.setLicensePlate(licensePlate);
        ticket.setCustomerType(customerType);
        ticket.setVehicleType(vehicleType);
        ticket.setEntryDate(Timestamp.valueOf(LocalDateTime.now()));
        ticket.setEntryOperatorId(operatorId);
        ticket.setStatus(TicketStatusEnum.OPEN);

        // Generate QR code
        long timestamp = System.currentTimeMillis();
        String qrContent = String.format("TICKET:PENDING|PLATE:%s|DATE:%d", licensePlate, timestamp);
        ticket.setQrCode(qrContent);

        // Insert ticket
        Integer ticketId = ticketDAO.insert(ticket);
        if (ticketId != null) {
            ticket.setId(ticketId);
            return ticket;
        }

        return null;
    }

    /**
     * Processes vehicle exit and calculates charges
     * @param licensePlate The vehicle license plate
     * @param operatorId ID of the operator processing the exit
     * @param paymentMethod Payment method used
     * @return The updated ticket with charges or null if error
     */
    public Ticket processExit(String licensePlate, int operatorId, String paymentMethod) {

        // Find open ticket
        Ticket ticket = ticketDAO.findOpenTicketByLicensePlate(licensePlate);
        if (ticket == null) {
            System.err.println("No open ticket found for plate: " + licensePlate);
            return null;
        }

        Timestamp exitDate = Timestamp.valueOf(LocalDateTime.now());

        // Calculate amount to charge
        BigDecimal amountCharged = BigDecimal.ZERO;

        // Only charge guests, subscriptions don't pay per entry
        if (ticket.getCustomerType() == CustomerTypeEnum.GUEST) {
            long minutesStay = calculateMinutesStay(ticket.getEntryDate(), exitDate);
            amountCharged = rateCalculator.calculateAmount((int) minutesStay, ticket.getVehicleType());
        }

        // Update ticket exit
        boolean updated = ticketDAO.updateExit(ticket.getId(), exitDate, operatorId, amountCharged);

        if (!updated) {
            System.err.println("Error updating ticket exit");
            return null;
        }

        // Register payment if amount > 0
        if (amountCharged.compareTo(BigDecimal.ZERO) > 0) {
            Payment payment = new Payment();
            payment.setTicketId(ticket.getId());
            payment.setAmount(amountCharged);
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentDate(exitDate);
            payment.setOperatorId(operatorId);

            Integer paymentId = paymentDAO.insert(payment);
            if (paymentId == null) {
                System.err.println("Warning: Ticket updated but payment not registered");
            }
        }

        // Update ticket object
        ticket.setExitDate(exitDate);
        ticket.setExitOperatorId(operatorId);
        ticket.setStatus(TicketStatusEnum.CLOSED);
        ticket.setAmountCharged(amountCharged);

        return ticket;
    }

    /**
     * Gets all currently open tickets
     * @return List of open tickets
     */
    public List<Ticket> getAllOpenTickets() {
        return ticketDAO.findAllOpen();
    }

    /**
     * Finds a ticket by its ID
     * @param ticketId The ticket ID
     * @return The ticket or null if not found
     */
    public Ticket findTicketById(int ticketId) {
        return ticketDAO.findById(ticketId);
    }

    /**
     * Calculates stay duration in minutes
     * @param entryDate Entry timestamp
     * @param exitDate Exit timestamp
     * @return Duration in minutes
     */
    private long calculateMinutesStay(Timestamp entryDate, Timestamp exitDate) {
        LocalDateTime entry = entryDate.toLocalDateTime();
        LocalDateTime exit = exitDate.toLocalDateTime();
        Duration duration = Duration.between(entry, exit);
        return duration.toMinutes();
    }

    /**
     * Gets formatted stay time for display
     * @param ticket The ticket
     * @return Formatted stay time (e.g., "2 horas y 30 minutos")
     */
    public String getFormattedStayTime(Ticket ticket) {
        Timestamp exitDate = ticket.getExitDate();
        if (exitDate == null) {
            exitDate = Timestamp.valueOf(LocalDateTime.now());
        }
        long minutes = calculateMinutesStay(ticket.getEntryDate(), exitDate);
        return DateUtils.formatStayTime((int) minutes);
    }
}