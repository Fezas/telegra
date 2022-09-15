/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.*;
import fezas.telegra.entity.*;
import fezas.telegra.util.ValidatorTelegra;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.UnaryOperator;

public class TelegrammController implements Initializable {
    private static final Logger logger = LogManager.getLogger();
    public ObservableList<Address> addressWithChoice = FXCollections.observableArrayList();
    public ObservableList<Category> categories = FXCollections.observableArrayList();
    public ObservableList<Secrecy> secrecies = FXCollections.observableArrayList();
    public ObservableList<Supervisor> supervisors = FXCollections.observableArrayList();
    private ObservableList<Address> usersData = FXCollections.observableArrayList();
    private ObservableList<ParagraphEntity> paragraphsList = FXCollections.observableArrayList();
    private ObservableList<Type> typeList = FXCollections.observableArrayList();
    private ObservableList<Address> addressList = FXCollections.observableArrayList();
    private ObservableList<ParagraphEntity> itemsTableParagraphs = FXCollections.observableArrayList();
    private ObservableList<Type> itemsTableType = FXCollections.observableArrayList();
    private AddressDAO addressDAO = AddressDAO.getInstance();
    private MailingDAO mailingDAO = MailingDAO.getInstance();
    private ParsInTlgsDAO parsInTlgsDAO = ParsInTlgsDAO.getInstance();
    private ParagraphDAO paragraphDAO = ParagraphDAO.getInstance();
    private TelegrammaDAO telegrammaDAO = TelegrammaDAO.getInstance();
    private TypeInTlgDAO typeInTlgDAO = TypeInTlgDAO.getInstance();
    private TypeDAO typeDAO = TypeDAO.getInstance();
    private AppController appController = AppController.getInstance();
    private static TelegrammController instance;
    public Telegramma currentTlg = new Telegramma();
    public ObservableList<ApplicationDoc> applicationDocsList = FXCollections.observableArrayList();
    private ApplicationDocDAO applicationDocDAO = ApplicationDocDAO.getInstance();
    private ParsInAppsDAO parsInAppsDAO = ParsInAppsDAO.getInstance();
    public static Map<ApplicationDoc, List<ParagraphEntity>> mapAppWithPP = new HashMap<>();
    public List<ParagraphEntity> ppApp = new ArrayList<>();
    private ApplicationDoc currentApplicationDoc = new ApplicationDoc();
    private ObservableList<Secrecy> secreciesApp = FXCollections.observableArrayList();
    private ValidationSupport validationSupport = new ValidationSupport();
    private GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");

    public static TelegrammController getInstance() {
        if (instance == null) {
            instance = new TelegrammController();
        }
        return instance;
    }

    @FXML    private TableView<ParagraphEntity> tableSecParagraph;
    @FXML    private TableColumn<ParagraphEntity, Integer> paragraphColumn;
    @FXML    private TableColumn<ParagraphEntity, String> contentColumn, paragraphCheckColumn;

    @FXML    private TableView<Address> tableAddress;
    @FXML    private TableColumn<Address, String> addressCallsignColumn, addressColumn, addressCheckColumn,
            addressPersonColumn, addressPersonRespectColumn;

    @FXML    private TableColumn<Type, String> typeColumn, typeDescColumn, typeCheckColumn;
    @FXML    private TableView<Type> tableType;

    @FXML    private CheckBox selectAll;
    @FXML    private ComboBox<Category> selectCategory;
    @FXML    private ComboBox<Secrecy> selectSecrecy;
    @FXML    private ChoiceBox<Supervisor> selectSupervisor;
    @FXML    private WebView webTlg;
    @FXML    private WebView webViewInputTlgText;
    @FXML    private TextField titleTlg, textFieldTitle;
    @FXML    private Label countPages, countChar;
    @FXML    private Button saveButton, btnApp, closeButton, selectAddressButton, btnAddressBook;
    @FXML    public TextArea textTlg, choiceTypeText;
    @FXML    private CheckComboBox<Integer> checkParagraphs;
    @FXML    private CheckBox checkBoxRespect;

