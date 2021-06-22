package telegram.states;

import managers.DelegateState;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegram.config.PropertiesBot;
import telegram.entity.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookState extends DefaultAbsSender {
    private boolean hasChange = false;
    public BookState(Message message, String state, boolean stateHasChange) {
        super(new DefaultBotOptions());
        if (message.getText().startsWith("/start")){
            try {
                SendMessage startMessage = new SendMessage(message.getChatId().toString(), "Добро пожаловать в библиотеку\n" +
                        "Введите название книги или воспользуйтесь поиском\n" +
                        "Для корректного и удобного чтения с мобильных устройств, скачайте Fb2 Reader.");
                startMessage.setReplyMarkup(initReplyKeyboardMarkup("start"));
                execute(startMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }
        List<String> distinct = null;
        switch (message.getText()){
            case "Список названий":
                distinct = new Files().getDistinct("name");
                break;
            case "Список жанров":
                distinct = new Files().getDistinct("genre");
                break;
            case "Список авторов":
                distinct = new Files().getDistinct("author");
                break;
            case "Список по годам":
                distinct = new Files().getDistinct("year");
                break;
        }
        if (distinct != null){
            String temp;
            temp = "Количество элементов: " + distinct.size() + "\n";
            for (int i = 0; i < distinct.size(); i++){
                temp += distinct.get(i) + "\n";
            }
            SendMessage distinctMessage = new SendMessage(message.getChatId().toString(), temp);
            distinctMessage.setReplyMarkup(initReplyKeyboardMarkup(state));
            try {
                execute(distinctMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }
        this.hasChange = stateHasChange;
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyMarkup(initReplyKeyboardMarkup(state));
        String tag = null;
        List<String> names = null;
        switch (state){
            case "searchByName":
                tag = "name";
//                names = searchByName(message.getText().trim());
                break;
            case "searchByGenre":
                tag = "genre";
//                names = searchByGenre(message.getText().trim());
                break;
            case "searchByAuthor":
                tag = "author";
//                names = searchByAuthor(message.getText().trim());
                break;
            case "searchByYear":
                tag = "year";
//                names = searchByYear(message.getText().trim());
                break;
            default:
                tag = "book";
                break;
        }
        if (!hasChange) {
            names = new Files().searchByTarget(message.getText(), tag);

            if (names.size() == 1){
                List<String> book = new Files().searchAll(names.get(0), "name").get(0);
                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(message.getChatId().toString());
                File file = new Files().getEntity(names.get(0), "book");
                InputFile inputFile = new InputFile(file, names.get(0).concat(".fb2"));
                sendDocument.setDocument(inputFile);
                sendDocument.setReplyMarkup(initReplyKeyboardMarkup(state));
                try {
                    if(file.isFile()) {
                        execute(sendDocument);
                    }
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                String text;
                text = book.get(0) + "\n";
                text += "Автор: " + book.get(1) + "\n";
                text += "Год: " + book.get(2) + "\n";
                text += "Жанр: " + book.get(3) + "\n";
                text += "Описание: " + (book.get(4) == null ? "Нет описания" : book.get(4));

                try {
                    if (file.isFile()) {
                        execute(new SendMessage(message.getChatId().toString(), text));
                    }else{
                        execute(new SendMessage(message.getChatId().toString(), "Эта книга временно недоступна"));
                    }
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }else if(names.size() > 1){
                List<List<String>> books = new Files().searchAll(message.getText(), tag);
                String text = "";
                try {
                    execute(new SendMessage(message.getChatId().toString(), "Получилось несколько совпадений, введите название книги"));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < books.size(); i++){
                    List<String> book = books.get(i);
                    text = book.get(0) + "\n";
                    text += "Автор: " + book.get(1) + "\n";
                    text += "Год: " + book.get(2) + "\n";
                    text += "Жанр: " + book.get(3) + "\n";
                    text += "Описание: " + book.get(4) == null ? "Нет описания" : book.get(4);

                    try {
                        SendMessage message1 = new SendMessage(message.getChatId().toString(), text);
                        message1.setReplyMarkup(initReplyKeyboardMarkup(state));
                        execute(message1);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                DelegateState.setState(message.getChatId(), "searchByName");
            }else if(names.size() <= 0){
                try {
                    SendMessage message1 = new SendMessage(message.getChatId().toString(), "Совпадений не найдено");
                    message1.setReplyMarkup(initReplyKeyboardMarkup(state));
                    execute(message1);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            return;
        }else {
            String text = null;
            switch (tag){
                case "name":
                    text = "Введите название книги";
                    break;
                case "genre":
                    text = "Введите жанр книги";
                    break;
                case "author":
                    text = "Введите автора книги";
                    break;
                case "year":
                    text = "Введите год книги";
                    break;
                default:
                    text = "исключение!";
                    break;
            }


            try {
                SendMessage message1 = new SendMessage(message.getChatId().toString(), text);
                message1.setReplyMarkup(initReplyKeyboardMarkup(state));
                execute(message1);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }



    private List<String> searchByName(String target){
        return new Files().searchByTarget(target, "name");
    }

    private List<String> searchByGenre(String target){
        return new Files().searchByTarget(target, "genre");
    }

    private List<String> searchByAuthor(String target){
        return new Files().searchByTarget(target, "author");
    }

    private List<String> searchByYear(String year){
        return new Files().searchByTarget(year, "author");
    }

    private ReplyKeyboardMarkup initReplyKeyboardMarkup(String state){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardButton searchByName = new KeyboardButton("Поиск по названию");
        KeyboardButton searchByGenre = new KeyboardButton("Поиск по жанру");
        KeyboardButton searchByAuthor = new KeyboardButton("Поиск по автору");
        KeyboardButton searchByYear = new KeyboardButton("Поиск по году");
        switch (state){
            case "searchByName":
                searchByName.setText("Список названий");
                break;
            case "searchByGenre":
                searchByGenre.setText("Список жанров");
                break;
            case "searchByAuthor":
                searchByAuthor.setText("Список авторов");
                break;
            case "searchByYear":
                searchByYear.setText("Список по годам");
                break;
        }
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(searchByName);
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(searchByGenre);
        KeyboardRow threeRow = new KeyboardRow();
        threeRow.add(searchByAuthor);
        KeyboardRow fourRow = new KeyboardRow();
        fourRow.add(searchByYear);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        keyboardRowList.add(firstRow);
        keyboardRowList.add(secondRow);
        keyboardRowList.add(threeRow);
        keyboardRowList.add(fourRow);

        keyboardMarkup.setKeyboard(keyboardRowList);
        return keyboardMarkup;
    }

    @Override
    public String getBotToken() {
        return PropertiesBot.getToken();
    }
}
