package com.zhw.console.controller;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableListValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

@Component
public class AppModel {

    private StringProperty dbFilePath = new SimpleStringProperty("");
    private StringProperty username = new SimpleStringProperty("");

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    private StringProperty password = new SimpleStringProperty("");
    private ObservableListValue<String> dbFilePathHistory = new SimpleListProperty<String>(FXCollections.observableArrayList());

    private StringProperty sql = new SimpleStringProperty("");

    public ObservableList<String> getDbFilePathHistory() {
        return dbFilePathHistory.get();
    }

    public ObservableListValue<String> dbFilePathHistoryProperty() {
        return dbFilePathHistory;
    }

    public ObservableList<String> getScriptFilePathHistory() {
        return scriptFilePathHistory.get();
    }

    public ObservableListValue<String> scriptFilePathHistoryProperty() {
        return scriptFilePathHistory;
    }

    private ObservableListValue<String> sqlHistory = new SimpleListProperty<String>(FXCollections.observableArrayList());
    private ObservableListValue<String> tables = new SimpleListProperty<String>(FXCollections.observableArrayList());

    private StringProperty tableDefination = new SimpleStringProperty("");
    private StringProperty scriptFilePath = new SimpleStringProperty("");
    private ObservableListValue<String> scriptFilePathHistory = new SimpleListProperty<String>(FXCollections.observableArrayList());


    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    private StringProperty status = new SimpleStringProperty("");
    public String getDbFilePath() {
        return dbFilePath.get();
    }

    public StringProperty dbFilePathProperty() {
        return dbFilePath;
    }

    public void setDbFilePath(String dbFilePath) {
        this.dbFilePath.set(dbFilePath);
    }

    public String getSql() {
        return sql.get();
    }

    public StringProperty sqlProperty() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql.set(sql);
    }

    public ObservableList<String> getSqlHistory() {
        return sqlHistory.get();
    }

    public ObservableListValue<String> sqlHistoryProperty() {
        return sqlHistory;
    }

    public ObservableList<String> getTables() {
        return tables.get();
    }

    public ObservableListValue<String> tablesProperty() {
        return tables;
    }

    public String getTableDefination() {
        return tableDefination.get();
    }

    public StringProperty tableDefinationProperty() {
        return tableDefination;
    }

    public void setTableDefination(String tableDefination) {
        this.tableDefination.set(tableDefination);
    }

    public String getScriptFilePath() {
        return scriptFilePath.get();
    }

    public StringProperty scriptFilePathProperty() {
        return scriptFilePath;
    }

    public void setScriptFilePath(String scriptFilePath) {
        this.scriptFilePath.set(scriptFilePath);
    }
}

