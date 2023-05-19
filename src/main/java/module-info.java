module com.cydier.filedialog {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.cydier.filedialog to javafx.fxml;
    exports com.cydier.filedialog;
}