    @FXML
    private void appView () {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/application.fxml"));
            loader.setController(appController);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Приложения");
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();
        } catch (IOException e) {
            logger.error("Error", e);
        }
    }

    public ObservableList<Address> getAddressWithChoice() {
        MainController mainController = MainController.getInstance();
        //заполняем адреса
        ObservableList<Address> addressesAll = FXCollections.observableArrayList();
        addressesAll.addAll(addressDAO.findAll());
        List<Mailing> mailings = mailingDAO.findByTlgId(mainController.getCurrentTlg().getTlgId());
        //заполняем чекбоксы выбора адреса
        for (Address address : addressesAll) {
            for (Mailing mailing : mailings) {
                if (mailing.getAddress().getTlgAddressId().equals(address.getTlgAddressId())) {
                    address.getRemark().setSelected(true);
                }
            }
        }
        return addressesAll;
    }

    public ObservableList<ParagraphEntity> getParagraphsWithChoice() {
        MainController mainController = MainController.getInstance();
        //заполняем адреса
        ObservableList<ParagraphEntity> paragraphsAll = FXCollections.observableArrayList();
        paragraphsAll.addAll(paragraphDAO.findAll());
        List<ParagraphEntity> parsInTlgs = parsInTlgsDAO.findParsByTlgId(mainController.getCurrentTlg().getTlgId());
        //заполняем чекбоксы выбора адреса
        for (ParagraphEntity paragraph : paragraphsAll) {
            for (ParagraphEntity parsInTlg : parsInTlgs) {
                if (parsInTlg.getSecrecyParagraphId().equals(paragraph.getSecrecyParagraphId())) {
                    paragraph.getRemark().setSelected(true);
                }
            }
        }
        return paragraphsAll;
    }

    public ObservableList<Type> getTypeWithChoice() {
        MainController mainController = MainController.getInstance();
        //заполняем типы
        ObservableList<Type> typesAll = FXCollections.observableArrayList();
        typesAll.addAll(typeDAO.findAll());
        List<Type> typesInTlgs = typeInTlgDAO.findByTlgId(mainController.getCurrentTlg().getTlgId());
        //заполняем чекбоксы выбора адреса
        for (Type type : typesAll) {
            for (Type typeInTlgs : typesInTlgs) {
                if (typeInTlgs.getTypeId().equals(type.getTypeId())) {
                    type.getRemark().setSelected(true);
                }
            }
        }
        return typesAll;
    }

    @FXML
    private void actionAddressBook(ActionEvent event) {
        try {
            MainController mainController = MainController.getInstance();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/address.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Адресная книга");
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            if (mainController.getCurrentTlg().getTlgId() == null) {
                tableAddress.setItems(initAddress());
            } else tableAddress.setItems(getAddressWithChoice());
            tableAddress.refresh();
        } catch (NullPointerException e) {
            new Alert(Alert.AlertType.ERROR, "Не найдена view address.fxml").showAndWait();
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    public void setTextType(ObservableList<Type> types) {
        choiceTypeText.setText("");
        for (Type item : types) {
            if(item.getRemark().isSelected()) {
                choiceTypeText.appendText(item.getTypeName() + ", ");
            }
        }
        choiceTypeText.deleteText(choiceTypeText.getText().length() - 2, choiceTypeText.getText().length());
    }

    private void selectCategory() {
        CategoryDAO categoryDAO = CategoryDAO.getInstance();
        categories.clear();
        categories.addAll(categoryDAO.findAll());
        selectCategory.getItems().addAll(categories);
    }

    private void selectSecrecy() {
        SecrecyDAO secrecyDAO = SecrecyDAO.getInstance();
        secrecies.clear();
        secrecies.addAll(secrecyDAO.findAll());
        selectSecrecy.getItems().addAll(secrecies);
    }

    /**
     * Заполнение select списка исполнителей
     */
    private void selectSupervisor() {
        SupervisorDAO supervisorDAO = SupervisorDAO.getInstance();
        supervisors.clear();
        supervisors.addAll(supervisorDAO.findAll());
        selectSupervisor.getItems().addAll(supervisors);
        //выбор исполнителя по умолчанию
        for (Supervisor supervisor : supervisors) {
            if (supervisor.isDef()) selectSupervisor.getSelectionModel().select(supervisor);
        }
    }
    /**
     * Заполнение формы при редактировании телеграммы
     */

    private void editTlg() {
        getAddressWithChoice();
        //заполняем пункты перечня
        ObservableList<ParagraphEntity> paragraphsAll = FXCollections.observableArrayList();
        MainController mainController = MainController.getInstance();
        List<ParagraphEntity> parsInTlgs = parsInTlgsDAO.findParsByTlgId(mainController.getCurrentTlg().getTlgId());
        paragraphsAll.addAll(paragraphDAO.findAll());
        for (ParagraphEntity paragraph : paragraphsAll) {
            for (ParagraphEntity pars : parsInTlgs) {
                if (pars.getSecrecyParagraphId().equals(paragraph.getSecrecyParagraphId())) {
                    paragraph.getRemark().setSelected(true);
                }
            }
        }

        //исполнитель
        selectSupervisor.getSelectionModel().select(mainController.getCurrentTlg().getSupervisor());
        //конфиденциальность
        selectSecrecy.getSelectionModel().select(mainController.getCurrentTlg().getSecrecy());
        //категория
        selectCategory.getSelectionModel().select(mainController.getCurrentTlg().getCategory());
        //название телеграммы
        titleTlg.setText(mainController.getCurrentTlg().getTitle());
        //вывод текста телеграммы
        WebEngine webEngine = webTlg.getEngine();
        //Есть ли уважительное обращение
        checkBoxRespect.setSelected(mainController.getCurrentTlg().getTlgRespect());
        //заполнение таблиц пунктов Перечня и адресов
        tableAddress.setItems(getAddressWithChoice());
        tableSecParagraph.setItems(getParagraphsWithChoice());
        tableType.setItems(getTypeWithChoice());
        initWebView(mainController.getCurrentTlg().getText());

        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            public void handle(WebEvent<String> event) {
                if ("variables:ready".equals(event.getData())) {
                    logger.info("INFO: document loaded");
                }
            }
        });
    }

    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        MainController mainController = MainController.getInstance();
        mainController.setCurrentTlg(null);
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void preview(){}

    private void showAlertUpdate(String name, Telegramma telegramma) {
        MainController mainController = MainController.getInstance();
        TelegrammaDAO telegrammaDAO = TelegrammaDAO.getInstance();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Внимание");
        alert.setHeaderText("Запись существует");
        alert.setContentText("Внести изменения в телеграмму: " + name + "?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null) {

        } else if (option.get() == ButtonType.OK) {
            telegramma.setTlgVersion(mainController.getCurrentTlg().getTlgVersion() + 1);
            telegrammaDAO.update(telegramma);
            mainController.initData();
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        } else if (option.get() == ButtonType.CANCEL) {

        } else {

        }
    }

    @FXML
    private void saveTlg(ActionEvent event) {
        MainController mainController = MainController.getInstance();
        Telegramma currTlg;
        Telegramma telegramma = new Telegramma();
        Date today = new Date();
        //формирование содержимого страницы в <body>
        String html = (String) webTlg.getEngine().executeScript("document.getElementById(\"document\").outerHTML");
        html = html
                .replaceAll("\\n|\\r", "")
                .replaceAll("<br>","")
                .replaceAll("<script>setTimeout\\(function\\(\\)\\{alert\\('variables:ready'\\);\\}\\);</script></body>","")
                .replaceAll("<body contenteditable=\"true\" id=\"document\">","")
                .replaceAll("</body>","")
                .replaceAll("<p class=\"tlg-text\"></p>","");
        telegramma.setText(html);
        telegramma.setTitle(titleTlg.getText());
        telegramma.setCategory(selectCategory.getValue());
        telegramma.setSecrecy(selectSecrecy.getValue());
        telegramma.setSupervisor(selectSupervisor.getValue());
        telegramma.setTlgDateGreate(new Timestamp(today.getTime()));
        telegramma.setTlgRead(true);
        telegramma.setTlgDraft(false);
        telegramma.setTlgSample(false);
        telegramma.setTlgArchive(false);
        //порядок обращения
        telegramma.setTlgRespect(checkBoxRespect.isSelected());

        if (mainController.getCurrentTlg().getTlgId() != null) {
            telegramma.setTlgId(mainController.getCurrentTlg().getTlgId());
            showAlertUpdate(titleTlg.getText(), telegramma);
            currTlg = mainController.getCurrentTlg();
        } else  {
            telegramma.setTlgVersion(0);
            currTlg = telegrammaDAO.save(telegramma);
            logger.info("OPERATION: Телеграмма " + currTlg.toString() + " сохранена");
        }

       //сохранение адресов телеграммы
        boolean checked = false;
        addressList = tableAddress.getItems();
        if (mainController.getCurrentTlg().getTlgId() != null) mailingDAO.clear(currTlg.getTlgId());
        for (Address item : addressList) {
            if (item.getRemark().isSelected()) {
                mailingDAO.save(currTlg.getTlgId(), item.getTlgAddressId());
                checked = true;
            }
        }
        if (!checked) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка добавления адреса");
            alert.setHeaderText(null);
            alert.setContentText("Не выбран ни один адрес");
            alert.showAndWait();
        }

        //сохранение пунктов Перечня телеграммы
        itemsTableParagraphs = tableSecParagraph.getItems();
        if (mainController.getCurrentTlg().getTlgId() != null) parsInTlgsDAO.clear(currTlg.getTlgId());
        for (ParagraphEntity item : itemsTableParagraphs) {
            if (item.getRemark().isSelected()) {
                parsInTlgsDAO.save(currTlg.getTlgId(), item.getSecrecyParagraphId());
            }
        }

        //сохранение типов телеграммы
        itemsTableType = tableType.getItems();
        if (mainController.getCurrentTlg().getTlgId() != null) typeInTlgDAO.clear(currTlg.getTlgId());
        for (Type item : itemsTableType) {
            if (item.getRemark().isSelected()) {
                typeInTlgDAO.save(currTlg.getTlgId(), item.getTypeId());
            }
        }

        if(!mapAppWithPP.isEmpty()) {
            for (Map.Entry<ApplicationDoc, List<ParagraphEntity>> entry : mapAppWithPP.entrySet()) {
                if (entry.getKey().getAppId() != null) {
                    applicationDocDAO.update(entry.getKey());
                    parsInAppsDAO.clear(entry.getKey().getAppId());
                } else {
                    entry.getKey().setTlgId(currTlg.getTlgId());
                    applicationDocDAO.save(entry.getKey());
                }
                //сохранение пунктов приложений телеграммы
                for (ParagraphEntity p : entry.getValue()) {
                    parsInAppsDAO.save(entry.getKey().getAppId(), p.getSecrecyParagraphId());
                }
            }
        }

        mainController.initData();
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void initWebView(String html) {
        WebEngine webEngine = webTlg.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.setUserStyleSheetLocation(getClass().getResource("/css/webview-tlg.css").toString());
        String content = "<!DOCTYPE html>\n" +
                "<html lang=\"ru\">\n" +
                "<head><meta charset=\"UTF-8\"><title>Title</title></head>\n" +
                "<body contenteditable=\"true\" id=\"document\">" + html + "</body>\n" +
                "</html>";
        webEngine.loadContent(content);
        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
                    public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                        if (newState == Worker.State.SUCCEEDED) {
                            try {
                                webEngine.executeScript("const target = document.querySelector('#document');\n" +
                                        "\n" +
                                        "target.addEventListener('paste', (event) => {\n" +
                                        "    let paste = (event.clipboardData || window.clipboardData).getData('text');\n" +
                                        "    const selection = window.getSelection();\n" +
                                        "    if (!selection.rangeCount) return false;\n" +
                                        "    selection.deleteFromDocument();\n" +
                                        "    selection.getRangeAt(0).insertNode(document.createTextNode(paste));\n" +
                                        "\n" +
                                        "    event.preventDefault();\n" +
                                        "});");
                                String htmlTlg = (String) webEngine.executeScript("document.documentElement.outerHTML");
                            } catch (Exception ex) {
                                logger.error(ex);
                            }
                        }
                    }
                });

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnApp.setGraphic(fontAwesome.create("FILE"));
        saveButton.setGraphic(fontAwesome.create("SAVE"));
        btnAddressBook.setGraphic(fontAwesome.create("BOOK"));
        try {
            /*createEmptyValidator заменить на Regex*/
            validationSupport.registerValidator(titleTlg, Validator.createEmptyValidator("Заголовок не менее 5 знаков!"));
            mapAppWithPP = new HashMap<>();
            tableSecParagraph.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            //Всплывающие подсказки
            tableSecParagraph.setRowFactory(tableView -> {
                final TableRow<ParagraphEntity> row = new TableRow<>();
                row.hoverProperty().addListener((observable) -> {
                    final ParagraphEntity paragraph = row.getItem();
                    if (row.isHover() && paragraph != null) {
                        row.setTooltip(new Tooltip(paragraph.toString()));
                    }
                });
                return row;
            });

            tableType.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            //ограничение символов текста телеграммы
            UnaryOperator<TextFormatter.Change> modifyChange = c -> {
                if (c.isContentChange()) {
                    int newLength = c.getControlNewText().length();
                    if (newLength > 1500) {
                        String tail = c.getControlNewText().substring(newLength - 1500, newLength);
                        c.setText(tail);
                        int oldLength = c.getControlText().length();
                        c.setRange(0, oldLength);
                    }
                }
                return c;
            };

            ValidatorTelegra validatorTelegra = new ValidatorTelegra();
            validatorTelegra.textFieldLenght(titleTlg,200);

            //валидация кнопки сохранения
            saveButton.disableProperty().bind(
                    Bindings.isEmpty(titleTlg.textProperty())
                            .or(Bindings.isNull(selectCategory.getSelectionModel().selectedItemProperty()))
                            .or(Bindings.isNull(selectSecrecy.getSelectionModel().selectedItemProperty()))
                            .or(Bindings.isNull(selectSupervisor.getSelectionModel().selectedItemProperty()))
            );

            tableAddress.setEditable(true);
            tableAddress.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            if (true) {
                //слушатели чекбоксов
                selectAll.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
                    addressList = tableAddress.getItems();
                    for (Address item : addressList) {
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
            // устанавливаем тип и значение которое должно хранится в колонках адреса
            addressCallsignColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("tlgAddressCallsign"));
            addressColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("tlgAddressName"));
            addressPersonColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("tlgAddressPerson"));
            addressPersonRespectColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("tlgAddressPersonRespect"));
            addressCheckColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("remark"));

            // устанавливаем тип и значение которое должно хранится в колонках Перечня
            paragraphColumn.setCellValueFactory(new PropertyValueFactory<ParagraphEntity, Integer>("secrecyParagraphId"));
            contentColumn.setCellValueFactory(new PropertyValueFactory<ParagraphEntity, String>("secrecyParagraphText"));
            paragraphCheckColumn.setCellValueFactory(new PropertyValueFactory<ParagraphEntity, String>("remark"));

            // устанавливаем тип и значение которое должно хранится в колонке
            typeColumn.setCellValueFactory(new PropertyValueFactory<Type, String>("typeName"));
            typeDescColumn.setCellValueFactory(new PropertyValueFactory<Type, String>("typeDesc"));
            typeCheckColumn.setCellValueFactory(new PropertyValueFactory<Type, String>("remark"));
            titleTlg.textProperty().addListener((observable, oldValue, newValue) -> {
                newValue = newValue.replaceAll("#","_");
                newValue = newValue.replaceAll("\\!","_");
                newValue = newValue.replaceAll("@","_");
                newValue = newValue.replaceAll("%","_");
                newValue = newValue.replaceAll("\\$","_");
                newValue = newValue.replaceAll("\\^","_");
                newValue = newValue.replaceAll("&","_");
                newValue = newValue.replaceAll("\\*","_");
                newValue = newValue.replaceAll("\\\"","_");
                newValue = newValue.replaceAll("№","_");
                newValue = newValue.replaceAll(";","_");
                newValue = newValue.replaceAll(":","_");
                newValue = newValue.replaceAll("\\?","_");
                newValue = newValue.replaceAll("\\[","_");
                newValue = newValue.replaceAll("\\]","_");
                titleTlg.setText(newValue);
            });

            //проверка условия, что телеграмма - новая
            MainController mainController = MainController.getInstance();
            if(mainController.getCurrentTlg().getTlgId() != null) {
                editTlg();
            } else {
                initWebView("<p class=\"tlg-text\"><br></p>\n" +
                        "<script>setTimeout(function(){alert('variables:ready');});</script>");
                tableAddress.setItems(initAddress());
                tableSecParagraph.setItems(initParagraphs());
                tableType.setItems(initType());
                addressWithChoice.clear();
            }
            selectCategory();
            selectSecrecy();
            selectSupervisor();
            initApps();

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void initApps() {
        applicationDocsList.clear();
        MainController mainController = MainController.getInstance();
        //проверка стсатуса телеграммы (новая или редактирование)
        if(mainController.getCurrentTlg().getTlgId() != null) {
            var apps = applicationDocDAO.findAllFromTlg(mainController.getCurrentTlg().getTlgId());
            for (ApplicationDoc applicationDoc : apps) {
                applicationDocsList.add(new ApplicationDoc(
                                applicationDoc.getAppId(),
                                applicationDoc.getAppName(),
                                applicationDoc.getAppExt(),
                                applicationDoc.getAppSize(),
                                applicationDoc.getAppSec(),
                                mainController.getCurrentTlg().getTlgId(),
                                applicationDoc.getAppExe(),
                                applicationDoc.getAppNumb(),
                                ""
                        )
                );
                ArrayList<ParagraphEntity> pp = new ArrayList<>();
                pp.addAll(parsInAppsDAO.findParsByAppId(applicationDoc.getAppId()));
                mapAppWithPP.put(applicationDoc, pp);
            }
        }
    }

    private ObservableList<Address> initAddress() {
        usersData.clear();
        var addresses = addressDAO.findAll();
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
        return usersData;
    }

    private ObservableList<ParagraphEntity> initParagraphs() {
        paragraphsList.clear();
        var paragraphs = paragraphDAO.findAll();
        for (ParagraphEntity paragraph : paragraphs) {
            paragraphsList.add(new ParagraphEntity(
                    paragraph.getSecrecyParagraphId(),
                    paragraph.getSecrecyParagraphText(),
                    ""
            ));
        }
        return paragraphsList;
    }

    private ObservableList<Type> initType() {
        typeList.clear();
        var types = typeDAO.findAll();
        for (Type type : types) {
            typeList.add(new Type(
                    type.getTypeId(),
                    type.getTypeName(),
                    type.getTypeDesc(),
                    ""
            ));
        }
        return typeList;
    }
}