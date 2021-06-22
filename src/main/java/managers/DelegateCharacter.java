package managers;

import org.telegram.telegrambots.meta.api.objects.Message;
import telegram.states.BookState;
import telegram.states.FilmState;
import telegram.states.MenuState;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class DelegateCharacter {
    private Message message;
    private String state;
    private boolean hasChange;

    public DelegateCharacter(@Nullable Message message, @NotNull String state, boolean hasChange){
        this.message = message;
        this.state = state;
        this.hasChange = hasChange;
    }

    public void initCharacter(){
        new BookState(message, state, hasChange);
    }


}
