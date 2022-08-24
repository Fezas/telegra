/*
 * Copyright (c) 2022. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.ApplicationDocDAO;
import fezas.telegra.dao.ParsInAppsDAO;
import fezas.telegra.dao.SecrecyDAO;
import fezas.telegra.entity.ApplicationDoc;
import fezas.telegra.entity.ParagraphEntity;
import fezas.telegra.entity.Secrecy;
import fezas.telegra.util.ExtsApp;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.*;

import static fezas.telegra.controllers.TelegrammController.mapAppWithPP;

public class AppController implements Initializable {

    @FXML    private Button addButton, btnSelectFile, saveButton;
    @FXML    public Button choiceAppParagraph;
    @FXML    private TableColumn<ApplicationDoc, String> colAppName, colAppExt, colAppSize, colAppSec, colAppNumb, colAppExe, colAppCheck;
    @FXML    private TableView<ApplicationDoc> tableApp = new TableView<>();
    @FXML    private TextField textfieldApplicationDocName,textfiieldApplicationDocExtension,textfieldApplicationDocSize, textfieldAppExe, textfieldAppNumb;
    @FXML    private ComboBox<Secrecy> selectSecrecyApp;
    private ObservableList<Secrecy> secrecies = FXCollections.observableArrayList();
    public ObservableList<ApplicationDoc> applicationDocsList = FXCollections.observableArrayList();
    private ApplicationDocDAO applicationDocDAO = ApplicationDocDAO.getInstance();
    private ParsInAppsDAO parsInAppsDAO = ParsInAppsDAO.getInstance();
    public List<ParagraphEntity> ppApp = new ArrayList<>();
    private ApplicationDoc currentApplicationDoc = new ApplicationDoc();
    private static final Logger logger = LogManager.getLogger();
    private static AppController instance;
    public static AppController getInstance() {
        if (instance == null) {
            instance = new AppController();
        }
        return instance;
    }
    @FXML
    void actionSelectFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбор приложения");
        File file = fileChooser.showOpenDialog(btnSelectFile.getScene().getWindow());
        if (file != null) {
            textfieldApplicationDocName.setText(FilenameUtils.getBaseName(file.getPath()));
            textfiieldApplicationDocExtension.setText(FilenameUtils.getExtension(file.getPath()));
            textfieldApplicationDocSize.setText(String.valueOf(file.length()));
        }
    }

    @FXML
    void clearForm() {
        textfiieldApplicationDocExtension.clear();
        textfieldApplicationDocName.clear();
        textfieldApplicationDocSize.clear();
        textfieldAppExe.clear();
        textfieldAppNumb.clear();
        selectSecrecyApp.getSelectionModel().clearSelection();
        choiceAppParagraph.setText("Добавить пункты Перечня");
        currentApplicationDoc = new ApplicationDoc();
    }

    @FXML
    private void choiceAppParagraph(ActionEvent event) {
        try {
            ParagraphAppController paragraphAppController = new ParagraphAppController(ppApp);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/paragraphapp-choice.fxml"));
            loader.setController(paragraphAppController);
            Stage stage = new Stage();
            stage.setTitle("Выбор пункта перечня");
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (NullPointerException e) {
            new Alert(Alert.AlertType.ERROR, "Не найдена view paragraphapp-choice.fxml").showAndWait();
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    void addApp(ActionEvent event) {
        currentApplicationDoc.setAppExt(textfiieldApplicationDocExtension.getText());
        currentApplicationDoc.setAppName(textfieldApplicationDocName.getText());
        currentApplicationDoc.setAppSize(textfieldApplicationDocSize.getText());
        currentApplicationDoc.setAppSec(selectSecrecyApp.getSelectionModel().getSelectedItem());
        currentApplicationDoc.setAppExe(textfieldAppExe.getText());
        currentApplicationDoc.setAppNumb(textfieldAppNumb.getText());
        currentApplicationDoc.setRemark(new CheckBox());
        mapAppWithPP.put(currentApplicationDoc,ppApp);
        clearForm();
        initApps();
    }

    private void selectSecrecy() {
        secrecies.clear();
        secrecies.addAll(SecrecyDAO.getInstance().findAll());
        selectSecrecyApp.getItems().addAll(secrecies);
    }

    public void setTextChoiceParagraph (List<ParagraphEntity> paragraphs) {
        if (!paragraphs.isEmpty()) {
            StringBuilder textParagraphButton = new StringBuilder("Пункты Перечня: ");
            for (ParagraphEntity item : paragraphs) {
                textParagraphButton.append(item.getSecrecyParagraphId() + ", ");
            }
            choiceAppParagraph.setText(textParagraphButton.substring(0, textParagraphButton.length() - 2));
        } else choiceAppParagraph.setText("Выберите пункты перечня");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //валидация кнопки приложений к телеграмме
        saveButton.disableProperty().bind(
                Bindings.isEmpty(textfieldApplicationDocName.textProperty())
                        .or(Bindings.isEmpty(textfiieldApplicationDocExtension.textProperty()))
                        .or(Bindings.isEmpty(textfieldApplicationDocSize.textProperty()))
                        .or(Bindings.isEmpty(textfieldAppExe.textProperty()))
                        .or(Bindings.isEmpty(textfieldAppNumb.textProperty()))
                        .or(Bindings.isNull(selectSecrecyApp.getSelectionModel().selectedItemProperty()))
        );
        //ввод расширения
        ExtsApp extsApp = new ExtsApp();
        textfiieldApplicationDocExtension.textProperty().addListener( (ov,oldV,newV) -> {
            if (extsApp.appNameFromExt(newV) != null) {
                textfieldAppExe.setText(extsApp.appNameFromExt(newV));
            } else textfieldAppExe.clear();
        } );
        // устанавливаем тип и значение которое должно хранится в колонках приложений
        colAppName.setCellValueFactory(new PropertyValueFactory<ApplicationDoc, String>("appName"));
        colAppExt.setCellValueFactory(new PropertyValueFactory<ApplicationDoc, String>("appExt"));
        colAppSec.setCellValueFactory(new PropertyValueFactory<ApplicationDoc, String>("appSec"));
        colAppSize.setCellValueFactory(new PropertyValueFactory<ApplicationDoc, String>("appSize"));
        colAppExe.setCellValueFactory(new PropertyValueFactory<ApplicationDoc, String>("appExe"));
        colAppNumb.setCellValueFactory(new PropertyValueFactory<ApplicationDoc, String>("appNumb"));
        colAppCheck.setCellValueFactory(new PropertyValueFactory<ApplicationDoc, String>("remark"));
        initApps();
        tableApp.setItems(applicationDocsList);
        selectSecrecy();
        tableApp.setRowFactory(
                tableView -> {
                    //событие по двойному клику строки
                    final TableRow<ApplicationDoc> row = new TableRow<>();
                    row.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                            ApplicationDoc rowData = row.getItem();
                        }
                    });
                    //контексное меню
                    final ContextMenu rowMenu = new ContextMenu();
                    MenuItem editItem = new MenuItem("Редактировать");
                    MenuItem removeItem = new MenuItem("Удалить пункт");
                    editItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            ppApp.clear();
                            currentApplicationDoc = row.getItem();
                            List<ParagraphEntity> itemPars = parsInAppsDAO.findParsByAppId(row.getItem().getAppId());
                            if (itemPars != null) ppApp.addAll(itemPars);
                            else ppApp = new ArrayList<>();
                            textfiieldApplicationDocExtension.setText(row.getItem().getAppExt());
                            textfieldApplicationDocName.setText(row.getItem().getAppName());
                            textfieldApplicationDocSize.setText(row.getItem().getAppSize());
                            textfieldAppExe.setText(row.getItem().getAppExe());
                            textfieldAppNumb.setText(row.getItem().getAppNumb());
                            selectSecrecyApp.getSelectionModel().select(row.getItem().getAppSec());
                            setTextChoiceParagraph(ppApp);
                            logger.info("OPERATION: пункты в приложении " + currentApplicationDoc.toString() + " изменены");
                        }
                    });

                    removeItem.setOnAction(event -> {
                        tableApp.getItems().remove(row.getItem());
                        applicationDocsList.remove(row.getItem());
                        mapAppWithPP.remove(row.getItem());
                        applicationDocDAO.delete(row.getItem().getAppId());
                        logger.info("OPERATION: пункт " + row.getItem().toString() + " приложения " + currentApplicationDoc.toString() + " удален");
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
    }

    private ObservableList<ApplicationDoc> initApps() {
        applicationDocsList.clear();
        if(!mapAppWithPP.isEmpty()) {
            for (Map.Entry<ApplicationDoc, List<ParagraphEntity>> entry : mapAppWithPP.entrySet()) {
                applicationDocsList.add(entry.getKey());
            }
        }
        return applicationDocsList;
    }
}
