package com.cydier.filedialog;

import com.cydier.filedialog.interfaze.IController;
import com.cydier.filedialog.interfaze.IThreadCompleteCallback;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BaseController implements IController, IThreadCompleteCallback {
    @Override
    public void initView(Scene scene) {

    }

    @Override
    public void initStage(Stage stage) {

    }

    @Override
    public void call(int reasonCode, Object result) {
        Platform.runLater(() -> dealThreadCallBack(reasonCode, result));
    }

    @Override
    public int getCallCode() {
        return 0;
    }

    protected void dealThreadCallBack(int reasonCode, Object result) {
    }
}
