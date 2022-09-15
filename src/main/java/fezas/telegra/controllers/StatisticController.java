/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.exception.DaoException;
import fezas.telegra.util.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StatisticController implements Initializable {
    private static ObservableList<Characteristic> data = FXCollections.observableArrayList();
    @FXML    private Label labelSizeBd, labelCoutTlg, labelCoutTlgArchive;

    @FXML    private TableColumn<Characteristic, String> columnСharacName;
    @FXML    private TableColumn<Characteristic, String> columnСharacValue;
    @FXML    private TableView<Characteristic> tableСharac;

    private String countTlg() {
        try (var connection = ConnectionManager.get();
             var statement  = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(""" 
                SELECT COUNT(*) FROM tlg 
            """);
            if (resultSet.next()) return resultSet.getString("COUNT(*)");//для pg просто count
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
        return null;
    }
    private String memoryUsed() {
        try (var connection = ConnectionManager.get();
             var statement  = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(""" 
                SELECT MEMORY_USED() 
            """);
            if (resultSet.next()) return resultSet.getString("MEMORY_USED()");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
        return null;
    }
    private String version() {
        try (var connection = ConnectionManager.get();
             var statement  = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(""" 
                SELECT H2VERSION() 
            """);
            if (resultSet.next()) return resultSet.getString("H2VERSION()");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DaoException(throwables);
        }
        return null;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        data.add(new Characteristic ("Версия базы данных", version()));
        data.add(new Characteristic ("Количество записей в базе данных", countTlg()));
        data.add(new Characteristic ("Объем используемой памяти, Кб", memoryUsed()));
        // устанавливаем тип и значение которое должно хранится в колонке
        columnСharacName.setCellValueFactory(new PropertyValueFactory<Characteristic, String>("characterName"));
        columnСharacValue.setCellValueFactory(new PropertyValueFactory<Characteristic, String>("characterValue"));
        // заполняем таблицу данными
        tableСharac.setItems(data);
    }

    @Getter
    @Setter
    public class Characteristic {
        public String characterName;
        public String characterValue;
        public Characteristic(String characterName, String characterValue) {
            this.characterName = characterName;
            this.characterValue = characterValue;
        }
    }
}
