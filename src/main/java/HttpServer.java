import java.io.*;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HttpServer {
    private final int port;
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "public";
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
    private boolean shutdown = false;


    public HttpServer() {
        this.port = 9999;
    }

    public HttpServer(int port) {
        this.port = port;
    }

    public void go() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port: " + port);
            // Loop waiting for a request
            while (true) {
                try {
                    final var socket = serverSocket.accept();
                    System.out.println("New client connected");
                    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(64);
                    //threadPoolExecutor.execute(new ServerThreads(socket));
                    new ServerThreads(socket).start();
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
