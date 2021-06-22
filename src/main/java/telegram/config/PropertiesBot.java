package telegram.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesBot {
    private static Properties properties = new Properties();
    static {
        try {
            properties.load(new FileInputStream(new File("src/main/resources/telegram.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getToken(){
        return properties.getProperty("telegram.token");
    }

    public static String getUsername(){
        return properties.getProperty("telegram.username");
    }
    public static String getAdmin(){
        return properties.getProperty("telegram.admin");
    }
}
