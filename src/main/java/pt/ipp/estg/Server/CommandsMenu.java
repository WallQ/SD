package pt.ipp.estg.Server;

/**
 * The {@code CommandsMenu} class provides static methods to generate command menus
 * for different categories, such as authentication, messaging, offensive actions, and management.
 * These commands can be used in an application to perform various actions.
 *
 * @author Carlos Leite, Sergio Felix
 * @version 1.0
 */
public class CommandsMenu {
    /**
     * Generates a command menu for authentication-related commands.
     *
     * @return A string containing authentication-related commands.
     */
    public static String AuthenticationCommands() {
        return """
                [Authentication Commands]
                /sign-up {username} {email} {password} {role (Private, Sergeant, Lieutenant, General)}
                /sign-in {email} {password}
                """;
    }

    /**
     * Generates a command menu for messaging-related commands.
     *
     * @return A string containing messaging-related commands.
     */
    public static String MessageCommands() {
        return """
                [Message Commands]
                /whisper {username} {message}
                /say {role (Private, Sergeant, Lieutenant, General)} {message}
                /all {message}
                /room {room name} {message}
                /create-room {room name}
                /join-room {room name}
                /leave-room {room name}
                /list-rooms
                """;
    }

    /**
     * Generates a command menu for offensive action-related commands.
     *
     * @return A string containing offensive action-related commands.
     */
    public static String OffensiveCommands() {
        return """
                [Offensive Commands]
                /launch-missile {location} {reason}
                """;
    }

    /**
     * Generates a command menu for management-related commands.
     *
     * @return A string containing management-related commands.
     */
    public static String ManagementCommands() {
        return """
                [Management Commands]
                /list-requests
                /accept-request {id}
                /reject-request {id}
                /promote {username} {role (Private, Sergeant, Lieutenant, General)}
                /demote {username} {role (Private, Sergeant, Lieutenant, General)}
                """;
    }
}
