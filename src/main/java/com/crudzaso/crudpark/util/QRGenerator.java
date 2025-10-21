package com.crudzaso.crudpark.util;

import com.crudzaso.crudpark.config.AppConfig;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;

/**
 * QR code generator for tickets
 */
public class QRGenerator {
    private final AppConfig config;

    public QRGenerator() {
        this.config = AppConfig.getInstance();
    }

    public BufferedImage generateQR(int ticketId, String licensePlate, long timestamp) {
        String content = String.format(
                config.getQrFormat(),
                ticketId,
                licensePlate,
                timestamp
        );

        return generateQRFromText(content);
    }

    public BufferedImage generateQRFromText(String text) {
        try {
            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrWriter.encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    config.getQrWidth(),
                    config.getQrHeight()
            );

            return MatrixToImageWriter.toBufferedImage(bitMatrix);

        } catch (WriterException e) {
            System.err.println("Error generating QR: " + e.getMessage());
            return null;
        }
    }
}