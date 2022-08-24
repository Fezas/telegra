/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.dao.CategoryDAO;
import fezas.telegra.entity.Category;
import fezas.telegra.util.ValidatorTelegra;
import javafx.beans.binding.Bindings;
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

import java.net.URL;
import java.util.*;

public class CategoryController implements Initializable {
    private static ObservableList<Category> usersData = FXCollections.observableArrayList();
    private static Map<Integer, String > categoriesMap = new HashMap<>();
    private static List<Integer> deleteItems = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger();
    private static Integer idRecord = 0;

    @FXML
    private Button saveButton;

    @FXML
    private TextArea inputCategoryDesc;

    @FXML
    private Button closeButton;

    @FXML
    private TableColumn<Category, String> categoryColumn;

    @FXML
    private TableColumn<Category, String> descColumn;

    @FXML
    private TextField inputCategory;

    @FXML
    private TableView<Category> tableCategory;

    private void showAlertUpdate(String name, Category category) {
        CategoryDAO categoryDAO = CategoryDAO.getInstance();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Внимание");
        alert.setHeaderText("Запись существует");
        alert.setContentText("Заменить пункт: " + name + "?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null) {

        } else if (option.get() == ButtonType.OK) {
            categoryDAO.update(category);
            inputCategory.clear();
            inputCategoryDesc.clear();
            initData();
            tableCategory.refresh();
            idRecord = 0;
        } else if (option.get() == ButtonType.CANCEL) {

        } else {

        }
    }
    @FXML
    private void saveCat(ActionEvent event) {
        CategoryDAO categoryDAO = CategoryDAO.getInstance();
        String inputCat = inputCategory.getText();
        String inputCatDesc = inputCategoryDesc.getText();
        Category category = new Category();
        category.setCategoryName(inputCat);
        category.setCategoryDesc(inputCatDesc);
        if (idRecord!=0) {
            category.setCategoryId(idRecord);
            showAlertUpdate(inputCat, category);
        } else  {
            categoryDAO.save(category);
            inputCategory.clear();
            inputCategoryDesc.clear();
            initData();
            tableCategory.refresh();
        }
    }
    @FXML
    public void handleCloseButtonAction(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            initData();
            ValidatorTelegra validatorTelegra = new ValidatorTelegra();
            validatorTelegra.textFieldLenght(inputCategory,200);
            validatorTelegra.textAreaLenght(inputCategoryDesc,200);
            //кнопка сохранить активна если все поля заполнены
            saveButton.disableProperty().bind(
                    Bindings.isEmpty(inputCategory.textProperty())
                            .or(Bindings.isEmpty(inputCategoryDesc.textProperty()))
            );
            //перенос текста в TextArea
            inputCategoryDesc.setWrapText(true);
            tableCategory.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            tableCategory.setRowFactory(
                    new Callback<TableView<Category>, TableRow<Category>>() {
                        @Override
                        public TableRow<Category> call(TableView<Category> tableView) {
                            final TableRow<Category> row = new TableRow<>();
                            row.setOnMouseClicked(event -> {
                                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                                    Category rowData = row.getItem();
                                    System.out.println(rowData);
                                }
                            });
                            final ContextMenu rowMenu = new ContextMenu();
                            MenuItem editItem = new MenuItem("Редактировать");
                            editItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    inputCategory.setText(row.getItem().getCategoryName());
                                    inputCategoryDesc.setText(row.getItem().getCategoryDesc());
                                    idRecord = row.getItem().getCategoryId();
                                }
                            });
                            MenuItem removeItem = new MenuItem("Удалить пункт");
                            removeItem.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    Alert alertDelete = new Alert(Alert.AlertType.CONFIRMATION);
                                    alertDelete.setTitle("Внимание");
                                    alertDelete.setHeaderText("Удаление записи");
                                    alertDelete.setContentText("Удалить запись: " + row.getItem().getCategoryName() + "?");
                                    Optional<ButtonType> option = alertDelete.showAndWait();
                                    if (option.get() == null) {

                                    } else if (option.get() == ButtonType.OK) {
                                        CategoryDAO categoryDAO = CategoryDAO.getInstance();
                                        categoryDAO.delete(row.getItem().getCategoryId());
                                        tableCategory.getItems().remove(row.getItem());
                                        inputCategory.clear();
                                        inputCategoryDesc.clear();
                                        initData();
                                        tableCategory.refresh();
                                    } else if (option.get() == ButtonType.CANCEL) {

                                    }



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
                    }
            );


            descColumn.setCellFactory(tc -> {
                TableCell<Category, String> cell = new TableCell<>();
                Text text = new Text();
                cell.setGraphic(text);
                cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
                text.wrappingWidthProperty().bind(descColumn.widthProperty());
                text.textProperty().bind(cell.itemProperty());
                return cell ;
            });

            // устанавливаем тип и значение которое должно хранится в колонке
            categoryColumn.setCellValueFactory(new PropertyValueFactory<Category, String>("categoryName"));
            descColumn.setCellValueFactory(new PropertyValueFactory<Category, String>("categoryDesc"));
            // заполняем таблицу данными
            tableCategory.setItems(usersData);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }
    private static void initData() {
        usersData.clear();
        var categories = CategoryDAO.getInstance().findAll();
        for (Category category : categories) {
            usersData.add(new Category(category.getCategoryId(), category.getCategoryName(),category.getCategoryDesc()));
            categoriesMap.put(category.getCategoryId(), category.getCategoryName());
        }
    }
}
