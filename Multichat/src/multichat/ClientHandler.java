package multichat;

/**
 *
 * @author iman907
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import javax.swing.JTextArea;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ArrayList<Socket> clientSockets;
    private BufferedReader reader;
    private JTextArea ta_chat;

    public ClientHandler(Socket socket, ArrayList<Socket> clientSockets, JTextArea ta_chat) {
        this.clientSocket = socket;
        this.clientSockets = clientSockets;
        this.ta_chat = ta_chat;
        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error initializing client input stream: " + e.getMessage());
        }
    }

    ClientHandler(Socket clientSocket, ArrayList<Socket> clientSockets) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void run() {
        String message;
        String[] data;
        String connect = "Connect", disconnect = "Disconnect", chat = "Chat";

        try {
            while ((message = reader.readLine()) != null) {
                ta_chat.append("Received: " + message + "\n");
                data = message.split(":");

                for (String token : data) {
                    ta_chat.append(token + "\n");
                }

                if (data[2].equals(connect)) {
                    sendMessageToAllClients(data[0] + ":" + data[1] + ":" + chat);
                    userAdd(data[0]);
                } else if (data[2].equals(disconnect)) {
                    sendMessageToAllClients(data[0] + ": has disconnected: " + chat);
                    userRemove(data[0]);
                } else if (data[2].equals(chat)) {
                    sendMessageToAllClients(message);
                } else {
                    ta_chat.append("No conditions were met.\n");
                }
            }
        } catch (SocketException se) {
            ta_chat.append("Connection reset by peer: " + se.getMessage() + "\n");
            clientSockets.remove(clientSocket);
        } catch (IOException e) {
            ta_chat.append("Error reading from client: " + e.getMessage() + "\n");
            clientSockets.remove(clientSocket);
        } finally {
            // Menutup soket klien
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException ex) {
                ta_chat.append("Error closing socket: " + ex.getMessage() + "\n");
            }
        }
    }

    // Mengirim pesan ke semua klien
    private void sendMessageToAllClients(String message) {
        for (Socket socket : clientSockets) {
            try {
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(message);
            } catch (IOException e) {
                System.err.println("Error sending message to client: " + e.getMessage());
            }
        }
    }

    // Placeholder untuk menambah pengguna
    private void userAdd(String username) {
        ta_chat.append("User " + username + " added.\n");
    }

    // Placeholder untuk menghapus pengguna
    private void userRemove(String username) {
        ta_chat.append("User " + username + " removed.\n");
    }
}
