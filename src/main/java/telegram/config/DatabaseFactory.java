package telegram.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseFactory {
    private static String url = PropertiesDB.getUrl();
    private static String user = PropertiesDB.getUser();
    private static String password = PropertiesDB.getPassword();
    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static Connection getConnection(){
        return connection;
    }


}
