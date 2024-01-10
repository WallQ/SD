package pt.ipp.estg.Server;

public class CommandsMenu {
    public static String AuthenticationCommands() {
        return """
                [Authentication Commands]
                /sign-up {username} {email} {password} {role (Private, Sergeant, Lieutenant, General)}
                /sign-in {email} {password}
                """;
    }

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

    public static String OffensiveCommands() {
        return """
                [Offensive Commands]
                /launch-missile {location} {reason}
                """;
    }

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
