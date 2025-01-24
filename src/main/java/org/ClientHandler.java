package org;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static org.SerializeToBase64.deserializeObjectFromBase64;
import static org.SerializeToBase64.serializeObjectToBase64;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlerArraysList = new ArrayList<ClientHandler>();       // se tine lista cu clientii
    public Socket socket;                       // stabileste conexiune client - server
    private BufferedReader bufferedReader;      // se citesc mesaje
    private BufferedWriter bufferedWriter;      // se trimit mesajele la alti clienti
    private User user;

    public ClientHandler(Socket socket) throws IOException {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String base64MessageString = bufferedReader.readLine();
            MessageProtocol messageProtocol = (MessageProtocol) deserializeObjectFromBase64(base64MessageString);
            this.user = messageProtocol.getUser();

            // adaugam client-ul in lista de clienti
            clientHandlerArraysList.add(this);

            MessageProtocol messageProtocolToBroadcastFromServer = new MessageProtocol("A wild " + user.getUsername() + " has entered the chat. First name is: "
                    + user.getFirstName() + " and last name is: " + user.getLastName(), Server.ServerUser);

            String base64MessageToSend = serializeObjectToBase64(messageProtocolToBroadcastFromServer);

            broadcastMessage(base64MessageToSend);

        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {

                messageFromClient = bufferedReader.readLine();                   // functie blocking. Se asteapta citirea
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlerArraysList) {
            try {
                if (clientHandler.user.getId() != this.user.getId()) {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();                     // este echivalent cu apasarea enter
                    clientHandler.bufferedWriter.flush();                       // mesajele se trimit pe buffered fortat (inainte sa fie full)
                }
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        try {
            clientHandlerArraysList.remove(this);
            MessageProtocol messageProtocolToBroadcastFromServer = new MessageProtocol( "User "+ user.getUsername() + " has left the chat.", Server.ServerUser);

            String base64MessageToSend = serializeObjectToBase64(messageProtocolToBroadcastFromServer);

            broadcastMessage(base64MessageToSend);
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();                                 // se inchise si socker iostream
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
