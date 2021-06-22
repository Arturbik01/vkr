package telegram;

import managers.DelegateCharacter;
import managers.DelegateState;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import telegram.config.PropertiesBot;
import telegram.entity.Files;
import telegram.users.User;

import java.io.File;
import java.io.IOException;

public class MainBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return PropertiesBot.getUsername();
    }

    @Override
    public String getBotToken() {
        return PropertiesBot.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {


        String state;
        boolean hasChange = false;
        if (User.getUserStateByChatId(update.getMessage().getChatId().toString()) == null){
            User user = new User(update.getMessage().getChatId().toString(), "book", update.getMessage().getChat().getFirstName());
            user.addUserToDatabase();
            state = "book";
        }else if (update.getMessage().getText() != null){
            String newState = null;
            switch (update.getMessage().getText().toLowerCase()){
                case "библиотека":
                    newState = "book";
                    break;
                case "поиск по названию":
                    newState = "searchByName";
                    break;
                case "поиск по жанру":
                    newState = "searchByGenre";
                    break;
                case "поиск по автору":
                    newState = "searchByAuthor";
                    break;
                case "поиск по году":
                    newState = "searchByYear";
                    break;
            }
            if (newState != null){
                state = DelegateState.getState(update.getMessage().getChatId());
                DelegateState.setState(update.getMessage().getChatId(), newState);
                hasChange = state != newState ? true : false;
            }
        }
        state = DelegateState.getState(update.getMessage().getChatId());
        if (update.getMessage().getText() != null){
            DelegateCharacter character = new DelegateCharacter(update.getMessage(), state, hasChange);
            character.initCharacter();
        }

        if (update.getMessage().hasDocument() && update.getMessage().getChatId() == Integer.parseInt(PropertiesBot.getAdmin())){
            System.out.println(update.getMessage().getDocument());
            Document document = update.getMessage().getDocument();
            GetFile file = new GetFile(document.getFileId());
            String filePath = null;
            try {
                filePath = execute(file).getFilePath();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            //                File fil = downloadFile(filePath, new File("src/main/resources/"+state+"/"+document.getFileName()));
//                fil.createNewFile();
            if (update.getMessage().hasDocument() && update.getMessage().getCaption() != null
                    && update.getMessage().getCaption().split("&").length == 5){
                        String[] attributes = update
                        .getMessage()
                        .getCaption()
                        .replaceAll(" &", "&")
                        .replaceAll("& ", "&")
                        .trim()
                        .split("&");
                try {
                    File fileDownload = downloadFile(filePath, new File("src/main/resources/files/book"
                            +"/"+document.getFileName()));
                        new Files().addEntity(attributes[0], fileDownload, "book", attributes[1], attributes[2], attributes[3], attributes[4]);

                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }




    }
}
