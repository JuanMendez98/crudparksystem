package com.crudzaso.crudpark.ui.panels;

import com.crudzaso.crudpark.model.Ticket;
import com.crudzaso.crudpark.service.AuthService;
import com.crudzaso.crudpark.service.TicketService;
import com.crudzaso.crudpark.util.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Modern dashboard panel showing real-time parking information
 * UI in Spanish for end users, code in English for developers
 */
public class DashboardPanel extends JPanel {
    private final AuthService authService;
    private final TicketService ticketService;
    private JTable ticketsTable;
    private DefaultTableModel tableModel;
    private JLabel vehiclesInsideLabel;
    private JLabel totalCarsLabel;
    private JLabel totalMotorcyclesLabel;
    private JButton refreshButton;

    public DashboardPanel(AuthService authService) {
        this.authService = authService;
        this.ticketService = new TicketService();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Header with stats cards
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Footer with info
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("📊 Dashboard - Vehículos en el Parqueadero");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(52, 73, 94));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // Stats cards panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Card 1: Total vehicles
        JPanel card1 = createStatCard("🚗 Total Vehículos", "0", new Color(52, 152, 219));
        vehiclesInsideLabel = (JLabel) ((JPanel) card1.getComponent(1)).getComponent(0);
        statsPanel.add(card1);

        // Card 2: Cars
        JPanel card2 = createStatCard("🚙 Carros", "0", new Color(46, 204, 113));
        totalCarsLabel = (JLabel) ((JPanel) card2.getComponent(1)).getComponent(0);
        statsPanel.add(card2);

        // Card 3: Motorcycles
        JPanel card3 = createStatCard("🏍️ Motos", "0", new Color(155, 89, 182));
        totalMotorcyclesLabel = (JLabel) ((JPanel) card3.getComponent(1)).getComponent(0);
        statsPanel.add(card3);

        headerPanel.add(statsPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        valuePanel.setOpaque(false);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);

        valuePanel.add(valueLabel);

        card.add(titleLabel);
        card.add(valuePanel);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setOpaque(false);

        // Table header
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);

        JLabel tableTitle = new JLabel("Vehículos Actuales");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tableTitle.setForeground(new Color(52, 73, 94));
        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);

        refreshButton = new JButton("🔄 Actualizar");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(120, 32));
        refreshButton.addActionListener(e -> loadData());

        // Hover effect
        refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(new Color(52, 152, 219));
            }
        });

        tableHeaderPanel.add(refreshButton, BorderLayout.EAST);
        tablePanel.add(tableHeaderPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Folio", "Placa", "Tipo Cliente", "Tipo Vehículo", "Hora Ingreso", "Tiempo Transcurrido"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ticketsTable = new JTable(tableModel);
        ticketsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ticketsTable.setRowHeight(30);
        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketsTable.setShowGrid(true);
        ticketsTable.setGridColor(new Color(236, 240, 241));
        ticketsTable.setSelectionBackground(new Color(52, 152, 219, 50));
        ticketsTable.setSelectionForeground(new Color(52, 73, 94));

        // Header styling
        JTableHeader header = ticketsTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < ticketsTable.getColumnCount(); i++) {
            ticketsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Column widths
        ticketsTable.getColumnModel().getColumn(0).setPreferredWidth(100);  // Folio
        ticketsTable.getColumnModel().getColumn(1).setPreferredWidth(100);  // Placa
        ticketsTable.getColumnModel().getColumn(2).setPreferredWidth(120);  // Tipo Cliente
        ticketsTable.getColumnModel().getColumn(3).setPreferredWidth(120);  // Tipo Vehículo
        ticketsTable.getColumnModel().getColumn(4).setPreferredWidth(150);  // Hora Ingreso
        ticketsTable.getColumnModel().getColumn(5).setPreferredWidth(150);  // Tiempo

        JScrollPane scrollPane = new JScrollPane(ticketsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel infoLabel = new JLabel("ℹ️ Los datos se actualizan automáticamente al presionar 'Actualizar'");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(127, 140, 141));
        footerPanel.add(infoLabel);

        return footerPanel;
    }

    private void loadData() {
        tableModel.setRowCount(0);

        List<Ticket> openTickets = ticketService.getAllOpenTickets();

        int totalCars = 0;
        int totalMotorcycles = 0;

        for (Ticket ticket : openTickets) {
            // Count by vehicle type
            switch (ticket.getVehicleType()) {
                case CAR:
                    totalCars++;
                    break;
                case MOTORCYCLE:
                    totalMotorcycles++;
                    break;
            }

            // Calculate elapsed time
            LocalDateTime entryTime = ticket.getEntryDate().toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(entryTime, now);
            long minutes = duration.toMinutes();

            Object[] row = {
                ticket.getFolio(),
                ticket.getLicensePlate(),
                ticket.getCustomerType().getDescription(),
                ticket.getVehicleType().getDescription(),
                DateUtils.formatForTicket(ticket.getEntryDate()),
                DateUtils.formatStayTime((int) minutes)
            };

            tableModel.addRow(row);
        }

        // Update stats cards
        vehiclesInsideLabel.setText(String.valueOf(openTickets.size()));
        totalCarsLabel.setText(String.valueOf(totalCars));
        totalMotorcyclesLabel.setText(String.valueOf(totalMotorcycles));
    }

    public void refresh() {
        loadData();
    }
}