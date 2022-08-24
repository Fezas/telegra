/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.StateDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class StateController implements Initializable {
    StateDAO stateDAO = StateDAO.getInstance();
    @FXML
    private Label labelSizeBd;
    @FXML
    private Label labelCoutTlg;
    @FXML
    private Label labelCoutTlgArchive;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //labelSizeBd.setText(stateDAO.size()); //for postgresql
        labelCoutTlg.setText(stateDAO.countTlg());
    }
}
