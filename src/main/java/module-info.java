module fezas.telegra {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.apache.commons.io;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires java.desktop;
    requires java.sql;
    requires com.h2database;
    requires org.controlsfx.controls;
    requires org.apache.poi.ooxml;
    requires fr.opensagres.xdocreport.core;
    requires fr.opensagres.xdocreport.document;
    requires fr.opensagres.xdocreport.template;
    requires org.apache.logging.log4j;
    requires lombok;

    opens fezas.telegra to javafx.graphics, javafx.fxml, javafx.base, javafx.web;
    opens fezas.telegra.controllers to javafx.fxml;
    opens fezas.telegra.entity to javafx.base;
    exports fezas.telegra.controllers;
}