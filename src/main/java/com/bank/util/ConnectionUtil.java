package com.bank.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {

    private static final Properties props = new Properties();

    // Static block: runs ONCE, when the class is first used
    static {
        try (InputStream in = ConnectionUtil.class.getClassLoader()
                                   .getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("db.properties not found in src/main/resources");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Could not load db.properties", e);
        }
    }

    private ConnectionUtil() {}   // utility class: nobody should create one with new

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            props.getProperty("url"),
            props.getProperty("username"),
            props.getProperty("password"));
    }
}