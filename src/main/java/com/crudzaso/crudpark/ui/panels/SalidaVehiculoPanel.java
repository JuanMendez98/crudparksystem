package com.crudzaso.crudpark.ui.panels;

import com.crudzaso.crudpark.model.Ticket;
import com.crudzaso.crudpark.model.enums.CustomerTypeEnum;
import com.crudzaso.crudpark.service.AuthService;
import com.crudzaso.crudpark.service.TicketService;
import com.crudzaso.crudpark.util.DateUtils;
import com.crudzaso.crudpark.util.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Modern panel for vehicle exit and payment processing
 * UI in Spanish for end users, code in English for developers
 */
public class SalidaVehiculoPanel extends JPanel {
    private final AuthService authService;
    private final TicketService ticketService;

    private JTextField licensePlateField;
    private JButton searchButton;
    private JLabel folioLabel;
    private JLabel customerTypeLabel;
    private JLabel vehicleTypeLabel;
    private JLabel entryDateLabel;
    private JLabel stayTimeLabel;
    private JLabel amountLabel;
    private JComboBox<String> paymentMethodCombo;
    private JButton registerExitButton;
    private JPanel ticketInfoPanel;

    private Ticket currentTicket;

    public SalidaVehiculoPanel(AuthService authService) {
        this.authService = authService;
        this.ticketService = new TicketService();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("🚀 Registro de Salida de Vehículo");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 73, 94));
        add(titleLabel, BorderLayout.NORTH);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // Search panel
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Ticket info panel
        ticketInfoPanel = createTicketInfoPanel();
        mainPanel.add(ticketInfoPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Exit button panel
        JPanel exitButtonPanel = createExitButtonPanel();
        mainPanel.add(exitButtonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        searchPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            "Buscar Ticket",
            0,
            0,
            new Font("Segoe UI", Font.BOLD, 13),
            new Color(52, 73, 94)
        ));
        searchPanel.setBackground(Color.WHITE);

        JLabel plateLabel = new JLabel("Placa:");
        plateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchPanel.add(plateLabel);

        licensePlateField = new JTextField(12);
        licensePlateField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        licensePlateField.setPreferredSize(new Dimension(150, 35));
        licensePlateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchPanel.add(licensePlateField);

        searchButton = new JButton("🔍 Buscar");
        searchButton.setPreferredSize(new Dimension(120, 35));
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> searchTicket());

        // Hover effect
        searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchButton.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchButton.setBackground(new Color(52, 152, 219));
            }
        });

        searchPanel.add(searchButton);

        // Enter key action
        licensePlateField.addActionListener(e -> searchTicket());

        return searchPanel;
    }

    private JPanel createTicketInfoPanel() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            "Información del Ticket",
            0,
            0,
            new Font("Segoe UI", Font.BOLD, 13),
            new Color(52, 73, 94)
        ));
        infoPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Folio
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel folioTitleLabel = new JLabel("Folio:");
        folioTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(folioTitleLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        folioLabel = new JLabel("-");
        folioLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        folioLabel.setForeground(new Color(52, 73, 94));
        infoPanel.add(folioLabel, gbc);

        // Row 2: Customer Type
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel customerTypeTitleLabel = new JLabel("Tipo de Cliente:");
        customerTypeTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(customerTypeTitleLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        customerTypeLabel = new JLabel("-");
        customerTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(customerTypeLabel, gbc);

        // Row 3: Vehicle Type
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel vehicleTypeTitleLabel = new JLabel("Tipo de Vehículo:");
        vehicleTypeTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(vehicleTypeTitleLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        vehicleTypeLabel = new JLabel("-");
        vehicleTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(vehicleTypeLabel, gbc);

        // Row 4: Entry Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel entryDateTitleLabel = new JLabel("Fecha de Ingreso:");
        entryDateTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(entryDateTitleLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        entryDateLabel = new JLabel("-");
        entryDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(entryDateLabel, gbc);

        // Row 5: Stay Time
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        JLabel stayTimeTitleLabel = new JLabel("Tiempo de Estadía:");
        stayTimeTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(stayTimeTitleLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        stayTimeLabel = new JLabel("-");
        stayTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        stayTimeLabel.setForeground(new Color(52, 152, 219));
        infoPanel.add(stayTimeLabel, gbc);

        // Row 6: Amount
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        JLabel amountTitleLabel = new JLabel("Monto a Cobrar:");
        amountTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoPanel.add(amountTitleLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        amountLabel = new JLabel("-");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        amountLabel.setForeground(new Color(39, 174, 96));
        infoPanel.add(amountLabel, gbc);

        // Row 7: Payment Method
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;
        JLabel paymentMethodTitleLabel = new JLabel("Método de Pago:");
        paymentMethodTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoPanel.add(paymentMethodTitleLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        paymentMethodCombo = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia", "Nequi", "Daviplata"});
        paymentMethodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        paymentMethodCombo.setPreferredSize(new Dimension(200, 30));
        paymentMethodCombo.setEnabled(false);
        infoPanel.add(paymentMethodCombo, gbc);

        return infoPanel;
    }

    private JPanel createExitButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        registerExitButton = new JButton("✓ Registrar Salida");
        registerExitButton.setPreferredSize(new Dimension(200, 45));
        registerExitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerExitButton.setForeground(Color.WHITE);
        registerExitButton.setBackground(new Color(231, 76, 60));
        registerExitButton.setFocusPainted(false);
        registerExitButton.setBorderPainted(false);
        registerExitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerExitButton.setEnabled(false);
        registerExitButton.addActionListener(e -> registerExit());

        // Hover effect
        registerExitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (registerExitButton.isEnabled()) {
                    registerExitButton.setBackground(new Color(192, 57, 43));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (registerExitButton.isEnabled()) {
                    registerExitButton.setBackground(new Color(231, 76, 60));
                }
            }
        });

        buttonPanel.add(registerExitButton);

        return buttonPanel;
    }

    private void searchTicket() {
        String licensePlate = licensePlateField.getText().trim().toUpperCase();

        if (licensePlate.isEmpty()) {
            showError("Por favor ingrese una placa");
            return;
        }

        if (!ValidationUtils.isLicensePlateValid(licensePlate)) {
            showError("Formato de placa inválido");
            return;
        }

        // Search for open ticket
        currentTicket = ticketService.getAllOpenTickets().stream()
            .filter(t -> t.getLicensePlate().equals(licensePlate))
            .findFirst()
            .orElse(null);

        if (currentTicket == null) {
            showWarning("No se encontró un ticket abierto para la placa: " + licensePlate);
            clearFields();
            return;
        }

        displayTicketInfo();
    }

    private void displayTicketInfo() {
        folioLabel.setText(currentTicket.getFolio());
        customerTypeLabel.setText(currentTicket.getCustomerType().getDescription());
        vehicleTypeLabel.setText(currentTicket.getVehicleType().getDescription());
        entryDateLabel.setText(DateUtils.formatForTicket(currentTicket.getEntryDate()));
        stayTimeLabel.setText(ticketService.getFormattedStayTime(currentTicket));

        // Calculate amount (temporarily create exit ticket to calculate)
        if (currentTicket.getCustomerType() == CustomerTypeEnum.SUBSCRIPTION) {
            amountLabel.setText("$0 (Mensualidad)");
            amountLabel.setForeground(new Color(39, 174, 96));
            paymentMethodCombo.setEnabled(false);
        } else {
            // We'll calculate the amount when processing exit
            amountLabel.setText("A calcular...");
            amountLabel.setForeground(new Color(243, 156, 18));
            paymentMethodCombo.setEnabled(true);
        }

        registerExitButton.setEnabled(true);
    }

    private void registerExit() {
        if (currentTicket == null) {
            return;
        }

        String paymentMethod = (String) paymentMethodCombo.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Confirmar salida del vehículo " + currentTicket.getLicensePlate() + "?",
            "Confirmar Salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Ticket exitTicket = ticketService.processExit(
            currentTicket.getLicensePlate(),
            authService.getCurrentOperator().getId(),
            paymentMethod
        );

        if (exitTicket != null) {
            String message = "Salida registrada exitosamente.\n" +
                "Folio: " + exitTicket.getFolio() + "\n" +
                "Placa: " + exitTicket.getLicensePlate() + "\n" +
                "Tiempo de estadía: " + ticketService.getFormattedStayTime(exitTicket);

            if (exitTicket.getAmountCharged().compareTo(BigDecimal.ZERO) > 0) {
                message += "\nMonto cobrado: $" + exitTicket.getAmountCharged();
                message += "\nMétodo de pago: " + paymentMethod;
            } else {
                message += "\nMonto: $0 (Mensualidad)";
            }

            showSuccess(message);

            clearFields();
            licensePlateField.setText("");
            licensePlateField.requestFocus();
        } else {
            showError("Error al registrar la salida");
        }
    }

    private void clearFields() {
        currentTicket = null;
        folioLabel.setText("-");
        customerTypeLabel.setText("-");
        vehicleTypeLabel.setText("-");
        entryDateLabel.setText("-");
        stayTimeLabel.setText("-");
        amountLabel.setText("-");
        paymentMethodCombo.setEnabled(false);
        registerExitButton.setEnabled(false);
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