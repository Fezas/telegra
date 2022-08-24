package fezas.telegra.controllers;

import fezas.telegra.dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Objects;

/**
 * Контроллер окна авторизации
 *
 * @author a.stratonov
 * @version 1.0
 */
public class LoginController {
    private static final Logger logger = LogManager.getLogger();
    /**
     * Свойство - кнопка авторизация
     */
    public Button btnLogin;
    /**
     * Свойство - кнопка close
     */
    @FXML    private Button buttonClose;
    /**
     * Свойство - поле для ввода логина
     */
    @FXML    private TextField txtUsername;
    /**
     * Свойство - поле для ввода пароля
     */
    @FXML    private PasswordField txtPassword;
    /**
     * Выход из интерфейса
     */
    @FXML
    void close(ActionEvent event) {
        ((Stage) buttonClose.getScene().getWindow()).close();
    }
    /**
     * Проврка данных пользователя, в случае успешной авторизации переходит на окно с таблицами.
     */
    @FXML
    private void btnLoginAction(ActionEvent event) {
        String login = txtUsername.getText();
        String password = txtPassword.getText();
        try {
            UserDAO userDAO = UserDAO.getInstance();
            if (login.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка авторизации");
                alert.setHeaderText("Укажите логин или пароль!");
                alert.showAndWait();
            } else if (userDAO.login(login,password) != null) {
                UserLoginController userLoginController = UserLoginController.getInstance();
                userLoginController.setCurrentUser(userDAO.login(login,password));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Telegra");
                stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/telegra.png"))));
                stage.setScene(new Scene(loader.load()));
                stage.show();
                ((Stage) btnLogin.getScene().getWindow()).close();
                logger.info("LOGIN: Авторизация успешна прошла!");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка авторизации");
                alert.setHeaderText("Подключение не произошло.\nПроверьте логин или пароль");
                alert.showAndWait();
            }
        } catch (NullPointerException e) {
            new Alert(Alert.AlertType.ERROR, "Не найдена view main.fxml").showAndWait();
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}
