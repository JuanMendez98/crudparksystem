package com.crudzaso.crudpark;

import com.crudzaso.crudpark.ui.LoginFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

/**
 * Main entry point for CrudPark application
 * Initializes the UI with modern FlatLaf theme
 */
public class Main {

    public static void main(String[] args) {
        // Set FlatLaf modern look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            // Customize FlatLaf properties for better appearance
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.width", 12);
            UIManager.put("TabbedPane.selectedBackground", java.awt.Color.WHITE);

        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf Look and Feel. Using system default.");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Start application on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}