package org;

import java.io.Serializable;
import java.util.Random;

public class MessageProtocol implements Serializable {
    private int id;
    private String message;
    private User user;
    private static final Random rand = new Random();


    public MessageProtocol(String message, User user) {
        this.message = message;
        this.user = user;
        this.id = rand.nextInt();
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }


    @Override
    public String toString() {
        return "MessageProtocol{" +
                "user=" + (user != null ? user.toString() : "null") +
                ", message='" + message + '\'' +
                '}';
    }
}
