package multichat;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerStart implements Runnable {
    private ServerSocket serverSocket;
    private ArrayList<Socket> clientSockets;

    public ServerStart() {
        clientSockets = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            // Menginisialisasi ServerSocket pada port 2222
            serverSocket = new ServerSocket(2222);
            System.out.println("Server is listening on port 2222...");

            while (!serverSocket.isClosed()) {
                // Menerima koneksi dari client
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Menambahkan client socket ke daftar
                clientSockets.add(clientSocket);

                // Membuat thread untuk menangani client
                new Thread(new ClientHandler(clientSocket, clientSockets)).start();
            }
        } catch (IOException e) {
            System.err.println("Error in Server: " + e.getMessage());
        } finally {
            // Menutup server socket saat server berhenti
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Could not close server socket: " + e.getMessage());
            }
        }
    }

    // Getter untuk serverSocket
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    // Getter untuk clientSockets
    public ArrayList<Socket> getClientSockets() {
        return clientSockets;
    }
}
