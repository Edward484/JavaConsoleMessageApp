package org;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public static final User ServerUser = new User("Server", "admin", "admin");

    public void startServer() {
        try {
            System.out.println("Server started");
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();                  // metoda e de tip blocking
                System.out.println("Client connected");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);             // clasa ClientHandler implementeaza runnable
                thread.start();


            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            System.out.println("Closing server socket");
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket1 = new ServerSocket((Integer) Constants.DEFAULT_PORT);
        Server server = new Server(serverSocket1);
        server.startServer();
    }
}
