import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class Connection implements Runnable {
    private final Socket socket;
    private Thread rxThread;
    private final BufferedReader in;
    private final BufferedOutputStream out;
    private final ConnectionListener eventListener;

    public Connection(ConnectionListener eventListener, String ipAddress, int port) throws IOException{
        this(eventListener, new Socket(ipAddress,port));
    }
    public Connection(ConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        //in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedOutputStream(socket.getOutputStream());

    }

    public synchronized void sendOut(String msg) {
        try {
            out.write((msg + "\r\n").getBytes());
            out.flush();
        } catch (IOException e) {
            eventListener.onException(Connection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(Connection.this, e);
        }
    }
    @Override
    public String toString() {
        return "Connection: " + socket.getInetAddress()+ " port: " + socket.getPort();
    }

    @Override
    public void run() {
        try {
            eventListener.onConnectionReady(Connection.this);
            while (rxThread.isAlive()) {
                eventListener.onReceiveHandler(Connection.this, in.readLine());
            }

        } catch (IOException e) {
            eventListener.onException(Connection.this, e);
        } finally {
            eventListener.onDisconnect(Connection.this);
        }
    }

    public void go() {
        rxThread = new Thread(Connection.this);
        rxThread.start();
    }
}
