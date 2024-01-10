package pt.ipp.estg.Entities;

import pt.ipp.estg.Utils.JSON;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static void log(String ipAddress, String actionType, String message) {
        LogEntry logEntry = new LogEntry(getCurrentTime(), ipAddress, actionType, message);
        JSON.saveLog(logEntry);
    }

    private static String getCurrentTime() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }

    public static class LogEntry {
        public String dateTime;
        public String ipAddress;
        public String actionType;
        public String message;

        public LogEntry(String dateTime, String ipAddress, String actionType, String message) {
            this.dateTime = dateTime;
            this.ipAddress = ipAddress;
            this.actionType = actionType;
            this.message = message;
        }
    }
}
