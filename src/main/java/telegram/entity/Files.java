package telegram.entity;

import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegram.config.DatabaseFactory;
import telegram.config.PropertiesBot;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Files extends DefaultAbsSender implements Entity{
    private String name;
    private String type;
    private String year;
    private String author;
    private String chatId;


    public Files(String name, String type, String year, String author, String chatId) {
        super(new DefaultBotOptions());
        this.name = name;
        this.type = type;
        this.year = year;
        this.author = author;
        this.chatId = chatId;
    }

    public Files() {
        super(new DefaultBotOptions());
    }

    public void addFile(String genre, String description){
        File file = this.getEntity(name, type);
        addEntity(name, file, type, year, author, genre, description);
    }

    public List<String> searchByTarget(String target, String tag){
        List<String> namesBookList = new ArrayList<>();
        ResultSet resultSet = null;
        try {
            resultSet = DatabaseFactory.getConnection().createStatement()
                    .executeQuery(String.format("SELECT name FROM files WHERE %s = '%s'", tag, target));
            while (resultSet.next()){
                namesBookList.add(resultSet.getString("name"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return namesBookList;
    }



    public List<List<String>> searchAll(String target, String tag){
        List<List<String>> mapList = new ArrayList<>();
        ResultSet resultSet;
        try {
            resultSet = DatabaseFactory.getConnection().createStatement()
                    .executeQuery(String.format("SELECT name, author, year, genre, description FROM files WHERE %s = '%s'", tag, target));
            while (resultSet.next()){
                List<String> book = new ArrayList<>();
                book.add(resultSet.getString("name"));
                book.add(resultSet.getString("author"));
                book.add(resultSet.getString("year"));
                book.add(resultSet.getString("genre"));
                book.add(resultSet.getString("description"));
                mapList.add(book);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return mapList;
    }

    public List<String> searchByGenre(String genre){

        return null;
    }

    public List<String> searchByAuthor(String author){

        return null;
    }

    public List<String> searchByYear(String year){

        return null;
    }

    public String getNameByFile(File file){
        String name = null;
        try {
            ResultSet resultSet = DatabaseFactory.getConnection().createStatement()
                    .executeQuery(String.format("SELECT name FROM files WHERE filepath = '%s'", file.getPath()));
            name = resultSet.next() ? resultSet.getString("name").getBytes(StandardCharsets.UTF_8).toString() : "DEFINED";
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return name;
    }

    public void sendFile() throws TelegramApiException {
        File file = this.getEntity(name, type);
        if (file == null) {
            execute(new SendMessage(chatId, "Файл не найден"));
            return;
        }
        if (file.exists()){
            execute(new SendDocument(chatId, new InputFile(file, name)));
        }else {
            execute(new SendMessage(chatId, "Файл был удален"));
        }

    }

    @Override
    public String getBotToken() {
        return PropertiesBot.getToken();
    }
}
