/*
 * Copyright (c) 2022. Stepantsov P.V.
 */

package fezas.telegra.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ValidatorTelegra {

    public void textFieldLenght (TextField textField, int limit) {
        Pattern pattern = Pattern.compile(".{0," + limit + "}");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        textField.setTextFormatter(formatter);
        //первый символ не может быть пробелом
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.trim().length() == 0) {
                    textField.setText("");
                }
            }
        });
    }

    public void textAreaLenght (TextArea textArea, int limit) {
        Pattern pattern = Pattern.compile(".{0," + limit + "}");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        textArea.setTextFormatter(formatter);
        //первый символ не может быть пробелом
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.trim().length() == 0) {
                    textArea.setText("");
                }
            }
        });
    }
}
