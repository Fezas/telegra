package fezas.telegra.controllers;

import fezas.telegra.dao.TelegrammaDAO;
import fezas.telegra.entity.Telegramma;
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
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private static ObservableList<Telegramma> usersData = FXCollections.observableArrayList();
    private static Telegramma currentTlg = new Telegramma();
    private static final Logger logger = LogManager.getLogger();

    @FXML    private TableColumn<Telegramma, String> dateGreateColumn;
    @FXML    private TableColumn<Telegramma, String> numbColumn;
    @FXML    private TableColumn<Telegramma, String> categoryColumn;
    @FXML    private TableColumn<Telegramma, String> secrecyColumn;
    @FXML    private TableColumn<Telegramma, String> titleColumn;
    @FXML    private TableColumn<Telegramma, Integer> versionColumn;
    @FXML    private TableColumn<Telegramma, String> supervisorColumn;
    @FXML    private TableColumn<Telegramma, String> tlgCheckColumn;
    @FXML    private TableView<Telegramma> tlgsTable = new TableView<>();
    @FXML    private MenuItem usersItem;
    @FXML    private Label labelOperator;
    private static MainController instance;
    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    private TelegrammController telegrammController = TelegrammController.getInstance();
    private AppController appController = AppController.getInstance();

    @FXML
    private void template(ActionEvent event) {
    }

    private void createScene(ActionEvent event, String nameResourceXML, String title, Boolean resizable) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + nameResourceXML));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/telegra.png"))));
            stage.setResizable(resizable);
            stage.showAndWait();
        } catch (NullPointerException e) {
            logger.error("Error", e);
            System.exit(-1);
        } catch (Exception e) {
            logger.error("Error", e);
        }
    }


    @FXML
    private void address(ActionEvent event) {
        createScene(event, "address.fxml", "Адреса", false);
    }

    @FXML
    private void about(ActionEvent event) {
        createScene(event, "about.fxml", "О программе", false);
    }

    @FXML
    private void secrecy(ActionEvent event) {
        createScene(event, "secrecy.fxml", "Уровни конфиденциальности", false);
    }

    @FXML
    private void categoryEdit(ActionEvent event) {
        createScene(event, "category.fxml", "Срочность", false);
    }

    @FXML
    private void help(ActionEvent event) {
        createScene(event, "help.fxml", "Помощь", false);
    }

    @FXML
    private void supervisor(ActionEvent event) {
        createScene(event, "supervisor.fxml", "Руководители", false);
    }

    @FXML
    private void stateOpen(ActionEvent event) {
        createScene(event, "state.fxml", "Статистика", false);
    }

    @FXML
    private void users(ActionEvent event) {
        createScene(event, "users.fxml", "Пользователи", false);
    }

    @FXML
    private void settings(ActionEvent event) {
        createScene(event, "settings.fxml", "Настройки", false);
    }

    @FXML
    private void type(ActionEvent event) {
        createScene(event, "type.fxml", "Типы телеграмм", false);
    }

    @FXML
    private void logout(ActionEvent event) {
        Stage stage = (Stage) tlgsTable.getScene().getWindow();
        stage.close();
        createScene(event, "login.fxml", "Авторизация", false);
    }

    @FXML
    private void paragraph(ActionEvent event) {
        try {
            ParagraphController paragraphController = new ParagraphController(true);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/paragraph.fxml"));
            loader.setController(paragraphController);
            Stage stage = new Stage();
            stage.setTitle("Пункт Перечня");
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/telegra.png"))));
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (NullPointerException e) {
            logger.error("Error", e);
            System.exit(-1);
        } catch (Exception e) {
            logger.error("Error", e);
        }
    }


    @FXML
    private void newTlg(ActionEvent event) {
        try {
            setCurrentTlg(new Telegramma());
            openTlg(getClass().getResource("/views/tlg.fxml"));
        } catch (NullPointerException e) {
            logger.error("Error message", e);
            System.exit(-1);
        } catch (Exception e) {
            logger.error("Error message", e);
        }
    }

    @FXML
    private void openTlg(URL resource){
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setController(telegrammController);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Бланк телеграммы");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/telegra.png"))));
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            logger.error("Error", e);
        }
        stage.showAndWait();
    }

    @FXML
    private void openApp(URL resource){
        try {
            FXMLLoader loader = new FXMLLoader(resource);
            loader.setController(appController);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Приложения");
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/telegra.png"))));
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();
        } catch (IOException e) {
            logger.error("Error", e);
        }
    }



    public void setCurrentTlg(Telegramma telegramma){
        currentTlg = telegramma;
    }

    public Telegramma getCurrentTlg() {
        return currentTlg;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            //меню пользователей только для админа
            if (UserLoginController.getInstance().getCurrentUser().getUserRole().equals("администратор")) {
                usersItem.setVisible(true);
            } else usersItem.setVisible(false);

            initData();
            tlgsTable.setEditable(true);
            tlgsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tlgsTable.setRowFactory(
                    new Callback<TableView<Telegramma>, TableRow<Telegramma>>() {
                        @Override
                        public TableRow<Telegramma> call(TableView<Telegramma> tableView) {
                            //событие по двойному клику строки
                            final TableRow<Telegramma> row = new TableRow<>();
                            row.setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                                    CreateDocFromTemplate doc = new CreateDocFromTemplate(row.getItem());
                                    doc.createDOC();
                                }
                            });
                            //контексное меню
                            final ContextMenu rowMenu = new ContextMenu();
                            MenuItem editItem = new MenuItem("Редактировать");
                            MenuItem addAppItem = new MenuItem("Добавить приложение");
                            MenuItem createDocItem = new MenuItem("Создать DOC");
                            MenuItem archiveItem = new MenuItem("В архив");
                            MenuItem removeItem = new MenuItem("Удалить");
                            editItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    try {
                                        setCurrentTlg(row.getItem());
                                        openTlg(getClass().getResource("/views/tlg.fxml"));
                                    } catch (NullPointerException e) {
                                        new Alert(Alert.AlertType.ERROR, "Не найдена view tlg.fxml").showAndWait();
                                        logger.error("Error", e);
                                        System.exit(-1);
                                    } catch (Exception e) {
                                        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                                        logger.error("Error", e);
                                    }
                                }
                            });
                            addAppItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    try {
                                        setCurrentTlg(row.getItem());
                                        openApp(getClass().getResource("/views/application.fxml"));
                                    } catch (NullPointerException e) {
                                        new Alert(Alert.AlertType.ERROR, "Не найдена view application.fxml").showAndWait();
                                        logger.error("Error", e);
                                        System.exit(-1);
                                    } catch (Exception e) {
                                        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                                        logger.error("Error", e);
                                    }

                                }
                            });
                            createDocItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    CreateDocFromTemplate doc = new CreateDocFromTemplate(row.getItem());
                                    doc.createDOC();
                                }
                            });
                            archiveItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                }
                            });
                            removeItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    Alert alertDelete = new Alert(Alert.AlertType.CONFIRMATION);
                                    alertDelete.setTitle("Внимание");
                                    alertDelete.setHeaderText("Удаление записи");
                                    alertDelete.setContentText("Удалить запись: " + row.getItem().getTitle() + "?");
                                    Optional<ButtonType> option = alertDelete.showAndWait();
                                    if (option.get() == null) {

                                    } else if (option.get() == ButtonType.OK) {
                                        TelegrammaDAO telegrammaDAO = TelegrammaDAO.getInstance();
                                        telegrammaDAO.delete(row.getItem().getTlgId());
                                        tlgsTable.getItems().remove(row.getItem());
                                        initData();
                                        tlgsTable.refresh();
                                    } else if (option.get() == ButtonType.CANCEL) {

                                    }
                                }
                            });
                            rowMenu.getItems().addAll(editItem, addAppItem, createDocItem, archiveItem, removeItem);
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
            tlgCheckColumn.setCellValueFactory(new PropertyValueFactory<>("remark"));
            numbColumn.setCellValueFactory(new PropertyValueFactory<>("tlgNumber"));
            dateGreateColumn.setCellValueFactory(new PropertyValueFactory<>("formatTlgDateGreate"));
            versionColumn.setCellValueFactory(new PropertyValueFactory<>("tlgVersion"));
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            secrecyColumn.setCellValueFactory(new PropertyValueFactory<>("secrecy"));
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            supervisorColumn.setCellValueFactory(new PropertyValueFactory<>("supervisor"));
            labelOperator.setText(
                    UserLoginController.getInstance().getCurrentUser().getUserRole() + " " +
                    UserLoginController.getInstance().getCurrentUser().getUserPosition() + " " +
                    UserLoginController.getInstance().getCurrentUser().getUserFIO()
            );
            // заполняем таблицу данными
            tlgsTable.setItems(usersData);
        } catch (Exception throwables) {
            throwables.printStackTrace();
            logger.error("Error message", throwables);
        }
    }
    public void initData() {
        usersData.clear();
        var tlgs = TelegrammaDAO.getInstance().findAll();
        for (Telegramma telegramma : tlgs) {
            usersData.add(new Telegramma(
                    telegramma.getTlgId(),
                    telegramma.getCategory(),
                    telegramma.getSecrecy(),
                    telegramma.getSupervisor(),
                    telegramma.getTitle(),
                    telegramma.getText(),
                    telegramma.getTlgDateGreate(),
                    telegramma.getTlgDateInput(),
                    telegramma.getTlgDateEdit(),
                    telegramma.getTlgNumber(),
                    telegramma.getTlgRead(),
                    telegramma.getTlgDraft(),
                    telegramma.getTlgSample(),
                    telegramma.getTlgArchive(),
                    telegramma.getTlgRespect(),
                    telegramma.getTlgVersion(),
                    telegramma.getApps(),
                    ""
            ));
        }
    }
}
