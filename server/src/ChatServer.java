import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {

    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server running...");
        try(ServerSocket socket = new ServerSocket(8981)) {
            while (true) {
                try {
                    new TCPConnection(this, socket.accept());
                } catch (IOException exception) {
                    System.out.println("TCPConnection exception: " + exception);
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client connected: " + tcpConnection);
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String message) {
        sendToAllConnections(message);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected: " + tcpConnection);
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception exception) {
        System.out.println("! TCPConnection exception: " + exception);
    }

    private void sendToAllConnections(String value) {
        System.out.println("Message: " + value);
        connections.forEach(connection -> connection.sendMessage(value));
    }
}
