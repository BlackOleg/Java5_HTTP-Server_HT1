import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Server implements ConnectionListener {
    private final int port;
    private List<Connection> connections;
    final List<String> validPaths = List.of(
            "/index.html",
            "/spring.svg",
            "/spring.png",
            "/resources.html",
            "/styles.css",
            "/app.js",
            "/links.html",
            "/forms.html",
            "/classic.html",
            "/events.html",
            "/events.js");

    public Server() {
        this.port = 9999;
    }

    public Server(int port) {
        this.port = port;
    }

    public void go() {
        connections = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("got a connection on port: " + port);

            while (true) {
                try {
                    new Connection(this, serverSocket.accept()).go();

                } catch (IOException e) {
                    System.out.println("TCPConnection Exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onConnectionReady(Connection connection) {
        connections.add(connection);
        System.out.println("Client connected! WELCOME - " + connection);
    }

    @Override
    public void onReceiveHandler(Connection connection, String in) throws RuntimeException, IOException {
        var out = new StringBuilder();

        final var requestLine = in;
        if (in.equals("")){
            //onDisconnect(connection);
            return;
        }
        final var parts = requestLine.split(" ");

        if (parts.length != 3) {
            // just close socket
            onDisconnect(connection);
            return;
        }
        final var path = parts[1];
        if (!validPaths.contains(path)) {
            out.append("HTTP/1.1 404 Not Found\r\n");
            out.append("Content-Length: 0\r\n");
            out.append("Connection: close\r\n");
            connection.sendOut(String.valueOf(out));
            return;
        }


        final Path filePath = Path.of(".", "public", path);
        final var mimeType = Files.probeContentType(filePath);

        // special case for classic
        if (path.equals("/classic.html")) {
            final String template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.append("HTTP/1.1 200 OK\r\n");
            out.append("Content-Type: " + mimeType + "\r\n");
            out.append("Content-Length: " + content.length + "\r\n");
            out.append("Connection: close\r\n");
            connection.sendOut(String.valueOf(out));
            return;
        }

    }

    @Override
    public void onDisconnect(Connection connection) {
        connections.remove(connection);
        System.out.println("Client disconnected! GOODBYE - " + connection);
    }

    @Override
    public void onException(Connection connection, Exception e) {
        System.out.println("TCPConnection Exception: " + e);
    }

    @Override
    public String received(Connection connection, String msg) {
        return msg;
    }
}
