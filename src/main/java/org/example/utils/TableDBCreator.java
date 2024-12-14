package org.example.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TableDBCreator {

    private final String DB_URL;
    private final String DB_USER;
    private final String DB_PASSWORD;

    public TableDBCreator(String DB_URL, String DB_USER, String DB_PASSWORD) {
        this.DB_URL = DB_URL;
        this.DB_USER = DB_USER;
        this.DB_PASSWORD = DB_PASSWORD;
    }

    public void checkTablesAndCreateIfNonExistent() {
        try (Connection connection = DriverManager.getConnection(this.DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            String createUsersTable = """
                    CREATE TABLE IF NOT EXISTS users (
                        username VARCHAR(20) PRIMARY KEY,
                        first_name VARCHAR(50),
                        last_name VARCHAR(50),
                        profile_image VARCHAR(200),
                        bio TEXT
                    );
                    """;
            statement.execute(createUsersTable);

            String createUserCredentialsTable = """
                    CREATE TABLE IF NOT EXISTS usercredentials (
                        username VARCHAR(20) PRIMARY KEY,
                        password VARCHAR(255),
                        FOREIGN KEY (username) REFERENCES users(username)
                    );
                    """;
            statement.execute(createUserCredentialsTable);

            String createMessagesTable = """
                    CREATE TABLE IF NOT EXISTS messages (
                        message VARCHAR(100),
                        from_username VARCHAR(20),
                        to_username VARCHAR(20),
                        date TIMESTAMP,
                        from_replied_username VARCHAR(20),
                        to_replied_username VARCHAR(20),
                        date_of_replied TIMESTAMP,
                        PRIMARY KEY (from_username, to_username, date),
                        FOREIGN KEY (from_username) REFERENCES users(username),
                        FOREIGN KEY (to_username) REFERENCES users(username),
                        FOREIGN KEY (from_replied_username, to_replied_username, date_of_replied) REFERENCES messages(from_username, to_username, date)
                    );
                    """;
            statement.execute(createMessagesTable);

            String createFriendshipRequestsTable = """
                    CREATE TABLE IF NOT EXISTS friendshiprequests (
                        from_username VARCHAR(20),
                        to_username VARCHAR(20),
                        status VARCHAR(20),
                        friendship_request_date TIMESTAMP,
                        PRIMARY KEY (from_username, to_username),
                        FOREIGN KEY (from_username) REFERENCES users(username),
                        FOREIGN KEY (to_username) REFERENCES users(username)
                    );
                    """;
            statement.execute(createFriendshipRequestsTable);

            String createFriendshipsTable = """
                    CREATE TABLE IF NOT EXISTS friendships (
                        username_1 VARCHAR(20),
                        username_2 VARCHAR(20),
                        friendship_date TIMESTAMP,
                        PRIMARY KEY (username_1, username_2),
                        FOREIGN KEY (username_1) REFERENCES users(username),
                        FOREIGN KEY (username_2) REFERENCES users(username)
                    );
                    """;
            statement.execute(createFriendshipsTable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
