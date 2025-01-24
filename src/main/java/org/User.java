package org;

import java.io.Serializable;
import java.util.Scanner;
import java.util.Random;

public class User implements Serializable {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private static final Random rand = new Random();

    public User(int id, String username, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String username, String firstName, String lastName) {
        this.id = rand.nextInt();
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public static User readUser(Scanner scanner) {
        System.out.println("Hello! Enter your username: ");
        String username = scanner.nextLine();
        System.out.println("Enter your first name: ");
        String firstName = scanner.nextLine();
        System.out.println("Enter your last name: ");
        String lastName = scanner.nextLine();
        int id = rand.nextInt();

        return new User(id, username, firstName, lastName);

    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username=" + username +
                ", firstname=" + firstName +
                ", lastname=" + lastName +
                '}';
    }
}
