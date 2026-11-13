package com.library.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe",
                "pavi",
                "YOUR_ORACLE_PASSWORD"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
