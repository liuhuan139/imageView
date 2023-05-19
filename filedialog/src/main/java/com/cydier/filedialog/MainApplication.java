package com.cydier.filedialog;

import com.cydier.filedialog.interfaze.IController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);

        IController controller = fxmlLoader.getController();
        controller.initView(scene);
        controller.initStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}