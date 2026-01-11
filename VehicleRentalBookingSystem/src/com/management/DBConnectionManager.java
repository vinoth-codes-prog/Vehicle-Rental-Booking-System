package com.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnectionManager {
    private static Connection conn;

    public static synchronized Connection getConnection() throws Exception {
        if (conn != null && !conn.isClosed()) return conn;

        Properties props = new Properties();
        // try classpath first (works when database.properties is in src/ or resources/)
        InputStream in = DBConnectionManager.class.getClassLoader().getResourceAsStream("database.properties");

        try {
            if (in == null) {
                // fallback locations (useful during development)
                File f = new File("src/database.properties");
                if (!f.exists()) f = new File("database.properties");        // project root
                if (!f.exists()) f = new File("bin/database.properties");    // eclipse output
                if (!f.exists()) {
                    throw new java.io.FileNotFoundException("database.properties not found in classpath or fallback locations. Put it in src/ or project root.");
                }
                in = new FileInputStream(f);
            }

            props.load(in);

            String driver = props.getProperty("db.driver");
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String pass = props.getProperty("db.password");

            if (driver == null || url == null) {
                throw new IllegalStateException("database.properties missing required properties (db.driver, db.url).");
            }

            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("☑ DB connected successfully.");
            return conn;
        } finally {
            if (in != null) try { in.close(); } catch (Exception ignored) {}
        }
    }
}
