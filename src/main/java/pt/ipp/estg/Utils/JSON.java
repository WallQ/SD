package pt.ipp.estg.Utils;

import com.google.gson.Gson;
import pt.ipp.estg.Entities.User;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The {@code JSON} class provides methods for loading and saving data in JSON format.
 * It is specifically tailored for handling user data and log entries in the application.
 * Utilizes the Gson library for JSON processing.
 *
 * @author Carlos Leite, Sergio Felix
 * @version 1.0
 */
public class JSON {
    /**
     * The file path for storing user data in JSON format.
     */
    private static final String USERS_FILE_PATH = Resources.getPathFromResources("users.json");
    /** The file path for storing log entries in JSON format. */
    private static final String LOGS_FILE_PATH = Resources.getPathFromResources("logs.json");

    /**
     * Loads user data from the JSON file.
     *
     * @return An ArrayList of User objects loaded from the JSON file.
     */
    public static ArrayList<User> loadUsers() {
        ArrayList<User> users = new ArrayList<>();
        Gson gson = new Gson();

        try (Reader fileReader = new FileReader(USERS_FILE_PATH)) {
            User[] userArray = gson.fromJson(fileReader, User[].class);
            if (userArray != null) users.addAll(Arrays.asList(userArray));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Saves a User object to the JSON file.
     *
     * @param user The User object to be saved.
     */
    public static void saveUser(User user) {
        Gson gson = new Gson();
        ArrayList<User> users = loadUsers();

        try (FileWriter fileWriter = new FileWriter(USERS_FILE_PATH)) {
            users.add(user);
            gson.toJson(users, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads log entries from the JSON file.
     *
     * @return An ArrayList of LogEntry objects loaded from the JSON file.
     */
    public static ArrayList<Logger.LogEntry> loadLogs() {
        ArrayList<Logger.LogEntry> logs = new ArrayList<>();
        Gson gson = new Gson();

        try (Reader fileReader = new FileReader(LOGS_FILE_PATH)) {
            Logger.LogEntry[] logsArray = gson.fromJson(fileReader, Logger.LogEntry[].class);
            if (logsArray != null) logs.addAll(Arrays.asList(logsArray));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logs;
    }

    /**
     * Saves a LogEntry object to the JSON file.
     *
     * @param logEntry The LogEntry object to be saved.
     */
    public static void saveLog(Logger.LogEntry logEntry) {
        Gson gson = new Gson();
        ArrayList<Logger.LogEntry> logEntries = loadLogs();

        try (FileWriter fileWriter = new FileWriter(LOGS_FILE_PATH)) {
            logEntries.add(logEntry);
            gson.toJson(logEntries, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
