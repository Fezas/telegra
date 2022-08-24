/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.SecrecyDAO;
import fezas.telegra.entity.Secrecy;
import fezas.telegra.util.ValidatorTelegra;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class SecrecyController implements Initializable {
    private static ObservableList<Secrecy> usersData = FXCollections.observableArrayList();
    private static Map<Integer, String > secrecyMap = new HashMap<>();
    private MainController mainController = MainController.getInstance();
    private static final Logger logger = LogManager.getLogger();
    private static Integer idRecord = 0;
    SecrecyDAO secrecyDAO = SecrecyDAO.getInstance();
    @FXML
    private TableColumn<Secrecy, String> secrecyColumn;

    @FXML
    private Button closeButton;

    @FXML
    private TextField inputSecrecy;

    @FXML
    private TableView<Secrecy> tableSecrecy;

    @FXML
    private Button saveButton;


    private void showAlertUpdate(String name, Secrecy secrecy) {
        SecrecyDAO secrecyDAO = SecrecyDAO.getInstance();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Внимание");
        alert.setHeaderText("Запись существует");
        alert.setContentText("Заменить пункт: " + name + "?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null) {

        } else if (option.get() == ButtonType.OK) {
            secrecyDAO.update(secrecy);
            inputSecrecy.clear();
            initData();
            mainController.initData();
            tableSecrecy.refresh();
            idRecord = 0;
        } else if (option.get() == ButtonType.CANCEL) {

        } else {

        }
    }
    @FXML
    private void saveSecrecy(ActionEvent event) {
        String inputSec = inputSecrecy.getText();
        Secrecy secrecy = new Secrecy();
        secrecy.setSecrecyName(inputSec);
        if (idRecord!=0) {
            secrecy.setSecerecyId(idRecord);
            showAlertUpdate(inputSec, secrecy);
        } else  {
            secrecyDAO.save(secrecy);
            inputSecrecy.clear();
            initData();
            tableSecrecy.refresh();
            mainController.initData();
        }
    }
    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
        @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            initData();
            ValidatorTelegra validatorTelegra = new ValidatorTelegra();
            validatorTelegra.textFieldLenght(inputSecrecy,26);
            //кнопка сохранить активна если все поля заполнены
            saveButton.disableProperty().bind(
                    Bindings.isEmpty(inputSecrecy.textProperty())
            );
            tableSecrecy.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            tableSecrecy.setRowFactory(
                    new Callback<TableView<Secrecy>, TableRow<Secrecy>>() {
                        @Override
                        public TableRow<Secrecy> call(TableView<Secrecy> tableView) {
                            final TableRow<Secrecy> row = new TableRow<>();
                            row.setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                                    Secrecy rowData = row.getItem();
                                    System.out.println(rowData);
                                }
                            });
                            final ContextMenu rowMenu = new ContextMenu();
                            MenuItem editItem = new MenuItem("Редактировать");
                            editItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    inputSecrecy.setText(row.getItem().getSecrecyName());
                                    idRecord = row.getItem().getSecerecyId();
                                }
                            });
                            MenuItem removeItem = new MenuItem("Удалить пункт");
                            removeItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    Alert alertDelete = new Alert(Alert.AlertType.CONFIRMATION);
                                    alertDelete.setTitle("Внимание");
                                    alertDelete.setHeaderText("Удаление записи");
                                    alertDelete.setContentText("Удалить запись: " + row.getItem().getSecrecyName() + "?");
                                    Optional<ButtonType> option = alertDelete.showAndWait();
                                    if (option.get() == null) {

                                    } else if (option.get() == ButtonType.OK) {
                                        secrecyDAO.delete(row.getItem().getSecerecyId());
                                        tableSecrecy.getItems().remove(row.getItem());
                                        initData();
                                        tableSecrecy.refresh();
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
            secrecyColumn.setCellValueFactory(new PropertyValueFactory<Secrecy, String>("secrecyName"));
            // заполняем таблицу данными
            tableSecrecy.setItems(usersData);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }
    private static void initData() {
        usersData.clear();
        var secreciess = SecrecyDAO.getInstance().findAll();
        for (Secrecy secrecy : secreciess) {
            usersData.add(new Secrecy(secrecy.getSecerecyId(), secrecy.getSecrecyName()));
            secrecyMap.put(secrecy.getSecerecyId(), secrecy.getSecrecyName());
        }
    }

}
