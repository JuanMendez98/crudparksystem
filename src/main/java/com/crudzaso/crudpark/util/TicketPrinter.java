package com.crudzaso.crudpark.util;

import com.crudzaso.crudpark.config.AppConfig;
import com.crudzaso.crudpark.model.Operator;
import com.crudzaso.crudpark.model.Ticket;

import java.awt.image.BufferedImage;

/**
 * Class for formatting and printing tickets
 */
public class TicketPrinter {
    private final AppConfig config;
    private final QRGenerator qrGenerator;

    public TicketPrinter() {
        this.config = AppConfig.getInstance();
        this.qrGenerator = new QRGenerator();
    }

    public String formatTicket(Ticket ticket, Operator operator, BufferedImage qrImage) {
        StringBuilder sb = new StringBuilder();
        int width = config.getTicketWidth();

        sb.append(center("=", width)).append("\n");
        sb.append(center(config.getTicketCompany(), width)).append("\n");
        sb.append(center("=", width)).append("\n");
        sb.append(String.format("Folio: %s%n", ticket.getFolio() != null ? ticket.getFolio() : "N/A"));
        sb.append(String.format("Ticket #: %06d%n", ticket.getId()));
        sb.append(String.format("Placa: %s%n", ticket.getLicensePlate()));
        sb.append(String.format("Tipo: %s%n", ticket.getCustomerType().getDescription()));
        sb.append(String.format("Ingreso: %s%n", DateUtils.formatForTicket(ticket.getEntryDate())));
        sb.append(String.format("Operador: %s%n", operator.getName()));
        sb.append(center("-", width)).append("\n");

        if (qrImage != null) {
            sb.append("QR: TICKET:").append(ticket.getId())
                    .append("|PLATE:").append(ticket.getLicensePlate())
                    .append("|DATE:").append(ticket.getEntryDate().getTime())
                    .append("\n");
        }

        sb.append(center("-", width)).append("\n");
        sb.append(center(config.getTicketFarewellMessage(), width)).append("\n");
        sb.append(center("=", width)).append("\n");

        return sb.toString();
    }

    public void printTicket(Ticket ticket, Operator operator) {
        long timestamp = ticket.getEntryDate().getTime();
        BufferedImage qrImage = qrGenerator.generateQR(
                ticket.getId(),
                ticket.getLicensePlate(),
                timestamp
        );

        String formattedTicket = formatTicket(ticket, operator, qrImage);

        System.out.println("\n" + formattedTicket);

        System.out.println("[SIMULATION] Sending to printer: " +
                config.getPrinterName());
    }

    private String center(String text, int width) {
        if (text.length() == 1) {
            return text.repeat(width);
        }

        if (text.length() >= width) {
            return text.substring(0, width);
        }

        int leftSpaces = (width - text.length()) / 2;
        int rightSpaces = width - text.length() - leftSpaces;

        return " ".repeat(leftSpaces) + text + " ".repeat(rightSpaces);
    }

    public String generateTicketForView(Ticket ticket, Operator operator) {
        return formatTicket(ticket, operator, null);
    }
}