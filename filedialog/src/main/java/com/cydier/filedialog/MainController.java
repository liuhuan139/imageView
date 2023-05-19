package com.cydier.filedialog;

import com.cydier.filedialog.manager.ThreadBuilder;
import com.cydier.filedialog.utils.ControllerUtils;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MainController extends BaseController {
    @FXML
    private MenuItem mFilerChooserItem;
    @FXML
    private MenuItem mMultiFilerChooserItem;
    @FXML
    private ImageView mPicView;
    private FileChooser mFileChooser;
    @FXML
    private Button mNextBtn;
    @FXML
    private Button mPreBtn;
    private ObservableList<Image> imageList;
    private int picNumLocation = -1;
    private static final int SAVE_IMG_TASK_ID = 999;
    private static final int TEST_TASK_ID = 666;

    public void initView(Scene scene) {
        initFileChooser();
        initFileChooseMenuItem(scene);
        initImageView(scene);
        initImageBtn();
    }

    private void initImageBtn() {
        mNextBtn.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
            if (imageList == null || imageList.size() < 2) {
                return false;
            }
            return true;
        }, imageList));
        mPreBtn.visibleProperty().bind(Bindings.createBooleanBinding(() -> {
            if (imageList == null || imageList.size() < 2) {
                return false;
            }
            return true;
        }, imageList));

        mNextBtn.visibleProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                mNextBtn.setOnMouseClicked(mouseEvent -> {
                    int nextCount = ControllerUtils.getNextCount(picNumLocation, imageList.size());
                    mPicView.setImage(imageList.get(nextCount));
                    picNumLocation++;
                    if (picNumLocation == imageList.size()) {
                        picNumLocation = 0;
                    }
                });
            }
        });
        mPreBtn.visibleProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                mPreBtn.setOnMouseClicked(mouseEvent -> {
                    int preCount = ControllerUtils.getPreCount(picNumLocation, imageList.size());
                    mPicView.setImage(imageList.get(preCount));
                    picNumLocation--;
                    if (picNumLocation == -1) {
                        picNumLocation = imageList.size() - 1;
                    }
                });
            }

        });
    }


    private void initFileChooser() {
        imageList = FXCollections.observableArrayList();
        mFileChooser = new FileChooser();
        mFileChooser.setTitle("select file");
    }

    private void initImageView(Scene scene) {
        ContextMenu contextMenu = getImageContextMenu(scene);
        mPicView.setSmooth(true);
        mPicView.setOnContextMenuRequested(contextMenuEvent -> {
            if (contextMenu != null && mPicView.getImage() != null) {
                contextMenu.show(mPicView, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
            }
        });
    }

    private ContextMenu getImageContextMenu(Scene scene) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem saveItem = new MenuItem("保存文件");
        MenuItem copyItem = new MenuItem("复制图片");
        contextMenu.getItems().add(saveItem);
        contextMenu.getItems().add(copyItem);
        saveItem.setOnAction(actionEvent -> {
            ControllerUtils.addAllFileExtensionFilter(mFileChooser.getExtensionFilters());
            File file = mFileChooser.showSaveDialog(scene.getWindow());
            ControllerUtils.saveImageInThread(mPicView.getImage(), file, this, ThreadBuilder.ThreadType.SINGLE);
        });
        return contextMenu;
    }

    private void initFileChooseMenuItem(Scene scene) {
        mFilerChooserItem.setOnAction(actionEvent -> {
            ControllerUtils.addImageExtensionFilter(mFileChooser.getExtensionFilters());
            File file = mFileChooser.showOpenDialog(scene.getWindow());
            if (file == null) {
                return;
            }
            String imgPath = ControllerUtils.getFilePath(file);
            if (imgPath != null && imgPath.length() > 0) {
                if (imageList != null) {
                    imageList.clear();
                }
                mPicView.setImage(new Image(imgPath));
            }
        });

        mMultiFilerChooserItem.setOnAction(actionEvent -> {
            ControllerUtils.addImageExtensionFilter(mFileChooser.getExtensionFilters());
            List<File> images= mFileChooser.showOpenMultipleDialog(scene.getWindow());
            if (images == null || images.size() == 0) {
                return;
            }
            ControllerUtils.transformDataInList(imageList, images);
            if (imageList.size() >= 1) {
                mPicView.setImage(imageList.get(0));
                picNumLocation = 0;
            }

        });
    }

    @Override
    public void initStage(Stage stage) {
        stage.setOnCloseRequest(windowEvent -> System.exit(0));
    }


    @Override
    public void dealThreadCallBack(int reasonCode, Object result) {
        switch (reasonCode) {
            case SAVE_IMG_TASK_ID:
                Boolean ret = (Boolean) result;
                String information = ret ? "文件保存成功！" : "文件保存失败";
                ControllerUtils.showInformationDialog(information);
                break;
            case TEST_TASK_ID:
                System.out.println("哟西！result = " + result);
            default:
                break;
        }
    }

    /**
     * for test
     */
    private void testThread() {
        Callable callable = () -> {
            for (int i = 0; i < 1000; i++) {
                System.out.println("i = " + i);
            }
            return true;
        };
        ThreadBuilder.ThreadTask task = new ThreadBuilder.ThreadTask(callable);
        task.setTaskId(TEST_TASK_ID);
        ControllerUtils.executeTaskInThread(task, ThreadBuilder.ThreadType.SINGLE, this);
    }

    @Override
    public int getCallCode() {
        return SAVE_IMG_TASK_ID;
    }
}