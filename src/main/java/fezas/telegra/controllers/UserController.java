/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.UserDAO;
import fezas.telegra.entity.User;
import fezas.telegra.util.ValidatorTelegra;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    private UsersController usersController = UsersController.getInstance();
    private User currentUser = usersController.getCurrentUser();
    private UserDAO userDAO = UserDAO.getInstance();
    private static final Logger logger = LogManager.getLogger();
    @FXML    private TextField familyInput, loginInput, positionInput, telephoneInput;

    @FXML    private  PasswordField passwordInput;

    @FXML    private Button closeButton, saveButton;

    @FXML    private ComboBox<String> selectRole;

    private void showAlertUpdate(String name, User user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Внимание");
        alert.setHeaderText("Запись существует");
        alert.setContentText("Заменить пользователя: " + name + "?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null) {
        } else if (option.get() == ButtonType.OK) {
            userDAO.update(user);
            usersController.initData();
        } else if (option.get() == ButtonType.CANCEL) {
        }
    }
    @FXML
    private void saveExecutor(ActionEvent event) {
        currentUser.setUserFIO(familyInput.getText());
        currentUser.setUserLogin(loginInput.getText());
        currentUser.setUserTelephone(telephoneInput.getText());
        currentUser.setUserPassword(passwordInput.getText());
        currentUser.setUserRole(selectRole.getSelectionModel().getSelectedItem());
        currentUser.setUserPosition(positionInput.getText());
        if (currentUser.getUserId() != null) {
            showAlertUpdate(familyInput.getText(), currentUser);
        } else  {
            userDAO.save(currentUser);
        }
        usersController.initData();
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ValidatorTelegra validatorTelegra = new ValidatorTelegra();
            validatorTelegra.textFieldLenght(familyInput,20);
            validatorTelegra.textFieldLenght(positionInput,60);
            validatorTelegra.textFieldLenght(telephoneInput,30);
            validatorTelegra.textFieldLenght(loginInput,16);
            validatorTelegra.textFieldLenght(passwordInput,10);
            //кнопка сохранить активна если все поля заполнены
            saveButton.disableProperty().bind(
                    Bindings.isEmpty(familyInput.textProperty())
                            .or(Bindings.isEmpty(familyInput.textProperty()))
                            .or(Bindings.isEmpty(positionInput.textProperty()))
                            .or(Bindings.isEmpty(telephoneInput.textProperty()))
                            .or(Bindings.isEmpty(loginInput.textProperty()))
                            .or(Bindings.isEmpty(passwordInput.textProperty()))
            );
            if (currentUser.getUserId() != null) {
                familyInput.setText(currentUser.getUserFIO());
                positionInput.setText(currentUser.getUserPosition());
                telephoneInput.setText(currentUser.getUserTelephone());
                loginInput.setText(currentUser.getUserLogin());
                passwordInput.setText(currentUser.getUserPassword());
            }
            selectRole.getItems().addAll("администратор", "пользователь");
            selectRole.getSelectionModel().selectLast();
        } catch (Exception throwables) {
            throwables.printStackTrace();
            logger.error(throwables);
        }
    }
}
