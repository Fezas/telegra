/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.ParagraphDAO;
import fezas.telegra.entity.ParagraphEntity;
import fezas.telegra.util.ValidatorTelegra;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ParagraphController implements Initializable  {


    private ObservableList<ParagraphEntity> usersData = FXCollections.observableArrayList();
    private static ObservableList<ParagraphEntity> items = FXCollections.observableArrayList();
    private TelegrammController telegrammController = TelegrammController.getInstance();
    private MainController mainController = MainController.getInstance();
    private GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    private static final Logger logger = LogManager.getLogger();
    private ParagraphEntity updateParagraphs;
    @FXML    private Button btnSave, closeButton;
    @FXML    private TableView<ParagraphEntity> tableSecParagraph;
    @FXML    private TableColumn<ParagraphEntity, Integer> paragraphColumn;
    @FXML    private TableColumn<ParagraphEntity, String> contentColumn;
    @FXML    private TextField inputParagraphId;
    @FXML    private TextArea inputParagraphText;

    private Boolean flagNew;
    public ParagraphController(Boolean flagNew) {
        this.flagNew = flagNew;
    }

    @FXML
    private void showAlertNotSelectedParagraph() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ошибка добавления пункта");
        alert.setHeaderText(null);
        alert.setContentText("Не выбран ни один пункт \n");
        alert.showAndWait();
    }

    private void showAlertUpdate(ParagraphEntity paragraph) {
        ParagraphDAO paragraphDAO = ParagraphDAO.getInstance();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Внимание");
        alert.setHeaderText("Запись существует");
        alert.setContentText("Заменить пункт: " + paragraph.getSecrecyParagraphId() + "?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null) {

        } else if (option.get() == ButtonType.OK) {
            paragraphDAO.update(paragraph);
        } else if (option.get() == ButtonType.CANCEL) {

        } else {

        }
    }
    private void showAlertErrorSave(ParagraphEntity paragraph) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание");
        alert.setHeaderText("Запись существует");
        alert.setContentText("Невозможно заменить существующую запись: " + paragraph.getSecrecyParagraphId());
        Optional<ButtonType> option = alert.showAndWait();
    }

    @FXML
    private void saveParagraph(ActionEvent event) {
        Boolean save = true;
        ParagraphDAO paragraphDAO = ParagraphDAO.getInstance();
        String paragraphId = inputParagraphId.getText();
        String paragraphText = inputParagraphText.getText();
        ParagraphEntity paragraph = new ParagraphEntity();
        paragraph.setSecrecyParagraphText(paragraphText);
        paragraph.setSecrecyParagraphId(Integer.parseInt(paragraphId));
        for (ParagraphEntity prgrf:usersData) {
            if (prgrf.getSecrecyParagraphId() == paragraph.getSecrecyParagraphId()) {
                save = false;
                break;
            }
        }
        if (updateParagraphs != null) {
            if (!save & updateParagraphs.getSecrecyParagraphId() != paragraph.getSecrecyParagraphId()) {
                showAlertErrorSave(paragraph);
            } else if (!save & updateParagraphs.getSecrecyParagraphId() == paragraph.getSecrecyParagraphId()) {
                paragraphDAO.updateAll(paragraph, updateParagraphs.getSecrecyParagraphId());
            } else if(save) {
                paragraphDAO.update(paragraph);
            }
        } else if (!save) {
            showAlertUpdate(paragraph);
        } else {
            paragraphDAO.save(paragraph);
        }
        inputParagraphId.clear();
        inputParagraphText.clear();
        initData();
        tableSecParagraph.refresh();
        updateParagraphs = null;
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
        btnSave.setGraphic(fontAwesome.create("SAVE"));
        initData();
        ValidatorTelegra validatorTelegra = new ValidatorTelegra();
        validatorTelegra.textFieldLenght(inputParagraphId,4);
        validatorTelegra.textAreaLenght(inputParagraphText,600);
        //кнопка сохранить активна если все поля заполнены
        btnSave.disableProperty().bind(
                Bindings.isEmpty(inputParagraphId.textProperty())
                        .or(Bindings.isEmpty(inputParagraphText.textProperty()))
        );
        //перенос текста в TextArea
        inputParagraphText.setWrapText(true);
        tableSecParagraph.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //События при изменении inputParagraphId
        inputParagraphId.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    inputParagraphId.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        tableSecParagraph.setRowFactory(
            new Callback<TableView<ParagraphEntity>, TableRow<ParagraphEntity>>() {
                @Override
                public TableRow<ParagraphEntity> call(TableView<ParagraphEntity> tableView) {
                    final TableRow<ParagraphEntity> row = new TableRow<>();
                    row.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                            ParagraphEntity rowData = row.getItem();
                        }
                    });
                    final ContextMenu rowMenu = new ContextMenu();
                    MenuItem editItem = new MenuItem("Редактировать");
                    editItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            inputParagraphId.setText(row.getItem().getSecrecyParagraphId().toString());
                            inputParagraphText.setText(row.getItem().getSecrecyParagraphText());
                            updateParagraphs = row.getItem();
                        }
                    });
                    MenuItem removeItem = new MenuItem("Удалить пункт");
                    removeItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            ParagraphEntity item = row.getItem();
                            Alert alertDelete = new Alert(Alert.AlertType.CONFIRMATION);
                            alertDelete.setTitle("Внимание");
                            alertDelete.setHeaderText("Удаление записи");
                            alertDelete.setContentText("Удалить запись: " + item.getSecrecyParagraphId() + "?");
                            Optional<ButtonType> option = alertDelete.showAndWait();
                            if (option.get() == null) {

                            } else if (option.get() == ButtonType.OK) {
                                ParagraphDAO paragraphDAO = ParagraphDAO.getInstance();
                                tableSecParagraph.getItems().remove(item);
                                paragraphDAO.delete(item.getSecrecyParagraphId());
                                inputParagraphId.clear();
                                inputParagraphText.clear();
                                initData();
                                tableSecParagraph.refresh();
                            } else if (option.get() == ButtonType.CANCEL) {

                            }
                        }
                    });
                    rowMenu.getItems().addAll(editItem, removeItem);
                    row.contextMenuProperty().bind(
                            Bindings.when(row.emptyProperty())
                                        .then((ContextMenu) null)
                                        .otherwise(rowMenu));
                    return row;
                }
            }
        );


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
        // заполняем таблицу данными
        tableSecParagraph.setItems(usersData);
    }

    private void initData() {
        usersData.clear();
        var paragraphs = ParagraphDAO.getInstance().findAll();
        for (ParagraphEntity paragraph : paragraphs) {
            usersData.add(new ParagraphEntity(
                    paragraph.getSecrecyParagraphId(),
                    paragraph.getSecrecyParagraphText(),
                    ""
            ));
        }
    }
}
