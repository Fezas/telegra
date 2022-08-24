/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.TypeDAO;
import fezas.telegra.entity.Type;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class TypeController implements Initializable {
    private static ObservableList<Type> usersData = FXCollections.observableArrayList();
    private static ObservableList<Type> items = FXCollections.observableArrayList();
    private TelegrammController telegrammController = TelegrammController.getInstance();
    private static final Logger logger = LogManager.getLogger();
    private static Integer idRecord = 0;

    @FXML    private Button saveButton, closeButton, addTypeChoiceButton;

    @FXML    private TextArea inputTypeDesc;

    @FXML    private TableColumn<Type, String> typeColumn;

    @FXML    private TableColumn<Type, String> typeDescColumn;

    @FXML    private TextField inputType;

    @FXML    private TableView<Type> tableType;


    public TypeController() {
    }

    @FXML
    private void showAlertNotSelectedType() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ошибка добавления типа");
        alert.setHeaderText(null);
        alert.setContentText("Не выбран ни один тип \n");
        alert.showAndWait();
    }

    private void showAlertUpdate(String name, Type type) {
        TypeDAO typeDAO = TypeDAO.getInstance();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Внимание");
        alert.setHeaderText("Запись существует");
        alert.setContentText("Заменить пункт: " + name + "?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null) {

        } else if (option.get() == ButtonType.OK) {
            typeDAO.update(type);
            inputType.clear();
            inputTypeDesc.clear();
            initData();
            tableType.refresh();
            idRecord = 0;
        } else if (option.get() == ButtonType.CANCEL) {

        } else {

        }
    }

    @FXML
    private void saveType(ActionEvent event) {
        TypeDAO typeDAO = TypeDAO.getInstance();
        String inputTypeText = inputType.getText();
        String inputTypeDescText = inputTypeDesc.getText();
        Type type = new Type();
        type.setTypeName(inputTypeText);
        type.setTypeDesc(inputTypeDescText);
        if (idRecord!=0) {
            type.setTypeId(idRecord);
            showAlertUpdate(inputTypeText, type);
        } else  {
            typeDAO.save(type);
            inputType.clear();
            inputTypeDesc.clear();
            initData();
            tableType.refresh();
        }
    }

    @FXML
    public void closeButton(ActionEvent event) {
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
            initData();
            ValidatorTelegra validatorTelegra = new ValidatorTelegra();
            validatorTelegra.textFieldLenght(inputType,40);
            validatorTelegra.textAreaLenght(inputTypeDesc,200);
            //кнопка сохранить активна если все поля заполнены
            saveButton.disableProperty().bind(Bindings.isEmpty(inputType.textProperty()));
            //перенос текста в TextArea
            inputTypeDesc.setWrapText(true);
            tableType.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            tableType.setRowFactory(
                    new Callback<TableView<Type>, TableRow<Type>>() {
                        @Override
                        public TableRow<Type> call(TableView<Type> tableView) {
                            final TableRow<Type> row = new TableRow<>();
                            row.setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                                    Type rowData = row.getItem();
                                }
                            });
                            final ContextMenu rowMenu = new ContextMenu();
                            MenuItem editItem = new MenuItem("Редактировать");
                            editItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    inputType.setText(row.getItem().getTypeName());
                                    inputTypeDesc.setText(row.getItem().getTypeDesc());
                                    idRecord = row.getItem().getTypeId();
                                }
                            });
                            MenuItem removeItem = new MenuItem("Удалить пункт");
                            removeItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    Alert alertDelete = new Alert(Alert.AlertType.CONFIRMATION);
                                    alertDelete.setTitle("Внимание");
                                    alertDelete.setHeaderText("Удаление записи");
                                    alertDelete.setContentText("Удалить запись: " + row.getItem().getTypeName() + "?");
                                    Optional<ButtonType> option = alertDelete.showAndWait();
                                    if (option.get() == null) {

                                    } else if (option.get() == ButtonType.OK) {
                                        TypeDAO typeDAO = TypeDAO.getInstance();
                                        typeDAO.delete(row.getItem().getTypeId());
                                        tableType.getItems().remove(row.getItem());
                                        inputType.clear();
                                        inputTypeDesc.clear();
                                        initData();
                                        tableType.refresh();
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


            typeDescColumn.setCellFactory(tc -> {
                TableCell<Type, String> cell = new TableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(typeDescColumn.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell ;
            });
            tableType.setEditable(true);
            // устанавливаем тип и значение которое должно хранится в колонке
            typeColumn.setCellValueFactory(new PropertyValueFactory<Type, String>("typeName"));
            typeDescColumn.setCellValueFactory(new PropertyValueFactory<Type, String>("typeDesc"));
            // заполняем таблицу данными
            tableType.setItems(usersData);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }
    private void initData() {
        usersData.clear();
        var types = TypeDAO.getInstance().findAll();
        for (Type type : types) {
            usersData.add(new Type(
                    type.getTypeId(),
                    type.getTypeName(),
                    type.getTypeDesc(),
                    ""
            ));
        }
    }
}