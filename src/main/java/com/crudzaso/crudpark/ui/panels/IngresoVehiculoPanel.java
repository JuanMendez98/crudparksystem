package com.crudzaso.crudpark.ui.panels;

import com.crudzaso.crudpark.model.Ticket;
import com.crudzaso.crudpark.model.enums.CustomerTypeEnum;
import com.crudzaso.crudpark.model.enums.VehicleTypeEnum;
import com.crudzaso.crudpark.service.AuthService;
import com.crudzaso.crudpark.service.SubscriptionService;
import com.crudzaso.crudpark.service.TicketService;
import com.crudzaso.crudpark.util.DateUtils;
import com.crudzaso.crudpark.util.PrinterHelper;
import com.crudzaso.crudpark.util.QRGenerator;
import com.crudzaso.crudpark.util.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Modern panel for vehicle entry registration with QR display
 * UI in Spanish for end users, code in English for developers
 */
public class IngresoVehiculoPanel extends JPanel {
    private final AuthService authService;
    private final TicketService ticketService;
    private final SubscriptionService subscriptionService;
    private final QRGenerator qrGenerator;
    private final PrinterHelper printerHelper;

    private JTextField licensePlateField;
    private JButton registerButton;
    private JButton clearButton;
    private JButton printButton;
    private JTextArea ticketDisplayArea;
    private JLabel qrLabel;
    private JLabel statusLabel;
    private Ticket lastTicket;

