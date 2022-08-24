/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.RankDAO;
import fezas.telegra.dao.SupervisorDAO;
import fezas.telegra.entity.Rank;
import fezas.telegra.entity.Supervisor;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class SupervisorController implements Initializable {
    private static ObservableList<Supervisor> usersData = FXCollections.observableArrayList();
    public ObservableList<Rank> ranks = FXCollections.observableArrayList();
    private MainController mainController = MainController.getInstance();
    private static final Logger logger = LogManager.getLogger();
    private static Integer idRecord = 0;

    @FXML    private TableColumn<Supervisor, String> familyColumn;
    @FXML    private TableColumn<Supervisor, String> positionColumn;
    @FXML    private TableColumn<Supervisor, String> telephoneColumn;
    @FXML    private TableColumn<Supervisor, String> rankColumn;
    @FXML    private TableView<Supervisor> tableExecutor;
    @FXML    private ComboBox<Rank> rankComboBox;
    @FXML    private TextField familyInput;
    @FXML    private TextField positionInput;
    @FXML    private Button closeButton;
    @FXML    private TextField telephoneInput;
    @FXML    private Button saveButton;
    @FXML    private Label labelDefault;

    private void showAlertUpdate(String name, Supervisor executor) {
        SupervisorDAO executorDAO = SupervisorDAO.getInstance();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Внимание");
        alert.setHeaderText("Запись существует");
        alert.setContentText("Заменить адрес: " + name + "?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null) {

        } else if (option.get() == ButtonType.OK) {
            executorDAO.update(executor);
            familyInput.clear();
            positionInput.clear();
            telephoneInput.clear();
            initData();
            mainController.initData();
            tableExecutor.refresh();
            idRecord = 0;
        } else if (option.get() == ButtonType.CANCEL) {

        }
    }
    @FXML
    private void saveSupervisor(ActionEvent event) {
        SupervisorDAO supervisorDAO = SupervisorDAO.getInstance();
        String familyInputText = familyInput.getText();
        String positionInputText = positionInput.getText();
        String telephoneInputText = telephoneInput.getText();
        Supervisor supervisor = new Supervisor();
        supervisor.setLastname(familyInputText);
        supervisor.setPosition(positionInputText);
        supervisor.setTelephone(telephoneInputText);
        supervisor.setRank(rankComboBox.getSelectionModel().getSelectedItem());
        if (idRecord!=0) {
            supervisor.setId(idRecord);
            showAlertUpdate(familyInputText, supervisor);
        } else  {
            supervisorDAO.save(supervisor);
            familyInput.clear();
            positionInput.clear();
            telephoneInput.clear();
            rankComboBox.getSelectionModel().selectFirst();
            initData();
            tableExecutor.refresh();
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
            validatorTelegra.textFieldLenght(familyInput,20);
            validatorTelegra.textFieldLenght(positionInput,60);
            validatorTelegra.textFieldLenght(telephoneInput,30);
            //кнопка сохранить активна если все поля заполнены
            saveButton.disableProperty().bind(
                    Bindings.isEmpty(familyInput.textProperty())
                            .or(Bindings.isEmpty(positionInput.textProperty()))
                            .or(Bindings.isEmpty(telephoneInput.textProperty()))
            );
            tableExecutor.setRowFactory(
                    new Callback<TableView<Supervisor>, TableRow<Supervisor>>() {
                        @Override
                        public TableRow<Supervisor> call(TableView<Supervisor> tableView) {
                            final TableRow<Supervisor> row = new TableRow<>();
                            row.setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                                    Supervisor rowData = row.getItem();
                                    System.out.println(rowData);
                                }
                            });

                            final ContextMenu rowMenu = new ContextMenu();
                            MenuItem defaultItem = new MenuItem("Исполнитель по умолчанию");
                            defaultItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    SupervisorDAO executorDAO = SupervisorDAO.getInstance();
                                    Supervisor executor = new Supervisor();
                                    executorDAO.updateDefAll();
                                    executor.setId(row.getItem().getId());
                                    executor.setTelephone(row.getItem().getTelephone());
                                    executor.setPosition(row.getItem().getPosition());
                                    executor.setLastname(row.getItem().getLastname());
                                    executor.setDef(true);
                                    executorDAO.update(executor);
                                    labelDefault.setText("Исполнитель по умолчанию - " + row.getItem().getLastname());
                                }
                            });
                            MenuItem editItem = new MenuItem("Редактировать");
                            editItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    familyInput.setText(row.getItem().getLastname());
                                    positionInput.setText(row.getItem().getPosition());
                                    telephoneInput.setText(row.getItem().getTelephone());
                                    rankComboBox.getSelectionModel().select(row.getItem().getRank());
                                    idRecord = row.getItem().getId();
                                }
                            });
                            MenuItem removeItem = new MenuItem("Удалить пункт");
                            removeItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    Alert alertDelete = new Alert(Alert.AlertType.CONFIRMATION);
                                    alertDelete.setTitle("Внимание");
                                    alertDelete.setHeaderText("Удаление записи");
                                    alertDelete.setContentText("Удалить запись: " + row.getItem().getLastname() + "?");
                                    Optional<ButtonType> option = alertDelete.showAndWait();
                                    if (option.get() == null) {

                                    } else if (option.get() == ButtonType.OK) {
                                        SupervisorDAO executorDAO = SupervisorDAO.getInstance();
                                        executorDAO.delete(row.getItem().getId());
                                        tableExecutor.getItems().remove(row.getItem());
                                        familyInput.clear();
                                        positionInput.clear();
                                        telephoneInput.clear();
                                        rankComboBox.getSelectionModel().selectFirst();
                                        initData();
                                        tableExecutor.refresh();
                                    } else if (option.get() == ButtonType.CANCEL) {

                                    }
                                }
                            });
                            rowMenu.getItems().addAll(editItem, removeItem, defaultItem);
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
            familyColumn.setCellValueFactory(new PropertyValueFactory<Supervisor, String>("lastname"));
            positionColumn.setCellValueFactory(new PropertyValueFactory<Supervisor, String>("position"));
            telephoneColumn.setCellValueFactory(new PropertyValueFactory<Supervisor, String>("telephone"));
            rankColumn.setCellValueFactory(new PropertyValueFactory<Supervisor, String>("rank"));
            // заполняем таблицу данными
            tableExecutor.setItems(usersData);

            RankDAO rankDAO = RankDAO.getInstance();
            ranks.clear();
            ranks.addAll(rankDAO.findAll());
            rankComboBox.getItems().addAll(ranks);

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }
    @FXML
    private void initData() {
        usersData.clear();
        var supervisors = SupervisorDAO.getInstance().findAll();
        for (Supervisor supervisor : supervisors) {
            usersData.add(new Supervisor(
                    supervisor.getId(),
                    supervisor.getPosition(),
                    supervisor.getLastname(),
                    supervisor.getTelephone(),
                    supervisor.getRank(),
                    supervisor.isDef())
            );
            if (supervisor.isDef()) {
                labelDefault.setText("Исполнитель по умолчанию - " + supervisor.getLastname());
            }
        }
    }
}
