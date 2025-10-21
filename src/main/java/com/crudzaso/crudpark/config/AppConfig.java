package com.crudzaso.crudpark.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class to load and manage application configuration
 * from the application.properties file
 * Implements Singleton pattern for global configuration access
 */
public class AppConfig {
    private static AppConfig instance;
    private Properties properties;

    private AppConfig() {
        properties = new Properties();
        loadProperties();
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                System.err.println("Could not find application.properties file");
                return;
            }

            properties.load(input);
            System.out.println("Configuration loaded successfully");

        } catch (IOException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
        }
    }

    // Database configuration
    public String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public String getDbUser() {
        return properties.getProperty("db.user");
    }

    public String getDbPassword() {
        return properties.getProperty("db.password");
    }

    // Application settings
    public int getGracePeriod() {
        return Integer.parseInt(properties.getProperty("app.grace.period", "30"));
    }

    public String getPrinterName() {
        return properties.getProperty("app.printer.name", "Thermal Printer");
    }

    public int getTicketWidth() {
        return Integer.parseInt(properties.getProperty("app.ticket.width", "32"));
    }

    public String getTicketCompany() {
        return properties.getProperty("app.ticket.company", "CrudPark - Crudzaso");
    }

    public String getTicketFarewellMessage() {
        return properties.getProperty("app.ticket.farewell.message", "Gracias por su visita.");
    }

    // QR code settings
    public int getQrWidth() {
        return Integer.parseInt(properties.getProperty("qr.width", "200"));
    }

    public int getQrHeight() {
        return Integer.parseInt(properties.getProperty("qr.height", "200"));
    }

    public String getQrFormat() {
        return properties.getProperty("qr.format", "TICKET:%d|PLATE:%s|DATE:%d");
    }

    // Generic property access
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