    public IngresoVehiculoPanel(AuthService authService) {
        this.authService = authService;
        this.ticketService = new TicketService();
        this.subscriptionService = new SubscriptionService();
        this.qrGenerator = new QRGenerator();
        this.printerHelper = new PrinterHelper();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("🚗 Registro de Ingreso de Vehículo");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 73, 94));
        add(titleLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setOpaque(false);

        // Left panel (form + ticket display)
        JPanel leftPanel = createLeftPanel();
        mainPanel.add(leftPanel, BorderLayout.CENTER);

        // Right panel (QR display)
        JPanel rightPanel = createRightPanel();
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            "Datos del Vehículo",
            0,
            0,
            new Font("Segoe UI", Font.BOLD, 13),
            new Color(52, 73, 94)
        ));
        formPanel.setBackground(Color.WHITE);

        // License plate input
        JPanel platePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        platePanel.setOpaque(false);

        JLabel plateLabel = new JLabel("Placa:");
        plateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        platePanel.add(plateLabel);

        licensePlateField = new JTextField(12);
        licensePlateField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        licensePlateField.setPreferredSize(new Dimension(150, 35));
        licensePlateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        platePanel.add(licensePlateField);

        JLabel formatLabel = new JLabel("(Carro: ABC123 | Moto: ABC12A)");
        formatLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        formatLabel.setForeground(new Color(127, 140, 141));
        platePanel.add(formatLabel);

        formPanel.add(platePanel);

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(127, 140, 141));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
        formPanel.add(statusLabel);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setOpaque(false);

        registerButton = new JButton("✓ Registrar Ingreso");
        registerButton.setPreferredSize(new Dimension(180, 40));
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(46, 204, 113));
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> handleRegistration());

        // Hover effect
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(new Color(46, 204, 113));
            }
        });

        clearButton = new JButton("🗑️ Limpiar");
        clearButton.setPreferredSize(new Dimension(120, 40));
        clearButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        clearButton.setForeground(Color.WHITE);
        clearButton.setBackground(new Color(149, 165, 166));
        clearButton.setFocusPainted(false);
        clearButton.setBorderPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> clearFields());

        // Hover effect
        clearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                clearButton.setBackground(new Color(127, 140, 141));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                clearButton.setBackground(new Color(149, 165, 166));
            }
        });

        buttonsPanel.add(registerButton);
        buttonsPanel.add(clearButton);
        formPanel.add(buttonsPanel);

        leftPanel.add(formPanel, BorderLayout.NORTH);

        // Ticket display panel
        JPanel ticketPanel = new JPanel(new BorderLayout());
        ticketPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            "Ticket Generado",
            0,
            0,
            new Font("Segoe UI", Font.BOLD, 13),
            new Color(52, 73, 94)
        ));
        ticketPanel.setBackground(Color.WHITE);

        ticketDisplayArea = new JTextArea(15, 50);
        ticketDisplayArea.setEditable(false);
        ticketDisplayArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        ticketDisplayArea.setBackground(new Color(250, 250, 250));
        ticketDisplayArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(ticketDisplayArea);
        scrollPane.setBorder(null);
        ticketPanel.add(scrollPane, BorderLayout.CENTER);

        leftPanel.add(ticketPanel, BorderLayout.CENTER);

        // Enter key action
        licensePlateField.addActionListener(e -> handleRegistration());

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setPreferredSize(new Dimension(280, 0));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            "Código QR",
            0,
            0,
            new Font("Segoe UI", Font.BOLD, 13),
            new Color(52, 73, 94)
        ));
        rightPanel.setBackground(Color.WHITE);

        // QR display
        qrLabel = new JLabel("", SwingConstants.CENTER);
        qrLabel.setPreferredSize(new Dimension(240, 240));
        qrLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        qrLabel.setBackground(Color.WHITE);
        qrLabel.setOpaque(true);

        JPanel qrContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        qrContainer.setOpaque(false);
        qrContainer.add(qrLabel);
        rightPanel.add(qrContainer, BorderLayout.CENTER);

        // Print button panel
        JPanel printPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        printPanel.setOpaque(false);
        printPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        printButton = new JButton("🖨️ Imprimir Ticket");
        printButton.setPreferredSize(new Dimension(200, 40));
        printButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        printButton.setForeground(Color.WHITE);
        printButton.setBackground(new Color(52, 152, 219));
        printButton.setFocusPainted(false);
        printButton.setBorderPainted(false);
        printButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printButton.setEnabled(false);
        printButton.addActionListener(e -> handlePrint());

        // Hover effect
        printButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (printButton.isEnabled()) {
                    printButton.setBackground(new Color(41, 128, 185));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (printButton.isEnabled()) {
                    printButton.setBackground(new Color(52, 152, 219));
                }
            }
        });

        printPanel.add(printButton);
        rightPanel.add(printPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private void handleRegistration() {
        String licensePlate = licensePlateField.getText().trim().toUpperCase();

        // Validate empty
        if (licensePlate.isEmpty()) {
            showError("Por favor ingrese una placa");
            return;
        }

        // Validate format
        if (!ValidationUtils.isLicensePlateValid(licensePlate)) {
            showError(
                "Formato de placa inválido.\n" +
                "Carro: ABC123 (3 letras + 3 números)\n" +
                "Moto: ABC12A (3 letras + 2 números + 1 letra)"
            );
            return;
        }

        // Check for existing open ticket
        Ticket existingTicket = ticketService.getAllOpenTickets().stream()
            .filter(t -> t.getLicensePlate().equals(licensePlate))
            .findFirst()
            .orElse(null);

        if (existingTicket != null) {
            showWarning(
                "Ya existe un ticket abierto para esta placa.\n" +
                "Folio: " + existingTicket.getFolio()
            );
            return;
        }

        // Detect vehicle type
        VehicleTypeEnum vehicleType = ValidationUtils.detectVehicleType(licensePlate);

        // Check subscription
        CustomerTypeEnum customerType = CustomerTypeEnum.GUEST;
        if (subscriptionService.hasActiveSubscription(licensePlate)) {
            customerType = CustomerTypeEnum.SUBSCRIPTION;
            statusLabel.setText("✓ Vehículo con mensualidad vigente");
            statusLabel.setForeground(new Color(39, 174, 96));
        } else {
            statusLabel.setText("ℹ️ Vehículo invitado (se cobrará por tiempo)");
            statusLabel.setForeground(new Color(52, 152, 219));
        }

        // Register entry
        Ticket ticket = ticketService.registerEntry(
            licensePlate,
            customerType,
            vehicleType,
            authService.getCurrentOperator().getId()
        );

        if (ticket != null) {
            lastTicket = ticket;
            displayTicket(ticket);
            generateAndDisplayQR(ticket);
            printButton.setEnabled(true);

            showSuccess(
                "Ingreso registrado exitosamente.\n" +
                "Folio: " + ticket.getFolio() + "\n" +
                "Tipo: " + customerType.getDescription() + "\n" +
                "Vehículo: " + vehicleType.getDescription()
            );

            licensePlateField.setText("");
            licensePlateField.requestFocus();
        } else {
            showError("Error al registrar el ingreso");
        }
    }

    private void handlePrint() {
        if (lastTicket == null) {
            showWarning("No hay ticket para imprimir");
            return;
        }

        // Try to print on Xprinter first, fallback to dialog
        boolean printed = printerHelper.printOnXprinter(lastTicket, authService.getCurrentOperator());

        if (!printed) {
            // If Xprinter failed, show print dialog
            int choice = JOptionPane.showConfirmDialog(
                this,
                "No se encontró la Xprinter XP-58IIT.\n¿Desea usar otra impresora?",
                "Seleccionar Impresora",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                printed = printerHelper.printWithDialog(lastTicket, authService.getCurrentOperator());
                if (printed) {
                    showSuccess("Ticket enviado a imprimir");
                }
            }
        } else {
            showSuccess("Ticket impreso en Xprinter XP-58IIT");
        }
    }

    private void displayTicket(Ticket ticket) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("           CRUDPARK PARKING SYSTEM         \n");
        sb.append("═══════════════════════════════════════════\n\n");
        sb.append("  TICKET DE INGRESO\n\n");
        sb.append("  Folio: ").append(ticket.getFolio()).append("\n");
        sb.append("  Placa: ").append(ticket.getLicensePlate()).append("\n");
        sb.append("  Tipo Cliente: ").append(ticket.getCustomerType().getDescription()).append("\n");
        sb.append("  Tipo Vehículo: ").append(ticket.getVehicleType().getDescription()).append("\n");
        sb.append("  Fecha Ingreso: ").append(DateUtils.formatForTicket(ticket.getEntryDate())).append("\n");
        sb.append("  Operador: ").append(authService.getCurrentOperator().getName()).append("\n\n");
        sb.append("═══════════════════════════════════════════\n");
        sb.append("  Conserve este ticket para la salida\n");
        sb.append("═══════════════════════════════════════════\n");

        ticketDisplayArea.setText(sb.toString());
    }

    private void generateAndDisplayQR(Ticket ticket) {
        long timestamp = ticket.getEntryDate().getTime();
        BufferedImage qrImage = qrGenerator.generateQR(
            ticket.getId(),
            ticket.getLicensePlate(),
            timestamp
        );

        if (qrImage != null) {
            ImageIcon qrIcon = new ImageIcon(qrImage);
            qrLabel.setIcon(qrIcon);
            qrLabel.setText("");
        } else {
            qrLabel.setIcon(null);
            qrLabel.setText("Error al generar QR");
        }
    }

    private void clearFields() {
        licensePlateField.setText("");
        ticketDisplayArea.setText("");
        qrLabel.setIcon(null);
        qrLabel.setText("");
        statusLabel.setText(" ");
        lastTicket = null;
        printButton.setEnabled(false);
        licensePlateField.requestFocus();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}