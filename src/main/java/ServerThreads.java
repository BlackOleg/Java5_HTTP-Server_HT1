import java.io.*;
import java.net.*;

public class ServerThreads extends Thread {
    private final Socket socket;

    public ServerThreads(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            final var out = new BufferedOutputStream(socket.getOutputStream());
            String line = in.readLine();

            if (line != null) {
                System.out.println("Request is: " + line);
                Request request = new Request(line);
                request.parse();
                Response response = new Response(out);
                response.setRequest(request);
                response.handler();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

