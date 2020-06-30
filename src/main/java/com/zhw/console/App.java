package com.zhw.console;

import com.zhw.console.utils.AppUtil;
import com.zhw.console.utils.AppUtilImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Configuration
@ComponentScan("com.zhw.console")
public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pane = new FXMLLoader().load(getClass().getResourceAsStream("/splash.fxml"));
        Scene scene = new Scene(pane);
        Stage stage = new Stage(StageStyle.UNDECORATED);


        stage.setScene(scene);
        stage.show();


        CompletableFuture
                .supplyAsync(()->{
                    prepareEnviement();
                    return new AnnotationConfigApplicationContext(App.class);
                })
                .whenComplete((ctx,ex)->{
                    if(ex==null){
                        Platform.runLater(()-> startMain(ctx));
                    }else {
                        Platform.runLater(()-> {
                            Alert alert = new Alert(Alert.AlertType.ERROR,"系统启动失败", ButtonType.CLOSE);
                            alert.showAndWait();
                            Platform.exit();
                        });
                    }
                })
                .whenComplete((ctx,error)->Platform.runLater(() -> stage.close()));

    }

    private void prepareEnviement(){
        Path path = Paths.get("fb").toAbsolutePath();
        System.setProperty("jna.library.path", path.toString());

    }

    private void startMain(ApplicationContext applicationContext) {
        Pane pane = null;
        try {
            pane = AppUtil.loadFXML("/main.fxml");
            Scene scene = new Scene(pane);
            Stage stage = new Stage();


            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
