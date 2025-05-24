package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database{
    private String url;
    private String username;
    private String password;

    public Database() throws ClassNotFoundException{
        Class.forName("org.postgresql.Driver");
        this.url = "jdbc:postgresql://172.24.122.195:5432/yapyapyap";
        this.username = System.getenv("DB_USERNAME");
        this.password = System.getenv("DB_PASSWORD");
    }

    public Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url, username, password);
    }
}
