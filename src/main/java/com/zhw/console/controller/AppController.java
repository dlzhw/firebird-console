package com.zhw.console.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.firebirdsql.gds.impl.jni.EmbeddedGDSFactoryPlugin;
import org.firebirdsql.management.FBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

@Controller
public class AppController implements Initializable {


    public TextField usernameTextField;
    public TextField passwordTextField;
    @Autowired
    private AppModel appModel;

    @FXML
    public ComboBox dbFilePathTextField;
    @FXML
    public TextArea sqlInputTextarea;
    @FXML
    public ListView sqlHistoryListView;
    @FXML
    public TableView sqlQueryResutTableView;
    @FXML
    public ListView tablesListView;
    @FXML
    public TextArea tableDefinationTextArea;
    @FXML
    public ComboBox scriptFilePathTextField;
    @FXML
    public Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        restoreModelFromHistory();

        statusLabel.textProperty().bind(appModel.statusProperty());
        dbFilePathTextField.itemsProperty().bind(appModel.dbFilePathHistoryProperty());
        dbFilePathTextField.getEditor().textProperty().bindBidirectional(appModel.dbFilePathProperty());

        tablesListView.itemsProperty().bind(appModel.tablesProperty());
        appModel.sqlProperty().bind(sqlInputTextarea.textProperty());

        appModel.scriptFilePathProperty().bind(scriptFilePathTextField.getEditor().textProperty());

