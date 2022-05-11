import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final static String IP_address = "localhost";
    private final static int PORT = 8981;
    private final static int WIDTH = 600;
    private final static int HEIGHT = 400;

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickName = new JTextField("Ska");
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickName, BorderLayout.NORTH);

        setVisible(true);

        try {
            connection = new TCPConnection(this, IP_address, PORT);
        } catch (IOException exception) {
            printMessage("Start. TCPConnection exception: " + exception);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String value = fieldInput.getText();
        if (value.equals("")) {
            return;
        }
        fieldInput.setText(null);
        connection.sendMessage(fieldNickName.getText() + ": " + value);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String message) {
        printMessage(message);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception exception) {
        printMessage("In process. TCPConnection exception: " + exception);
    }

    private synchronized void printMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(message + "\n");
                log.setCaretPosition(log.getDocument().getLength());

            }
        });
    }
}
