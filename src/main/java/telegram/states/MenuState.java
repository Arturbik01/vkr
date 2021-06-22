package telegram.states;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegram.config.PropertiesBot;

import java.util.ArrayList;
import java.util.List;

public class MenuState extends DefaultAbsSender {
    private Message message;

    public MenuState(Message message) {
        super(new DefaultBotOptions());
        this.message = message;
        SendMessage sendMessage = new SendMessage(message.getChatId().toString(), "Menu");
        sendMessage.setReplyMarkup(initReplayKeyboard());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup initReplayKeyboard(){
        KeyboardRow firstRow = new KeyboardRow();
        KeyboardButton library = new KeyboardButton();
        library.setText("Библиотека");
        firstRow.add(library);

        KeyboardRow secondRow = new KeyboardRow();
        KeyboardButton films = new KeyboardButton();
        films.setText("Кинозал");
        secondRow.add(films);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(firstRow);
        keyboard.add(secondRow);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);

        return markup;
    }

    @Override
    public String getBotToken() {
        return PropertiesBot.getToken();
    }
}
