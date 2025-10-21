package com.crudzaso.crudpark.ui;

import com.crudzaso.crudpark.service.AuthService;
import com.crudzaso.crudpark.ui.panels.DashboardPanel;
import com.crudzaso.crudpark.ui.panels.IngresoVehiculoPanel;
import com.crudzaso.crudpark.ui.panels.SalidaVehiculoPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window with modern tabbed interface
 * UI in Spanish for end users, code in English for developers
 */
public class MainFrame extends JFrame {
    private final AuthService authService;
    private JTabbedPane tabbedPane;
    private JLabel operatorLabel;
    private DashboardPanel dashboardPanel;
    private IngresoVehiculoPanel entryPanel;
    private SalidaVehiculoPanel exitPanel;

    public MainFrame(AuthService authService) {
        this.authService = authService;
        initComponents();
    }

    private void initComponents() {
        setTitle("CrudPark - Sistema de Parqueadero");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Modern header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane with modern styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.setBackground(Color.WHITE);

        // Dashboard tab
        dashboardPanel = new DashboardPanel(authService);
        tabbedPane.addTab("📊 Dashboard", dashboardPanel);

        // Entry tab
        entryPanel = new IngresoVehiculoPanel(authService);
        tabbedPane.addTab("🚗 Ingreso de Vehículo", entryPanel);

        // Exit tab
        exitPanel = new SalidaVehiculoPanel(authService);
        tabbedPane.addTab("🚀 Salida de Vehículo", exitPanel);

        // Refresh dashboard when switching tabs
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                dashboardPanel.refresh();
            }
        });

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        headerPanel.setBackground(new Color(41, 128, 185));

        // Left side - title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        JLabel iconLabel = new JLabel("🅿️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));

        JLabel titleLabel = new JLabel("CrudPark");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);
        headerPanel.add(leftPanel, BorderLayout.WEST);

        // Right side - operator info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        // Operator info panel
        JPanel operatorInfoPanel = new JPanel();
        operatorInfoPanel.setLayout(new BoxLayout(operatorInfoPanel, BoxLayout.Y_AXIS));
        operatorInfoPanel.setOpaque(false);

        JLabel operatorTitleLabel = new JLabel("Operador:");
        operatorTitleLabel.setForeground(new Color(236, 240, 241));
        operatorTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        operatorTitleLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        operatorLabel = new JLabel(authService.getCurrentOperator().getName());
        operatorLabel.setForeground(Color.WHITE);
        operatorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        operatorLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        operatorInfoPanel.add(operatorTitleLabel);
        operatorInfoPanel.add(operatorLabel);
        rightPanel.add(operatorInfoPanel);

        // Logout button
        JButton logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setPreferredSize(new Dimension(120, 32));
        logoutButton.addActionListener(e -> handleLogout());

        // Hover effect
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(192, 57, 43));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(231, 76, 60));
            }
        });

        rightPanel.add(logoutButton);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea cerrar sesión?",
            "Confirmar Cierre de Sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();

            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
}