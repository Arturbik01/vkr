import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import telegram.MainBot;

public class Main implements Runnable{
    private static Main main;
    public static void main(String[] args){
        new Thread(new RegisterFiles()).start();



    }

    public static Main getInstance(){
        if (main == null){
            main = new Main();
        }
        return main;
    }

    @Override
    public void run() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(new MainBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
