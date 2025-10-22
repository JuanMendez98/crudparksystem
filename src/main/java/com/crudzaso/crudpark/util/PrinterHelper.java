package com.crudzaso.crudpark.util;

import com.crudzaso.crudpark.model.Operator;
import com.crudzaso.crudpark.model.Ticket;

import javax.print.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;

/**
 * Helper class to print tickets on physical printers
 * Uses javax.print (Java Print API standard)
 * Compatible with any printer, optimized for Xprinter XP-58IIT
 */
public class PrinterHelper {
    private final QRGenerator qrGenerator;

    public PrinterHelper() {
        this.qrGenerator = new QRGenerator();
    }

    /**
     * Prints ticket using system print dialog
     * Compatible with any printer
     */
    public boolean printWithDialog(Ticket ticket, Operator operator) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new TicketPrintable(ticket, operator));

        boolean doPrint = job.printDialog();

        if (doPrint) {
            try {
                job.print();
                System.out.println("✓ Ticket sent to printer: Folio " + ticket.getFolio());
                return true;
            } catch (PrinterException e) {
                System.err.println("✗ Print error: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * Prints directly to default printer without dialog
     */
    public void printDirect(Ticket ticket, Operator operator) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new TicketPrintable(ticket, operator));

        try {
            job.print();
            System.out.println("✓ Ticket printed: Folio " + ticket.getFolio());
        } catch (PrinterException e) {
            System.err.println("✗ Print error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Prints on Xprinter XP-58IIT automatically
     * Searches for the printer by name
     * Falls back to other printers if Xprinter not found
     */
    public boolean printOnXprinter(Ticket ticket, Operator operator) {
        // Possible names for Xprinter in the system
        String[] possibleNames = {"XP-58", "Xprinter", "XP58", "58IIT", "POS-58", "POS58"};

        PrintService xprinter = null;

        // Search for the printer
        for (String name : possibleNames) {
            xprinter = findPrinter(name);
            if (xprinter != null) {
                System.out.println("✓ Xprinter found: " + xprinter.getName());
                break;
            }
        }

        if (xprinter == null) {
            System.err.println("✗ Xprinter XP-58IIT not found");
            System.out.println("Make sure:");
            System.out.println("  1. Printer is connected (USB)");
            System.out.println("  2. Driver is installed");
            System.out.println("  3. Printer is turned on");
            System.out.println("\nAvailable printers:");
            listPrinters();
            return false;
        }

        // Print using format optimized for 58mm
        PrinterJob job = PrinterJob.getPrinterJob();

        try {
            job.setPrintService(xprinter);

            // Configure page format for 58mm
            PageFormat pageFormat = job.defaultPage();
            Paper paper = new Paper();

            // Dimensions for 58mm thermal paper (in points: 1 inch = 72 points)
            double paperWidth = 58 * 72 / 25.4;  // 58mm to points
            double paperHeight = 300 * 72 / 25.4; // 300mm height for longer tickets

            // Minimum margin for thermal printers
            double margin = 5; // 5 points margin

            paper.setSize(paperWidth, paperHeight);
            paper.setImageableArea(margin, margin, paperWidth - 2 * margin, paperHeight - 2 * margin);
            pageFormat.setPaper(paper);

            job.setPrintable(new TicketPrintable58mm(ticket, operator), pageFormat);
            job.print();

            System.out.println("✓ Ticket printed successfully on Xprinter XP-58IIT");
            System.out.println("  Folio: " + ticket.getFolio());
            System.out.println("  License Plate: " + ticket.getLicensePlate());

            return true;

        } catch (PrinterException e) {
            System.err.println("✗ Error printing on Xprinter: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lists all available printers in the system
     */
    public void listPrinters() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        System.out.println("\n=== Available Printers ===");
        for (int i = 0; i < printServices.length; i++) {
            System.out.println((i + 1) + ". " + printServices[i].getName());
        }
        System.out.println("==========================\n");
    }

    /**
     * Finds a printer by name (partial case-insensitive search)
     */
    private PrintService findPrinter(String searchName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService service : printServices) {
            if (service.getName().toLowerCase().contains(searchName.toLowerCase())) {
                return service;
            }
        }

        return null;
    }

    /**
     * Inner class that defines how the ticket is printed
     * Standard format for any printer
     */
    private class TicketPrintable implements Printable {
        private final Ticket ticket;
        private final Operator operator;
        private final BufferedImage qrImage;

        public TicketPrintable(Ticket ticket, Operator operator) {
            this.ticket = ticket;
            this.operator = operator;

            // Generate QR to include in print
            long timestamp = ticket.getEntryDate().getTime();
            this.qrImage = qrGenerator.generateQR(ticket.getId(), ticket.getLicensePlate(), timestamp);
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // Fonts with enhanced bold design for better visibility
            Font titleFont = new Font("Monospaced", Font.BOLD, 16);
            Font subtitleFont = new Font("Monospaced", Font.BOLD, 12);
            Font normalFont = new Font("Monospaced", Font.BOLD, 11);
            Font smallFont = new Font("Monospaced", Font.BOLD, 9);

            int y = 25;
            int lineHeight = 16;

            // ===== HEADER =====
            g2d.setFont(titleFont);
            g2d.drawString(centerText("CRUDPARK", 40), 10, y);
            y += lineHeight;

            g2d.setFont(smallFont);
            g2d.drawString(centerText("Sistema de Parqueadero", 40), 10, y);
            y += lineHeight;

            g2d.drawString(centerText("-".repeat(40), 40), 10, y);
            y += lineHeight + 8;

            // ===== TICKET INFO =====
            g2d.setFont(subtitleFont);
            g2d.drawString("FOLIO:", 15, y);
            g2d.setFont(normalFont);
            g2d.drawString(ticket.getFolio(), 70, y);
            y += lineHeight;

            g2d.setFont(subtitleFont);
            g2d.drawString("TICKET #:", 15, y);
            g2d.setFont(normalFont);
            g2d.drawString(String.format("%06d", ticket.getId()), 70, y);
            y += lineHeight;

            g2d.setFont(subtitleFont);
            g2d.drawString("PLACA:", 15, y);
            g2d.setFont(normalFont);
            g2d.drawString(ticket.getLicensePlate(), 70, y);
            y += lineHeight;

            g2d.setFont(subtitleFont);
            g2d.drawString("TIPO:", 15, y);
            g2d.setFont(normalFont);
            g2d.drawString(ticket.getCustomerType().getDescription(), 70, y);
            y += lineHeight + 5;

            g2d.setFont(smallFont);
            g2d.drawString(centerText(".".repeat(40), 40), 10, y);
            y += lineHeight + 5;

            // ===== DATE AND OPERATOR =====
            g2d.setFont(subtitleFont);
            g2d.drawString("INGRESO:", 15, y);
            y += lineHeight;

            g2d.setFont(normalFont);
            String date = DateUtils.formatForTicket(ticket.getEntryDate());
            g2d.drawString(date, 20, y);
            y += lineHeight;

            g2d.setFont(subtitleFont);
            g2d.drawString("OPERADOR:", 15, y);
            g2d.setFont(normalFont);
            g2d.drawString(operator.getName(), 75, y);
            y += lineHeight + 8;

            // ===== QR CODE =====
            if (qrImage != null) {
                g2d.setFont(smallFont);
                g2d.drawString(centerText("-".repeat(40), 40), 10, y);
                y += lineHeight;

                g2d.setFont(subtitleFont);
                g2d.drawString(centerText("ESCANEA EL CODIGO QR", 40), 10, y);
                y += lineHeight;

                g2d.setFont(smallFont);
                g2d.drawString(centerText("PARA SALIDA RAPIDA", 40), 10, y);
                y += lineHeight + 5;

                // QR Code
                int qrSize = 120;
                int qrX = ((int) pageFormat.getImageableWidth() - qrSize) / 2;
                g2d.drawImage(qrImage, qrX, y, qrSize, qrSize, null);
                y += qrSize + 10;
            }

            y += 8;

            // ===== FOOTER =====
            g2d.setFont(smallFont);
            g2d.drawString(centerText("-".repeat(40), 40), 10, y);
            y += lineHeight;

            g2d.setFont(subtitleFont);
            g2d.drawString(centerText("GRACIAS POR SU VISITA", 40), 10, y);
            y += lineHeight;

            g2d.setFont(smallFont);
            g2d.drawString(centerText("Conserve este ticket", 40), 10, y);
            y += lineHeight;

            g2d.drawString(centerText("=".repeat(40), 40), 10, y);

            return PAGE_EXISTS;
        }

        private String centerText(String text, int width) {
            if (text.length() >= width) {
                return text.substring(0, width);
            }
            int leftSpaces = (width - text.length()) / 2;
            return " ".repeat(leftSpaces) + text;
        }
    }

    /**
     * Inner class optimized for 58mm printers (Xprinter XP-58IIT)
     */
    private class TicketPrintable58mm implements Printable {
        private final Ticket ticket;
        private final Operator operator;
        private final BufferedImage qrImage;

        public TicketPrintable58mm(Ticket ticket, Operator operator) {
            this.ticket = ticket;
            this.operator = operator;

            // Generate QR optimized for 58mm (smaller: 80x80px)
            long timestamp = ticket.getEntryDate().getTime();
            this.qrImage = qrGenerator.generateQR(ticket.getId(), ticket.getLicensePlate(), timestamp);
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            int maxWidth = (int) pageFormat.getImageableWidth();
            int margin = 5;
            int usableWidth = maxWidth - 2 * margin;

            // Fonts optimized for 58mm with bold text
            Font titleFont = new Font("Monospaced", Font.BOLD, 12);
            Font subtitleFont = new Font("Monospaced", Font.BOLD, 10);
            Font normalFont = new Font("Monospaced", Font.BOLD, 9);
            Font smallFont = new Font("Monospaced", Font.BOLD, 8);

            int y = 15;
            int lineHeight = 12;

            // ===== HEADER =====
            g2d.setFont(titleFont);
            g2d.drawString(centerText58mm("CRUDPARK", usableWidth), margin, y);
            y += lineHeight;

            g2d.setFont(smallFont);
            g2d.drawString(centerText58mm("Sistema de Parqueadero", usableWidth), margin, y);
            y += lineHeight;

            g2d.drawString(centerText58mm("-".repeat(28), usableWidth), margin, y);
            y += lineHeight + 5;

            // ===== TICKET INFO =====
            g2d.setFont(subtitleFont);
            g2d.drawString("FOLIO:", margin, y);
            g2d.setFont(normalFont);
            g2d.drawString(ticket.getFolio(), margin + 35, y);
            y += lineHeight;

            g2d.setFont(subtitleFont);
            g2d.drawString("TICKET #:", margin, y);
            g2d.setFont(normalFont);
            g2d.drawString(String.format("%06d", ticket.getId()), margin + 35, y);
            y += lineHeight;

            g2d.setFont(subtitleFont);
            g2d.drawString("PLACA:", margin, y);
            g2d.setFont(normalFont);
            g2d.drawString(ticket.getLicensePlate(), margin + 35, y);
            y += lineHeight;

            g2d.setFont(subtitleFont);
            g2d.drawString("TIPO:", margin, y);
            g2d.setFont(normalFont);
            g2d.drawString(ticket.getCustomerType().getDescription(), margin + 35, y);
            y += lineHeight;

            g2d.setFont(smallFont);
            g2d.drawString(centerText58mm(".".repeat(28), usableWidth), margin, y);
            y += lineHeight + 3;

            // ===== DATE AND OPERATOR =====
            g2d.setFont(subtitleFont);
            g2d.drawString("INGRESO:", margin, y);
            y += lineHeight;

            g2d.setFont(smallFont);
            String date = DateUtils.formatForTicket(ticket.getEntryDate());
            g2d.drawString(date, margin + 5, y);
            y += lineHeight;

            g2d.setFont(subtitleFont);
            g2d.drawString("OPERADOR:", margin, y);
            g2d.setFont(normalFont);
            g2d.drawString(operator.getName(), margin + 40, y);
            y += lineHeight + 5;

            // ===== QR CODE =====
            if (qrImage != null) {
                g2d.setFont(smallFont);
                g2d.drawString(centerText58mm("-".repeat(28), usableWidth), margin, y);
                y += lineHeight;

                g2d.setFont(subtitleFont);
                g2d.drawString(centerText58mm("ESCANEA EL QR", usableWidth), margin, y);
                y += lineHeight;

                // QR Code
                int qrSize = 70;
                int qrX = margin + (usableWidth - qrSize) / 2;
                g2d.drawImage(qrImage, qrX, y, qrSize, qrSize, null);
                y += qrSize + 5;

                g2d.setFont(smallFont);
                g2d.drawString(centerText58mm("SALIDA RAPIDA", usableWidth), margin, y);
                y += lineHeight;
            }

            y += 5;

            // ===== FOOTER =====
            g2d.setFont(smallFont);
            g2d.drawString(centerText58mm("-".repeat(28), usableWidth), margin, y);
            y += lineHeight;

            g2d.setFont(subtitleFont);
            g2d.drawString(centerText58mm("GRACIAS", usableWidth), margin, y);
            y += lineHeight;

            g2d.setFont(smallFont);
            g2d.drawString(centerText58mm("Conserve este ticket", usableWidth), margin, y);
            y += lineHeight;

            g2d.drawString(centerText58mm("=".repeat(28), usableWidth), margin, y);

            return PAGE_EXISTS;
        }

        private String centerText58mm(String text, int width) {
            if (text.length() >= width) {
                return text.substring(0, width);
            }
            int leftSpaces = (width - text.length()) / 2;
            return " ".repeat(leftSpaces) + text;
        }
    }
}