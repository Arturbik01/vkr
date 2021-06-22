package telegram.entity;

import telegram.config.DatabaseFactory;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface Entity {
    /*
    *
    */
    default void addEntity(String name, File file, String type, String year, String author, String genre, String description){
        String filename = file.getName();
        String filepath = type + "/" + file.getName();
        if (getEntity(name, "book") != null) return;
        try {
            DatabaseFactory.getConnection().createStatement()
                    .execute(String.format("INSERT files(name, filename, filepath, type, year, author, genre, description) values" +
                            "('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                            name, filename, filepath, type, year, author, genre, description));
            System.out.printf("Добавлен файл с названием '%s',\n файлом '%s', путь '%s',\n типом '%s', жанром '%s\n",
                    name, filename, filepath, type, genre);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    default int getCountWroteBook(String target, String tag){
        try {
            ResultSet resultSet = DatabaseFactory.getConnection().createStatement()
                    .executeQuery(String.format("SELECT count(name) FROM files WHERE %s = '%s'", tag, target));
            if (resultSet.next()){
               return resultSet.getInt(0);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }


    default List<String> getDistinct(String tag){
        List<String> array = new ArrayList<>();
        try {
            ResultSet resultSet = DatabaseFactory.getConnection().createStatement()
                    .executeQuery(String.format("SELECT DISTINCT %s FROM files ORDER BY %s", tag, tag));
            while (resultSet.next()){
                array.add(resultSet.getString(tag));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return array;
    }

    default File getEntity(String name, String type){
        String path = null;
        try {
            ResultSet resultSet = DatabaseFactory.getConnection().createStatement()
                    .executeQuery(String.format("SELECT filepath FROM files WHERE name = '%s'" +
                            " AND type = '%s'", name, type));
            if (resultSet.next()){
                 path = "src/main/resources/files/"+resultSet.getString("filepath");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  (path != null ? new File(path) : null);
    }

    default boolean isExist(File file, String type){
        boolean isExist = false;
        ResultSet resultSet = null;
        try {
            resultSet = DatabaseFactory.getConnection().createStatement()
                    .executeQuery(String.format("SELECT filepath FROM files WHERE filepath = '%s'",
                            type+"/"+file.getName()));
            isExist = resultSet.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return isExist;
    }
}
