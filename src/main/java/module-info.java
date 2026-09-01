/**
 * @author Group 9
 */
module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    requires transitive java.sql;
    requires com.zaxxer.hikari;

    opens org.example to javafx.fxml;
    opens org.example.controller to javafx.fxml;

    exports org.example;
    exports org.example.controller;
    exports org.example.model;
    exports org.example.service;
    exports org.example.db;
}
