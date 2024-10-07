package multichat;

/**
 *
 * @author iman907
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class server_frame extends javax.swing.JFrame {
    String username = "Server", address = "localhost";
    ArrayList<PrintWriter> clientOutputStreams;
    
    ArrayList<String> users;
    int port = 2222;
    PrintWriter writer;
    BufferedReader reader;
    Socket socket;
    Boolean isConnected = false;

    ArrayList<String> userss = new ArrayList<>();

    private void ListenThread() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void sendDisconnect() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void Disconnect() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;
        PrintWriter client;

        public ClientHandler(Socket clientSocket, PrintWriter user) {
            client = user;
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                ta_chat.append("Unexpected error... \n");
            }
        }

        public void ListenThread() {
            Thread incomingReader = new Thread(new IncomingReader());
            incomingReader.start();
        }

        public void userAdd(String data) {
            users.add(data);
            ta_chat.append(data + " has connected.\n");
            ta_online.append(data + "\n");
            System.out.println("User added: " + data);  // Tambahkan untuk debugging
        }

        public void userRemove(String data) {
            ta_chat.append(data + " is now offline.\n");
            users.remove(data);
            ta_chat.append(data + " has disconnected.\n");
            ta_online.setText(""); // Hapus semua pengguna lama
            for (String user : users) {
                ta_online.append(user + "\n"); // Tambahkan ulang daftar pengguna yang tersisa
            }
            System.out.println("User removed: " + data);  // Tambahkan untuk debugging
        }

        public void writeUsers() {
            String[] tempList = new String[users.size()];
            users.toArray(tempList);
            for (String token : tempList) {
                // Uncomment to append users to ta_chat
                // ta_chat.append(token + "\n");
            }
        }

        public void sendDisconnect() {
            String bye = username + ": :Disconnect";
            try {
                writer.println(bye);
                writer.flush();
            } catch (Exception e) {
                ta_chat.append("Could not send Disconnect message.\n");
            }
        }

        public void Disconnect() {
            try {
                ta_chat.append("Disconnected.\n");
                sock.close();
            } catch (Exception ex) {
                ta_chat.append("Failed to disconnect.\n");
            }
            isConnected = false;
        }

        public class IncomingReader implements Runnable {
            @Override
            public void run() {
                String[] data;
                String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat";

                try {
                    while ((stream = reader.readLine()) != null) {
                        data = stream.split(":");
                        if (data[2].equals(chat)) {
                            ta_chat.append(data[0] + ": " + data[1] + "\n");
                            ta_chat.setCaretPosition(ta_chat.getDocument().getLength());
                        } else if (data[2].equals(connect)) {
                            ta_chat.removeAll();
                            userAdd(data[0]);
                        } else if (data[2].equals(disconnect)) {
                            userRemove(data[0]);
                        } else if (data[2].equals(done)) {
                            writeUsers();
                            userss.clear();
                        }
                    }
                } catch (Exception ex) {
                    // Exception handling
                }
            }
        }

        @Override
        public void run() {
            String message, connect = "Connect", disconnect = "Disconnect", chat = "Chat";
            String[] data;

            try {
                while ((message = reader.readLine()) != null) {
                    ta_chat.append("Received: " + message + "\n");
                    data = message.split(":");

                    for (String token : data) {
                        ta_chat.append(token + "\n");
                    }

                    if (data[2].equals(connect)) {
                        tellEveryone(data[0] + ":" + data[1] + ":" + chat);
                        userAdd(data[0]);
                    } else if (data[2].equals(disconnect)) {
                        tellEveryone(data[0] + ":has disconnected.:" + chat);
                        userRemove(data[0]);
                    } else if (data[2].equals(chat)) {
                        tellEveryone(message);
                    } else {
                        ta_chat.append("No Conditions were met. \n");
                    }
                }
            } catch (Exception ex) {
                ta_chat.append("Lost a connection. \n");
                ex.printStackTrace();
                clientOutputStreams.remove(client);
            }
        }
    }

    public server_frame() {
        initComponents();
    }

    private void initComponents() {
        jScrollPane1 = new javax.swing.JScrollPane();
        ta_chat = new javax.swing.JTextArea();
        b_start = new javax.swing.JButton();
        b_end = new javax.swing.JButton();
        b_users = new javax.swing.JButton();
        b_clear = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        ta_online = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        tf_chat = new javax.swing.JTextField();
        b_send = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat - Server's frame");
        setResizable(false);

        ta_chat.setColumns(20);
        ta_chat.setRows(5);
        jScrollPane1.setViewportView(ta_chat);

        b_start.setText("START");
        b_start.addActionListener(evt -> b_startActionPerformed(evt));

        b_end.setText("END");
        b_end.addActionListener(evt -> b_endActionPerformed(evt));

        b_users.setText("Online Users");
        b_users.addActionListener(evt -> b_usersActionPerformed(evt));

        b_clear.setText("Clear");
        b_clear.addActionListener(evt -> b_clearActionPerformed(evt));

        ta_online.setColumns(20);
        ta_online.setRows(5);
        ta_online.setText("Online User :");
        jScrollPane2.setViewportView(ta_online);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); 
        jLabel1.setText("Currently Online");

        b_send.setText("Send");
        b_send.addActionListener(evt -> b_sendActionPerformed(evt));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(tf_chat, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(b_send, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(b_start, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(b_end, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(b_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(b_users, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addContainerGap())
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(b_start)
                                .addComponent(b_end))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(b_send)
                                .addComponent(tf_chat, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(b_clear)
                        .addComponent(b_users))
                    .addContainerGap())
        );

        pack();
    }

    private void b_endActionPerformed(java.awt.event.ActionEvent evt) {
        serverStop();
        try {
            Thread.sleep(2000);    
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        tellEveryone("Server:is stopping and all users will be disconnected.:Chat");
        ta_chat.append("Server stopping... \n");
    }

    private void b_startActionPerformed(java.awt.event.ActionEvent evt) {
        Thread starter = new Thread(new ServerStart());
        starter.start();
        ta_chat.append("Server started...\n");
    }

    private void b_sendActionPerformed(java.awt.event.ActionEvent evt) {
        String nothing = "";
        if (!tf_chat.getText().equals(nothing)) {
            tellEveryone(username + ":" + tf_chat.getText() + ":Chat");
            tf_chat.setText("");
        }
    }

    private void b_usersActionPerformed(java.awt.event.ActionEvent evt) {
        ta_chat.append("\n Online users: \n");
        for (String current_user : users) {
            ta_chat.append(current_user + "\n");
        }
        
        // Update juga di ta_online jika diperlukan
        ta_online.setText("Online Users:\n");
        for (String user : users) {
            ta_online.append(user + "\n");
        }
    }

    private void b_clearActionPerformed(java.awt.event.ActionEvent evt) {
        ta_chat.setText("");
    }

    public class ServerStart implements Runnable {
        @Override
        public void run() {
            clientOutputStreams = new ArrayList<>();
            users = new ArrayList<>();
            try {
                ServerSocket serverSock = new ServerSocket(2222);

                while (true) {
                    Socket clientSock = serverSock.accept();
                    PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
                    clientOutputStreams.add(writer);

                    Thread listener = new Thread(new ClientHandler(clientSock, writer));
                    listener.start();
                    ta_chat.append("Got a connection. \n");
                }
            } catch (Exception ex) {
                ta_chat.append("Error making a connection. \n");
            }
        }
    }

    public void tellEveryone(String message) {

        for (PrintWriter writer : clientOutputStreams) {
            try {
                writer.println(message);
                writer.flush();
            } catch (Exception ex) {
                ta_chat.append("Error telling everyone. \n");
            }
        }
    }
    private ServerSocket serverSock;

    public void serverStop() { // Inisialisasi ArrayList
        // Inisialisasi ArrayList

        try {
            // Kirim pesan kepada semua klien bahwa server akan berhenti
            tellEveryone("Server:is stopping and all users will be disconnected.:Chat");

            // Tutup semua stream output ke klien
            for (PrintWriter writer : clientOutputStreams) {
                writer.close();
            }

            // Tutup server socket sehingga tidak ada klien baru yang bisa terhubung
            if (serverSock != null && !serverSock.isClosed()) {
                serverSock.close();
            }

            // Kosongkan daftar pengguna dan koneksi
            clientOutputStreams.clear();
            users.clear();

            // Perbarui UI
            ta_chat.append("Server stopped.\n");
            ta_online.setText("Online Users:\n");

        } catch (IOException ex) {
            ta_chat.append("Error stopping the server.\n");
        }
    }
    
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new server_frame().setVisible(true));
    }


    private javax.swing.JButton b_clear;
    private javax.swing.JButton b_end;
    private javax.swing.JButton b_send;
    private javax.swing.JButton b_start;
    private javax.swing.JButton b_users;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea ta_chat;
    private javax.swing.JTextArea ta_online;
    private javax.swing.JTextField tf_chat;
}
