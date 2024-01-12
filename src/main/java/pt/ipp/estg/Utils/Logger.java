package pt.ipp.estg.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The {@code Logger} class provides a simple logging mechanism for recording
 * actions along with relevant details such as IP address, action type, and message.
 *
 * @author Carlos Leite, Sergio Felix
 * @version 1.0
 */
public class Logger {
    /**
     * Logs the specified details including IP address, action type, and message.
     *
     * @param ipAddress  The IP address associated with the action.
     * @param actionType The type of action being logged.
     * @param message    The detailed message describing the action.
     */
    public static void log(String ipAddress, String actionType, String message) {
        LogEntry logEntry = new LogEntry(getCurrentTime(), ipAddress, actionType, message);
        JSON.saveLog(logEntry);
    }

    /**
     * Gets the current time in the format "dd/MM/yyyy HH:mm:ss".
     *
     * @return The current time as a formatted string.
     */
    private static String getCurrentTime() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }

    /**
     * The {@code LogEntry} class represents a single log entry with details
     * such as date and time, IP address, action type, and message.
     */
    public static class LogEntry {
        /**
         * The date and time of the log entry.
         */
        public String dateTime;
        /**
         * The IP address associated with the log entry.
         */
        public String ipAddress;
        /**
         * The type of action being logged.
         */
        public String actionType;
        /**
         * The detailed message describing the action.
         */
        public String message;

        /**
         * Constructs a new {@code LogEntry} with the specified details.
         *
         * @param dateTime   The date and time of the log entry.
         * @param ipAddress  The IP address associated with the log entry.
         * @param actionType The type of action being logged.
         * @param message    The detailed message describing the action.
         */
        public LogEntry(String dateTime, String ipAddress, String actionType, String message) {
            this.dateTime = dateTime;
            this.ipAddress = ipAddress;
            this.actionType = actionType;
            this.message = message;
        }
    }
}
