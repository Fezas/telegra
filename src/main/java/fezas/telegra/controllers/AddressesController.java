/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.AddressDAO;
import fezas.telegra.entity.Address;
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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddressesController implements Initializable {
    private static ObservableList<Address> usersData = FXCollections.observableArrayList();
    private static final Logger logger = LogManager.getLogger();
    private Address selectAddress;
    private static AddressesController instance;

    @FXML    private Button closeButton, btnAddAddress;
    @FXML    private TableColumn<Address, String> addressCallsignColumn;
    @FXML    private TableColumn<Address, String> addressColumn;
    @FXML    private TableColumn<Address, String> addressCheckColumn;
    @FXML    private TableColumn<Address, String> addressPersonColumn;
    @FXML    private TableColumn<Address, String> addressPersonRespectColumn;
    @FXML    private TableView<Address> tableAddress  = new TableView<>();

    public static AddressesController getInstance() {
        if (instance == null) {
            instance = new AddressesController();
        }
        return instance;
    }

    @FXML
    private void actionAddAddress(ActionEvent event) {
        try {
            AddressInputController addressInputController = new AddressInputController(null);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/address-input.fxml"));
            loader.setController(addressInputController);
            Stage stage = new Stage();
            stage.setTitle("Ввод адреса");
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (NullPointerException e) {
            new Alert(Alert.AlertType.ERROR, "Не найдена view address-input.fxml").showAndWait();
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void actionEditAddress(Address address) {
        try {
            AddressInputController addressInputController = new AddressInputController(address);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/address-input.fxml"));
            loader.setController(addressInputController);
            Stage stage = new Stage();
            stage.setTitle("Ввод адреса");
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (NullPointerException e) {
            new Alert(Alert.AlertType.ERROR, "Не найдена view address-input.fxml").showAndWait();
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void refreshTableAddresses() {
        initData();
        tableAddress.refresh();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            initData();
            tableAddress.setEditable(true);
            tableAddress.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableAddress.setRowFactory(
                    tableView -> {
                        //событие по двойному клику строки
                        final TableRow<Address> row = new TableRow<>();
                        row.setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                                Address rowData = row.getItem();
                            }
                        });
                        //контексное меню
                        final ContextMenu rowMenu = new ContextMenu();

                        MenuItem editItem = new MenuItem("Редактировать");
                        MenuItem removeItem = new MenuItem("Удалить пункт");

                        editItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                selectAddress = row.getItem();
                                actionEditAddress(selectAddress);
                                selectAddress = null;
                            }
                        });
                        removeItem.setOnAction(event -> {
                            Alert alertDelete = new Alert(Alert.AlertType.CONFIRMATION);
                            alertDelete.setTitle("Внимание");
                            alertDelete.setHeaderText("Удаление записи");
                            alertDelete.setContentText("Удалить запись: " + row.getItem().getTlgAddressCallsign() + "?");
                            Optional<ButtonType> option = alertDelete.showAndWait();
                            if (option.get() == null) {

                            } else if (option.get() == ButtonType.OK) {
                                AddressDAO addressDAO = AddressDAO.getInstance();
                                addressDAO.delete(row.getItem().getTlgAddressId());
                                tableAddress.getItems().remove(row.getItem());
                                initData();
                                tableAddress.refresh();
                            } else if (option.get() == ButtonType.CANCEL) {

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
            );
            addressColumn.setCellFactory(tc -> {
                TableCell<Address, String> cell = new TableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(addressColumn.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell ;
            });

            // устанавливаем тип и значение которое должно хранится в колонке
            addressCallsignColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("tlgAddressCallsign"));
            addressColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("tlgAddressName"));
            addressPersonColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("tlgAddressPerson"));
            addressPersonRespectColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("tlgAddressPersonRespect"));
                // заполняем таблицу данными
            tableAddress.setItems(usersData);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }
    private void initData() {
        usersData.clear();
        var addresses = AddressDAO.getInstance().findAll();
        for (Address address : addresses) {
            usersData.add(new Address(
                    address.getTlgAddressId(),
                    address.getTlgAddressCallsign(),
                    address.getTlgAddressName(),
                    address.getTlgAddressPerson(),
                    address.getTlgAddressPersonRespect(),
                    ""
            ));
        }
    }
}
