/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.entity.Address;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import java.util.ResourceBundle;

public class AddressChoiceController implements Initializable {
    private static ObservableList<Address> usersData = FXCollections.observableArrayList();
    private static ObservableList<Address> items = FXCollections.observableArrayList();
    private TelegrammController telegrammController = TelegrammController.getInstance();
    private MainController mainController = MainController.getInstance();
    private static final Logger logger = LogManager.getLogger();
    @FXML    private Button btnAddressBook, closeButton, addAddressButton;

    @FXML    private TableColumn<Address, String> addressCallsignColumn;

    @FXML    private TableColumn<Address, String> addressColumn;

    @FXML    private TableColumn<Address, String> addressCheckColumn;

    @FXML    private TableColumn<Address, String> addressPersonColumn;

    @FXML    private TableColumn<Address, String> addressPersonRespectColumn;

    @FXML    private CheckBox selectAll;

    @FXML    private TableView<Address> tableAddress;

    @FXML
    private void actionAddressBook(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/address.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Адресная книга");
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            initData();
            tableAddress.refresh();
        } catch (NullPointerException e) {
            new Alert(Alert.AlertType.ERROR, "Не найдена view address.fxml").showAndWait();
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
    private void showAlertNotSelectedAddress() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ошибка добавления адреса");
        alert.setHeaderText(null);
        alert.setContentText("Не выбран ни один адрес");
        alert.showAndWait();
    }

    @FXML
    private void addAddress(ActionEvent event) {
        boolean checked = false;
        items = tableAddress.getItems();
        for (Address item : items) {
            if (item.getRemark().isSelected()) {
                checked = true;
                break;
            }
        }
        if (checked) {
            telegrammController.getAddressWithChoice().addAll(tableAddress.getItems());
            Stage stage = (Stage) addAddressButton.getScene().getWindow();
            stage.close();
        } else {
            showAlertNotSelectedAddress();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            initData();
            tableAddress.setEditable(true);
            tableAddress.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            if (true) {
                //слушатели чекбоксов
                selectAll.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
                    items = tableAddress.getItems();
                    for (Address item : items) {
                        if (selectAll.isSelected()) {
                            item.getRemark().setSelected(true);
                        } else {
                            item.getRemark().setSelected(false);
                        }
                    }
                });
            }

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
            addressCheckColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("remark"));
            // заполняем таблицу данными
            tableAddress.setItems(usersData);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }
    private void initData() {
        usersData.clear();
        usersData.addAll(telegrammController.getAddressWithChoice());
    }
}