        appModel.usernameProperty().bind(usernameTextField.textProperty());
        appModel.passwordProperty().bind(passwordTextField.textProperty());

    }

    private void restoreModelFromHistory() {
        Resource resource = new FileSystemResource("./.history.dat");
        if(resource.exists()){

        }
    }

    public void recordDBFilePath(ActionEvent actionEvent) {
        String path = appModel.dbFilePathProperty().getValue();
        appModel.dbFilePathHistoryProperty().add(path);
    }

    //create table tb_zhw(id int ,name varchar(32))
    public void executeSql(ActionEvent actionEvent) {

        CompletableFuture.supplyAsync(this::executeSql)
                .whenComplete((result,err)->{
                    if(err!=null){
                        setStatusLabelInfo("sql执行失败");
                    }else{
                        setStatusLabelInfo("sql执行成功");
                        if(result.isQuery()){
                            Platform.runLater(()->transferDataToTable(result));
                        }
                    }
                });
    }

    private void transferDataToTable(SqlExecuteResult sqlExecuteResult) {

        sqlQueryResutTableView.getColumns().clear();
        sqlQueryResutTableView.getItems().clear();
        List<String> columnNames = sqlExecuteResult.getColumnNames();

        for(int i=0;i<columnNames.size();i++){
            TableColumn<List<String>,String> tableColumn = new TableColumn<>(columnNames.get(i));
            final int j = i;
            tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<List<String>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<List<String>, String> param) {

                    return new SimpleStringProperty(String.valueOf(param.getValue().get(j)));
                }
            });

            sqlQueryResutTableView.getColumns().add(i,tableColumn);
        }
        sqlQueryResutTableView.getItems().addAll(sqlExecuteResult.getRowDatas());





    }

    private SqlExecuteResult executeSql() {
        String sql = appModel.getSql();
        SqlExecuteResult result = getJdbcTemplate().execute(new StatementCallback<SqlExecuteResult>() {
            @Override
            public SqlExecuteResult doInStatement(Statement stmt) throws SQLException, DataAccessException {

                SqlExecuteResult result = new SqlExecuteResult();

                stmt.execute(sql);
                ResultSet resultSet = stmt.getResultSet();

                if (resultSet != null) {
                    result.setQuery(true);

                    ResultSetMetaData metaData = resultSet.getMetaData();

                    List<String> columnNames = new ArrayList<>();
                    result.setColumnNames(columnNames);
                    int columnCount = metaData.getColumnCount();
                    for(int i=1 ;i<=columnCount ;i++){
                        columnNames.add(metaData.getColumnName(i));
                    }

                    List<List<Object>> rowDatas = new ArrayList<>();
                    while(resultSet.next()){
                        List<Object> rowData = new ArrayList<>();
                        rowDatas.add(rowData);
                        for(int i=1 ;i<=columnCount ;i++){
                            Object colval = resultSet.getObject(i);
                            rowData.add(colval);
                        }
                    }
                    result.setRowDatas(rowDatas);
                }

                return result;
            }
        });
        return result;
    }

    public void createDB(ActionEvent actionEvent) {
        String dbFilePath = appModel.getDbFilePath();



        CompletableFuture.runAsync(()->{
            Platform.runLater(()->appModel.statusProperty().setValue("开始创建数据库["+dbFilePath+"]"));
            createDatabase(dbFilePath,appModel.getUsername(),appModel.getPassword());
        }).whenComplete((rst,error)->{
            String msg = "创建成功!";
            if(error!=null){
                msg = "创建失败";
            }
            String result = msg;
            Platform.runLater(()->appModel.statusProperty().setValue(result));
            ;
        });

    }

    public void showTables(ActionEvent actionEvent) {

        CompletableFuture.supplyAsync(()->{
            setStatusLabelInfo("查询中...");
            return showTables();
        }).whenComplete((tables,error)->{
            if(error==null){
                setStatusLabelInfo("查询成功");
                Platform.runLater(()->{
                    appModel.tablesProperty().clear();
                    appModel.tablesProperty().addAll(tables);
                });
            }else {
                error.printStackTrace();
                setStatusLabelInfo("查询失败");
            }
        });
    }

    private List<String> showTables() {
        List<String> result =
        getJdbcTemplate().execute(new ConnectionCallback<List<String>>() {
            @Override
            public List<String> doInConnection(Connection con) throws SQLException, DataAccessException {
                List<String> result = new ArrayList<>();
                Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                ResultSet resultSet = con.getMetaData().getTables(null, null, null, new String[]{"TABLE", "VIEW"});

                while (resultSet.next()){
                    result.add(resultSet.getString("table_name"));
                }

                return result;
            }
        });
        return result;
    }

    public void importScript(ActionEvent actionEvent) {
        CompletableFuture.runAsync(()->{
            setStatusLabelInfo("正在导入中..");
            importScript();
        }).whenComplete((rst,error)->{
            if (error != null){
                setStatusLabelInfo("导入失败");
            }else {
                setStatusLabelInfo("导入成功");
            }
        });
    }

    private void importScript() {
        Resource resource = new FileSystemResource(appModel.getScriptFilePath());
        EncodedResource er = new EncodedResource(resource,"utf-8");
        getJdbcTemplate().execute(new ConnectionCallback<Object>() {
            @Override
            public Object doInConnection(Connection con) throws SQLException, DataAccessException {
                ScriptUtils.executeSqlScript(con,er);
                return null;
            }
        });
    }

    private void createDatabase(String dbpath,String user,String password) {
        FBManager fbManager = new FBManager(EmbeddedGDSFactoryPlugin.EMBEDDED_TYPE_NAME);
        //FBManager fbManager = new FBManager();
        try {
            fbManager.start();
            fbManager.createDatabase(dbpath, user, password);
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            try {
                fbManager.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private JdbcTemplate getJdbcTemplate(){
        String dbFilePath = appModel.getDbFilePath();
        dbFilePath = dbFilePath.replaceAll("\\\\","/");
        DriverManagerDataSource dataSource = new DriverManagerDataSource("jdbc:firebirdsql:embedded:"+dbFilePath+"?lc_ctype=UTF8&charSet=utf-8",appModel.getUsername(),appModel.getPassword());
        return new JdbcTemplate(dataSource);
    }

    private void setStatusLabelInfo(String msg){
        Platform.runLater(()->appModel.setStatus(msg));
    }
}
