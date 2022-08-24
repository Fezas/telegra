/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.AddressDAO;
import fezas.telegra.entity.Address;
import fezas.telegra.util.ValidatorTelegra;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class AddressInputController implements Initializable {
    private Address addressItem;
    private static final Logger logger = LogManager.getLogger();
    @FXML    private Button saveButton, closeButton;
    @FXML    private TextField inputCallsign, inputAddress, inputAddressPerson, inputAddressPersonRespect;


    public AddressInputController(Address addressItem) {
        this.addressItem = addressItem;
    }

    @FXML
    private void saveAddress(ActionEvent event) {
        AddressDAO addressDAO = AddressDAO.getInstance();
        String inputCallsignText = inputCallsign.getText();
        String inputAddressText = inputAddress.getText();
        String inputAddressPersonText = inputAddressPerson.getText();
        String inputAddressPersonRespectText = inputAddressPersonRespect.getText();
        Address address = new Address();
        address.setTlgAddressCallsign(inputCallsignText);
        address.setTlgAddressName(inputAddressText);
        address.setTlgAddressPerson(inputAddressPersonText);
        address.setTlgAddressPersonRespect(inputAddressPersonRespectText);
        if (addressItem != null) {//если это редактирование адреса
            address.setTlgAddressId(addressItem.getTlgAddressId());
            addressDAO.update(address);
        } else {
            addressDAO.save(address);
        }
        AddressesController addressesController = AddressesController.getInstance();
        addressesController.refreshTableAddresses();
        Stage stage = (Stage) saveButton.getScene().getWindow();
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
            //валидация полей на превышение и первый пробел
            ValidatorTelegra validatorTelegra = new ValidatorTelegra();
            validatorTelegra.textFieldLenght(inputCallsign,20);
            validatorTelegra.textFieldLenght(inputAddress,100);
            validatorTelegra.textFieldLenght(inputAddressPerson,40);
            validatorTelegra.textFieldLenght(inputAddressPersonRespect,60);
            //кнопка сохранить активна если все поля заполнены
            saveButton.disableProperty().bind(
                    Bindings.isEmpty(inputCallsign.textProperty())
                            .or(Bindings.isEmpty(inputAddress.textProperty()))
            );
            if (addressItem != null) {//если это редактирование адреса
                inputCallsign.setText(addressItem.getTlgAddressCallsign());
                inputAddress.setText(addressItem.getTlgAddressName());
                inputAddressPerson.setText(addressItem.getTlgAddressPerson());
                inputAddressPersonRespect.setText(addressItem.getTlgAddressPersonRespect());
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }
}
