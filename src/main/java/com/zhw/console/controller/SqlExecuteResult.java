package com.zhw.console.controller;

import java.util.List;
import java.util.Map;

public class SqlExecuteResult {
    boolean query;
    private List<String> columnNames;

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<List<Object>> getRowDatas() {
        return rowDatas;
    }

    private List<List<Object>> rowDatas;

    public boolean isQuery() {
        return query;
    }

    public void setQuery(boolean query) {
        this.query = query;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public void setRowDatas(List<List<Object>> rowDatas) {
        this.rowDatas = rowDatas;
    }
}
