package com.crudzaso.crudpark.util;

import com.crudzaso.crudpark.model.enums.VehicleTypeEnum;
import java.util.regex.Pattern;

/**
 * Utilities for data validation
 * Supports license plate validation for cars and motorcycles
 */
public class ValidationUtils {

    // Pattern for CAR plates: 3 letters + 3 numbers (ABC123)
    private static final Pattern CAR_PLATE_PATTERN = Pattern.compile("^[A-Z]{3}\\d{3}$");

    // Pattern for MOTORCYCLE plates: 3 letters + 2 numbers + 1 letter (ABC12A)
    private static final Pattern MOTORCYCLE_PLATE_PATTERN = Pattern.compile("^[A-Z]{3}\\d{2}[A-Z]$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Password must contain at least one letter and one number
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).+$");

    /**
     * Validates if the license plate is valid (car or motorcycle)
     */
    public static boolean isLicensePlateValid(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return false;
        }
        String plateUpper = plate.trim().toUpperCase();
        return CAR_PLATE_PATTERN.matcher(plateUpper).matches()
            || MOTORCYCLE_PLATE_PATTERN.matcher(plateUpper).matches();
    }

    /**
     * Validates if the license plate is for a CAR (ABC123)
     */
    public static boolean isCarPlate(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return false;
        }
        return CAR_PLATE_PATTERN.matcher(plate.trim().toUpperCase()).matches();
    }

    /**
     * Validates if the license plate is for a MOTORCYCLE (ABC12A)
     */
    public static boolean isMotorcyclePlate(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return false;
        }
        return MOTORCYCLE_PLATE_PATTERN.matcher(plate.trim().toUpperCase()).matches();
    }

    /**
     * Automatically detects the vehicle type by license plate format
     */
    public static VehicleTypeEnum detectVehicleType(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return VehicleTypeEnum.CAR; // Default
        }

        String plateUpper = plate.trim().toUpperCase();

        if (MOTORCYCLE_PLATE_PATTERN.matcher(plateUpper).matches()) {
            return VehicleTypeEnum.MOTORCYCLE;
        } else if (CAR_PLATE_PATTERN.matcher(plateUpper).matches()) {
            return VehicleTypeEnum.CAR;
        }

        return VehicleTypeEnum.CAR; // Default if no match
    }

    /**
     * Returns the expected format for the vehicle type
     */
    public static String getPlateFormat(VehicleTypeEnum type) {
        return type == VehicleTypeEnum.MOTORCYCLE ? "ABC12A" : "ABC123";
    }

    public static boolean isEmailValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates password: must contain at least one letter and one number,
     * and be between 8 and 30 characters long
     */
    public static boolean isPasswordValid(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        String pwd = password.trim();

        // Check length (8 to 30 characters)
        if (pwd.length() < 8 || pwd.length() > 30) {
            return false;
        }

        // Check that it contains at least one letter and one number
        return PASSWORD_PATTERN.matcher(pwd).matches();
    }

    public static String formatLicensePlate(String plate) {
        if (plate == null) {
            return null;
        }
        return plate.trim().toUpperCase();
    }

    public static boolean isStringEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isPositiveNumber(Number number) {
        return number != null && number.doubleValue() > 0;
    }
}
