package com.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DLException extends Exception {
    private Exception originalException;
    private List<String> additionalInfo;
    private static final String DEFAULT_MESSAGE = "Unable to complete operation. Please contact the administrator.";
    private static final String LOG_FILE = "error_log.txt";

    public DLException(Exception e) {
        super(DEFAULT_MESSAGE);
        this.originalException = e;
        this.additionalInfo = new ArrayList<>();
        log();
    }

    public DLException(Exception e, String... info) {
        super(DEFAULT_MESSAGE);
        this.originalException = e;
        this.additionalInfo = Arrays.asList(info);
        log();
    }

    public void log() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write("Timestamp: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writer.newLine();
            writer.write("Exception Type: " + originalException.getClass().getName());
            writer.newLine();
            writer.write("Message: " + originalException.getMessage());
            writer.newLine();

            if (originalException instanceof SQLException) {
                SQLException sqlE = (SQLException) originalException;
                writer.write("SQLState: " + sqlE.getSQLState());
                writer.newLine();
                writer.write("Vendor Error Code: " + sqlE.getErrorCode());
                writer.newLine();
            }

            if (additionalInfo != null && !additionalInfo.isEmpty()) {
                writer.write("Additional Info: " + String.join(", ", additionalInfo));
                writer.newLine();
            }

            writer.write("Stack Trace:");
            writer.newLine();
            for (StackTraceElement element : originalException.getStackTrace()) {
                writer.write("	at " + element.toString());
                writer.newLine();
            }
            writer.write("--------------------------------------------------------------------------------");
            writer.newLine();
        } catch (IOException ioe) {
            System.err.println("Failed to write to log file: " + ioe.getMessage());
        }
    }
}
