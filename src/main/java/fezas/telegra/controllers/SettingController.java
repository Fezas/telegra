/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.PropertyAppDAO;
import fezas.telegra.entity.PropertyApp;
import fezas.telegra.entity.User;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.controlsfx.validation.ValidationSupport;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class SettingController implements Initializable {
    private PropertyAppDAO propertyAppDAO = PropertyAppDAO.getInstance();
    private static final Logger logger = LogManager.getLogger();
    private GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");

    @FXML    private Button btnSelectCatWhithTlg, btnSelectCatWhithPdf, btnSelectCatWhithTmp, btnSelectCatWhithDoc, closeButton, btnSave;
    @FXML    private TextField textFieldNumbComp, textFieldService;
    @FXML    private CustomTextField textFieldSelectCatWhithPDF, textFieldSelectCatWhithTLG, textFieldSelectCatWhithDOC;
    @FXML    private Slider sliderMinStringsOnNewPage;

    private ValidationSupport validation = new ValidationSupport();

    public PropertyApp getPropertyApp() {
        User user = UserLoginController.getInstance().getCurrentUser();
        PropertyApp propertyApp = propertyAppDAO.findById(user.getUserId()).get();
        return propertyApp;
    }

    @FXML
    void saveSetting(ActionEvent event) {
        PropertyApp property;
        property = getPropertyApp();
        property.setPathPDF(textFieldSelectCatWhithPDF.getText());
        property.setPathTLG(textFieldSelectCatWhithTLG.getText());
        property.setPathDOC(textFieldSelectCatWhithDOC.getText());
        property.setNumberComputer(textFieldNumbComp.getText());
        property.setMinStringsOnNewPage((int) sliderMinStringsOnNewPage.getValue());
        property.setService(textFieldService.getText());
        propertyAppDAO.update(property);
        logger.info("Настройки успешно изменены");
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();

    }

    @FXML
    void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    private void showAlert(String error) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ошибка сохранения");
        alert.setHeaderText(null);
        alert.setContentText(error);
        alert.showAndWait();
    }
    @FXML
    void selectCatWhithPdf(ActionEvent event) {
        selectCat(btnSelectCatWhithPdf, textFieldSelectCatWhithPDF, "Выбор каталога для PDF файлов");
    }

    @FXML
    void selectCatWhithTlg(ActionEvent event) {
        selectCat(btnSelectCatWhithTlg, textFieldSelectCatWhithTLG, "Выбор каталога для TLG файлов");
    }

    @FXML
    void selectCatWhithDoc(ActionEvent event) {
        selectCat(btnSelectCatWhithDoc, textFieldSelectCatWhithDOC, "Выбор каталога для Doc файлов");
    }

    void selectCat(Button button, TextField textField, String str) {
        DirectoryChooser directoryTlgChooser = new DirectoryChooser();
        directoryTlgChooser.setTitle(str);
        String path = textField.getText();
        if (Paths.get(path).isAbsolute()) {
            File dir = new File(textField.getText());
            directoryTlgChooser.setInitialDirectory(dir);
        } else directoryTlgChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File dir = directoryTlgChooser.showDialog(button.getScene().getWindow());
        if (dir != null) {
            textField.setText(dir.getAbsolutePath());
        } else textField.setText(path);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnSave.setGraphic(fontAwesome.create("SAVE"));
        //btnSelectCatWhithTlg.setGraphic(fontAwesome.create('\uf07c'));
        try {
            //проверка существует ли еще путь
            String pathPDF = getPropertyApp().getPathPDF();
            if (pathPDF == null) {
                textFieldSelectCatWhithPDF.setText(System.getProperty("user.home"));
                getPropertyApp().setPathPDF(System.getProperty("user.home"));
            } else textFieldSelectCatWhithPDF.setText(pathPDF);

            String pathTLG = getPropertyApp().getPathTLG();
            if (pathTLG == null) {
                textFieldSelectCatWhithTLG.setText(System.getProperty("user.home"));
                getPropertyApp().setPathTLG(System.getProperty("user.home"));
            } else textFieldSelectCatWhithTLG.setText(pathTLG);

            String service= getPropertyApp().getService();
            if (service == null) {
                textFieldService.setText("");
            } else textFieldService.setText(service);

            String pathDOC= getPropertyApp().getPathDOC();
            if (pathDOC == null) {
                textFieldSelectCatWhithDOC.setText(System.getProperty("user.home"));
                getPropertyApp().setPathDOC(System.getProperty("user.home"));
            } else textFieldSelectCatWhithDOC.setText(pathDOC);

            sliderMinStringsOnNewPage.setValue(getPropertyApp().getMinStringsOnNewPage());
            textFieldSelectCatWhithPDF.setEditable(false);
            textFieldSelectCatWhithTLG.setEditable(false);
            textFieldSelectCatWhithDOC.setEditable(false);
            validation.setErrorDecorationEnabled(true);
            //validation.registerValidator(textFieldSelectCatWhithPDF, Validator.createEmptyValidator("Введите путь"));
            //validation.registerValidator(textFieldSelectCatWhithTLG, Validator.createEmptyValidator("Введите путь"));
            //saveButton.disableProperty().bind(validation.invalidProperty());
            Pattern patternNumbComp = Pattern.compile(".{0,20}");
            TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
                return patternNumbComp.matcher(change.getControlNewText()).matches() ? change : null;
            });
            textFieldNumbComp.setTextFormatter(formatter);
            textFieldNumbComp.setText(getPropertyApp().getNumberComputer());
            //первый символ не может быть пробелом
            textFieldNumbComp.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (newValue.trim().length() == 0) {
                        textFieldNumbComp.setText("");
                    }
                }
            });

            btnSave.disableProperty().bind(
                    Bindings.isEmpty(textFieldNumbComp.textProperty())
                            .or(Bindings.isEmpty(textFieldSelectCatWhithTLG.textProperty()))
                            .or(Bindings.isEmpty(textFieldSelectCatWhithPDF.textProperty()))
                            .or(Bindings.isEmpty(textFieldSelectCatWhithDOC.textProperty()))
            );
        } catch (Exception e) {
            logger.error("Error message", e);
        }

    }
}
