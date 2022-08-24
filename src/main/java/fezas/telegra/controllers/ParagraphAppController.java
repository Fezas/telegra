/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.ParagraphDAO;
import fezas.telegra.entity.ParagraphEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ParagraphAppController implements Initializable  {


    private ObservableList<ParagraphEntity> usersData = FXCollections.observableArrayList();
    private static ObservableList<ParagraphEntity> items = FXCollections.observableArrayList();
    private AppController appController = AppController.getInstance();
    private MainController mainController = MainController.getInstance();
    private static final Logger logger = LogManager.getLogger();
    @FXML
    private Button closeButton, buttonAddNewParagraph;
    @FXML
    private TableView<ParagraphEntity> tableSecParagraph;
    @FXML
    private TableColumn<ParagraphEntity, Integer> paragraphColumn;
    @FXML
    private TableColumn<ParagraphEntity, String> contentColumn;
    @FXML
    private TableColumn<ParagraphEntity, String> paragraphCheckColumn;
    @FXML
    private Button addParagraphChoiceButton;

    private List<ParagraphEntity> currentParList;

    public ParagraphAppController(List<ParagraphEntity> currentParList) {
        this.currentParList = currentParList;
    }

    @FXML
    private void showAlertNotSelectedParagraph() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ошибка добавления пункта");
        alert.setHeaderText(null);
        alert.setContentText("Не выбран ни один пункт \n");
        alert.showAndWait();
    }

    @FXML
    private void addParagraphChoice(ActionEvent event) {
        appController.ppApp.clear();
        items = tableSecParagraph.getItems();
        StringBuilder textParagraphButton = new StringBuilder("Пункты Перечня: ");
        for (ParagraphEntity item : items) {
            if (item.getRemark().isSelected()) {
                appController.ppApp.add(item);
                textParagraphButton.append(item.getSecrecyParagraphId() + ", ");
            }
        }
        if (!appController.ppApp.isEmpty()) {
            appController.choiceAppParagraph.setText(textParagraphButton.substring(0, textParagraphButton.length() - 2));
            Stage stage = (Stage) addParagraphChoiceButton.getScene().getWindow();
            stage.close();
        } else {
            showAlertNotSelectedParagraph();
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
        initData();
        tableSecParagraph.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        contentColumn.setCellFactory(tc -> {
            TableCell<ParagraphEntity, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(contentColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        tableSecParagraph.setEditable(true);


        // устанавливаем тип и значение которое должно хранится в колонке
        paragraphColumn.setCellValueFactory(new PropertyValueFactory<ParagraphEntity, Integer>("secrecyParagraphId"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<ParagraphEntity, String>("secrecyParagraphText"));
        paragraphCheckColumn.setCellValueFactory(new PropertyValueFactory<ParagraphEntity, String>("remark"));

        // заполняем таблицу данными
        tableSecParagraph.setItems(initData());

        items = tableSecParagraph.getItems();
        if(!currentParList.isEmpty()) {
            for (ParagraphEntity item : items) {
                for (ParagraphEntity appPar: currentParList) {
                    if (item.getSecrecyParagraphId().equals(appPar.getSecrecyParagraphId())) {
                        item.getRemark().setSelected(true);
                    }
                }
            }
        } else currentParList = new ArrayList<>();
    }

    private ObservableList<ParagraphEntity> initData() {
        usersData.clear();
        var paragraphs = ParagraphDAO.getInstance().findAll();
        usersData.addAll(paragraphs);
        return usersData;
    }
}
