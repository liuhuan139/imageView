package com.cydier.filedialog.utils;

import com.cydier.filedialog.interfaze.IThreadCompleteCallback;
import com.cydier.filedialog.manager.ThreadBuilder;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

public class ControllerUtils {

    public static String getFilePath(File file) {
        String ret = "";
        try {
            ret = file.getCanonicalPath();
        } catch (IOException e) {
            System.out.println("cannot get file path!");
        }
        return ret;
    }

    public static void transformDataInList(ObservableList<Image> images, List<File> files) {
        images.clear();
        files.forEach(file -> images.add(new Image(getFilePath(file))));
    }

    public static void addImageExtensionFilter(ObservableList<FileChooser.ExtensionFilter> extensionFilters) {
        extensionFilters.clear();
        extensionFilters.add(new FileChooser.ExtensionFilter("images", "*.jpg", "*.png"));
    }

    public static void addAllFileExtensionFilter(ObservableList<FileChooser.ExtensionFilter> extensionFilters) {
        extensionFilters.clear();
        extensionFilters.add(new FileChooser.ExtensionFilter("All files", "*.*"));
    }

    public static void saveImageInThread(Image image, File file, IThreadCompleteCallback callback, ThreadBuilder.ThreadType type) {
        if (image == null || file == null || callback == null || type == null) {
            return;
        }
        Callable callable = () -> {
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(image.getUrl());
            FileOutputStream fos = new FileOutputStream(file);
            while (true) {
                int len = fis.readNBytes(buffer, 0, 1024);
                if (len > 0) {
                    fos.write(buffer, 0, len);
                } else {
                    break;
                }
            }
            fis.close();
            fos.flush();
            fos.close();
            return true;
        };

        ThreadBuilder.ThreadTask futureTask = new ThreadBuilder.ThreadTask(callable);
        futureTask.setTaskId(callback.getCallCode());
        executeTaskInThread(futureTask, type, callback);
    }

    public static void executeTaskInThread( ThreadBuilder.ThreadTask futureTask, ThreadBuilder.ThreadType type, IThreadCompleteCallback callback) {
        ThreadBuilder builder = new ThreadBuilder();
        builder.setType(type).setTask(futureTask).setCallBack(callback).buildAndRun();
    }

    public static void showInformationDialog(String information) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(information);
        alert.showAndWait();
    }

    public static int getNextCount(int current, int total) {
        if (total > current + 1) {
            return current + 1;
        } else {
            return current + 1 - total;
        }
    }

    public static int getPreCount(int current, int total) {
        if (current - 1 >= 0) {
            return current - 1;
        } else {
            return total + current - 1;
        }
    }
}
