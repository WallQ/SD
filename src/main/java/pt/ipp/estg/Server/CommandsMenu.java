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
                /say {role} {message}
                /all {message}
                /create-room {room name}
                /join-room {room name}
                """;
    }
}
