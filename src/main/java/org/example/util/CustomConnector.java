package org.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CustomConnector {
    public Connection getConnection(String url) throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        return conn;
    }
    public Connection getConnection (String url, String user, String password) throws SQLException{
        Connection conn = DriverManager.getConnection(url, user, password);
        return conn;
    }
}
