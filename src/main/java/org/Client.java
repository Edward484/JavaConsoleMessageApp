package org;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static org.SerializeToBase64.deserializeObjectFromBase64;
import static org.SerializeToBase64.serializeObjectToBase64;

public class Client {

    public Socket socket;                       // stabileste conexiune client - server
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private User user;

    public Client(Socket socket, User user) {
        try {
            this.socket = socket;
            this.user = user;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }

    }

    public void sendMessage() {
        try {
            //bufferedWriter.write(clientUsername);
            MessageProtocol messageProtocolWithNewUser = new MessageProtocol("", user);
            String base64FirstMessageString = serializeObjectToBase64(messageProtocolWithNewUser);
            bufferedWriter.write(base64FirstMessageString);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                System.out.print(user.getUsername() + ": ");
                String clientMessage = scanner.nextLine();
                MessageProtocol messageProtocolToSend = new MessageProtocol(clientMessage, user);
                String base64MessageString = serializeObjectToBase64(messageProtocolToSend);
                bufferedWriter.write(base64MessageString);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    // se asteapta mesaje
    public void listenMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromChatBase64;

                while (socket.isConnected()) {
                    try {
                        messageFromChatBase64 = bufferedReader.readLine();
                        MessageProtocol deserializedMessageProtocol = (MessageProtocol) deserializeObjectFromBase64(messageFromChatBase64);
                        generateBlankSpace();
                        System.out.println(deserializedMessageProtocol.getUser().getUsername() + ": " + deserializedMessageProtocol.getMessage());
                        System.out.print(user.getUsername() + ": ");
                    } catch (IOException | ClassNotFoundException e) {
                        closeAll(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    private void generateBlankSpace() {
        int length = user.getUsername().length();
        StringBuilder backspaces = new StringBuilder();
        for (int i = 0; i < length * 10; i++) {
            backspaces.append("\b");
        }
        System.out.print(backspaces);
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        User user = User.readUser(scanner);
        Socket socket = new Socket("localhost", (Integer) Constants.DEFAULT_PORT);
        Client client = new Client(socket, user);
        client.listenMessages();
        client.sendMessage();
    }
}
