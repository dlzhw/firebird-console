package com.zhw.console.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class AppUtil {
    private static final Logger logger = LoggerFactory.getLogger(AppUtil.class);
    private static ApplicationContext applicationContext;
    private static Scene appRootScene;
    private static Pane appRootNode;
    private static Parent appMainContentNode;
    private static Map<String,Pane> fxmlCache = new HashMap<>();
    public static Pane loadFXML(String fxmlPath) {
        Pane pane;
        ResourceBundle resourceBundle = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(resourceBundle);
            loader.setControllerFactory(param -> applicationContext.getBean(param));
            pane = loader.load(applicationContext.getResource("classpath:" + fxmlPath).getInputStream());
            AppUtil.saveController(pane, loader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pane;
    }

    public static <T>  T getBean(Class<T> clazz){
        if(applicationContext==null){
            throw new RuntimeException("applicationContext未初始化");
        }
        return applicationContext.getBean(clazz);
    }

    /**
     * 将Controller对象记录到节点中,用于路由回调
     *
     * @param pane
     * @param loader
     */
    private static void saveController(Pane pane, FXMLLoader loader) {
        try {
            Field ctlField = loader.getClass().getDeclaredField("controller");
            boolean old = ctlField.isAccessible();
            ctlField.setAccessible(true);
            Object controller = ctlField.get(loader);
            ctlField.setAccessible(old);

            pane.setUserData(controller);
        } catch (Exception e) {
            logger.error("为节点设置controller时出错.", e);
        }
    }

    /**
     * 取出节点中存放的Controller对象 ,用于路由
     *
     * @param pane
     * @return
     */
    public static Object fetchController(Pane pane) {
        return pane.getUserData();
    }

    protected static void setApplicationContext(ApplicationContext applicationContext) {
        AppUtil.applicationContext = applicationContext;
    }

    public static Scene getAppRootScene() {
        return appRootScene;
    }

    public static Pane getAppRootNode() {
        return appRootNode;
    }
    protected static void setAppRootScene(Scene appRootScene) {
        AppUtil.appRootScene = appRootScene;
    }

    protected static void setAppRootNode(Pane appRootNode) {
        AppUtil.appRootNode = appRootNode;
        appMainContentNode = (Parent) AppUtil.appRootNode.lookup("#mainContent");
    }
    public static Parent getAppMainContentNode() {
        return appMainContentNode;
    }
}
