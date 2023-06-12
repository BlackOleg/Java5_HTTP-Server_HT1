import java.io.IOException;

public interface ConnectionListener {
    void onConnectionReady(Connection connection);
    void onReceiveHandler(Connection connection, String in) throws IOException;
    void onDisconnect(Connection connection);
    void onException(Connection connection, Exception e);
    String received (Connection connection, String msg);
}
