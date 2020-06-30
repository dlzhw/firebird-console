package com.zhw.console.utils;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.springframework.context.ApplicationContext;

public class AppUtilImpl extends AppUtil{

    public AppUtilImpl(ApplicationContext applicationContext){
        AppUtil.setApplicationContext(applicationContext);
    }

    public void setRootScene(Scene rootScene) {
        AppUtil.setAppRootScene(rootScene);
    }

    public void setRootNode(Pane rootNode) {
        AppUtil.setAppRootNode(rootNode);
    }

}
