package telegram.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesDB {
    private static Properties properties = new Properties();
    static {
        try {
            properties.load(new FileInputStream(new File("src/main/resources/connection.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getUrl(){
        return properties.getProperty("database.url");
    }

    public static String getUser(){
        return properties.getProperty("database.user");
    }

    public static String getPassword(){
        return properties.getProperty("database.password");
    }
}
