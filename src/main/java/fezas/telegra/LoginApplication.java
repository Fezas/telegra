package fezas.telegra;

import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;
import fezas.telegra.util.InstallSQL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class LoginApplication extends Application {
    private void installDB() {
        boolean haveBase = false;
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM USERS")){
            haveBase = true;
        } catch (Exception e) {
            //нет подключения
            haveBase = false;
        }
        if (!haveBase){
            //содаем базу данных
            InstallSQL installSQL = new InstallSQL();
            String sql = installSQL.getSql();
            try (var connection = ConnectionManager.get();
                 var preparedStatement = connection.prepareStatement(sql, Statement.SUCCESS_NO_INFO)) {
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                throw new DaoException(throwables);
            }
            //предупреждение о создании новой бд
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Установка базы данных");
            alert.setHeaderText(null);
            alert.setContentText("База данных создана");
            alert.showAndWait();
        }
    }

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        //проверка существования БД
        installDB();
        Parent root = FXMLLoader.load(getClass().getResource("/views/login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/telegra.png"))));
        stage.setTitle("Авторизация");
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}