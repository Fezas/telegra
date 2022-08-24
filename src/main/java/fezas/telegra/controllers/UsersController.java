/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.UserDAO;
import fezas.telegra.entity.User;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UsersController implements Initializable {
    private static ObservableList<User> usersData = FXCollections.observableArrayList();
    private MainController mainController = MainController.getInstance();
    private static User currentUser;
    private static UsersController instance;
    private static final Logger logger = LogManager.getLogger();
    public static UsersController getInstance() {
        if (instance == null) {
            instance = new UsersController();
        }
        return instance;
    }
    public static User getCurrentUser() {
        return currentUser;
    }
    @FXML
    private TableColumn<User, String> familyColumn;

    @FXML
    private TableColumn<User, String> positionColumn;

    @FXML
    private TableColumn<User, String> telephoneColumn;

    @FXML
    private TableView<User> tableUsers = new TableView<>();

    @FXML
    private void addUser(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/user.fxml"));
            currentUser = new User();
            Stage stage = new Stage();
            stage.setTitle("Пользователь");
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (NullPointerException e) {
            new Alert(Alert.AlertType.ERROR, "Не найдена view user.fxml").showAndWait();
            e.printStackTrace();
            logger.error(e);
            System.exit(-1);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            initData();
            tableUsers.setRowFactory(
                    new Callback<TableView<User>, TableRow<User>>() {
                        @Override
                        public TableRow<User> call(TableView<User> tableView) {
                            final TableRow<User> row = new TableRow<>();
                            row.setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                                    User rowData = row.getItem();
                                    System.out.println(rowData);
                                }
                            });

                            final ContextMenu rowMenu = new ContextMenu();
                            MenuItem editItem = new MenuItem("Редактировать");
                            editItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    currentUser = row.getItem();
                                    addUser(event);
                                }
                            });
                            MenuItem removeItem = new MenuItem("Удалить пункт");
                            removeItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    Alert alertDelete = new Alert(Alert.AlertType.CONFIRMATION);
                                    alertDelete.setTitle("Внимание");
                                    alertDelete.setHeaderText("Удаление записи");
                                    alertDelete.setContentText("Удалить запись: " + row.getItem().getUserFIO()+ "?");
                                    Optional<ButtonType> option = alertDelete.showAndWait();
                                    if (option.get() == null) {

                                    } else if (option.get() == ButtonType.OK) {
                                        UserDAO userDAO = UserDAO.getInstance();
                                        userDAO.delete(row.getItem().getUserId());
                                        tableUsers.getItems().remove(row.getItem());
                                        initData();
                                        tableUsers.refresh();
                                    } else if (option.get() == ButtonType.CANCEL) {

                                    }
                                }
                            });
                            rowMenu.getItems().addAll(editItem, removeItem);
                            // only display context menu for non-empty rows:
                            row.contextMenuProperty().bind(
                                    Bindings.when(row.emptyProperty())
                                            .then((ContextMenu) null)
                                            .otherwise(rowMenu));
                            return row;
                        }
                    }
            );


            // устанавливаем тип и значение которое должно хранится в колонке
            familyColumn.setCellValueFactory(new PropertyValueFactory<User, String>("userFIO"));
            positionColumn.setCellValueFactory(new PropertyValueFactory<User, String>("userPosition"));
            telephoneColumn.setCellValueFactory(new PropertyValueFactory<User, String>("userTelephone"));
            // заполняем таблицу данными
            tableUsers.setItems(usersData);
        } catch (Exception throwables) {
            throwables.printStackTrace();
            logger.error(throwables);
        }
    }

    @FXML
    public void initData() {
        usersData.clear();
        var users = UserDAO.getInstance().findAll();
        for (User user : users) {
            usersData.add(new User(
                    user.getUserId(),
                    user.getUserLogin(),
                    user.getUserPassword(),
                    user.getUserTelephone(),
                    user.getUserPosition(),
                    user.getUserRole(),
                    user.getUserFIO())
            );
        }
    }
}
