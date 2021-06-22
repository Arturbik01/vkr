package managers;

import org.telegram.telegrambots.meta.api.objects.Message;
import telegram.config.DatabaseFactory;
import telegram.states.MenuState;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DelegateState {
    private long chatId;
    private String username;
    private String state;

    public DelegateState(long chatId, @Nullable String username, String state) {
        this.chatId = chatId;
        this.username = username;
        this.state = state;
    }

    public static void setState(long chatId, String state){
        try {
            DatabaseFactory.getConnection().createStatement()
                    .execute(String.format("UPDATE users SET state = '%s' WHERE chatId = %d", state, chatId));
            } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void setState(){
        DelegateState.setState(chatId, state);
    }

    public static String getState(long chatId){
        String state = null;
        try {
            ResultSet resultSet = DatabaseFactory.getConnection().createStatement()
                    .executeQuery(String.format("SELECT state FROM users WHERE chatId = %d", chatId));
            state = resultSet.next() ? resultSet.getString("state") : null;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return state;
    }

    public String getState(){
        return DelegateState.getState(chatId);
    }


}

