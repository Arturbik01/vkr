package telegram.users;

import telegram.config.DatabaseFactory;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private String chatId;
    private String username;
    private String state;

    public User(String chatId, String state, @Nullable String username) {
        this.chatId = chatId;
        this.username = username;
        this.state = state;
    }

    public static String getUserStateByChatId(String chatId){
        String state = null;
        try {
            ResultSet resultSet =DatabaseFactory.getConnection().createStatement()
                    .executeQuery(String.format("SELECT state FROM users" +
                    " where chatId = %d", Integer.parseInt(chatId)));
            if (resultSet.next()){
                state = resultSet.getString(1);
            }else {
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return state != null ? state : null;
    }

    public void addUserToDatabase(){
        try {
            int chatId = Integer.parseInt(this.chatId);
            DatabaseFactory.getConnection().createStatement().execute(
                    String.format("INSERT users(chatId, username, state) values (%d, '%s', '%s')", chatId, username, state));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
