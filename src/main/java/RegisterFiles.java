
import telegram.entity.Files;
import java.io.File;
import java.util.Scanner;

public class RegisterFiles implements Runnable {
    @Override
    public void run() {


            File[] filesBook = new File("src/main/resources/files/book").listFiles();
            init(filesBook, "book");

//        File[] filesFilm = new File("src/main/resources/files/film").listFiles();
//        init(filesFilm, "film");

        runTelegramBot();
    }

    private void runTelegramBot() {

        new Thread(Main.getInstance()).start();
    }


    private void init(File[] files, String type) {
        System.out.printf("Начинается инициализация типа %s...", type);
        for (int i = 0; i < files.length; i++) {
            if (files[i].exists() && files[i].isFile()) {
                if (!new Files().isExist(files[i], type)) {
                    System.out.printf("Введите название %s: ", files[i].getName());
                    String name = new Scanner(System.in).nextLine();
                    if (name.isBlank()){
                        files[i].delete();
                        continue;
                    }
                    System.out.printf("Введите год файла %s: ", files[i].getName());
                    String year = new Scanner(System.in).nextLine();

                    System.out.printf("Введите автора файла %s: ", files[i].getName());
                    String author = new Scanner(System.in).nextLine();

                    System.out.printf("Введите жанр (через запятую) файла %s:  ", files[i].getName());
                    String genre = new Scanner(System.in).nextLine();
                    System.out.printf("Введите описание (через запятую) файла %s:  ", files[i].getName());
                    String description = new Scanner(System.in).nextLine();
                    new Files().addEntity(name, files[i], type, year, author, genre, description);

                }else {
                    System.out.println(files[i].getName() + " в хранилище");
                }
            }
        }
        System.out.println("Завершена инициализация типа ");

    }
}
