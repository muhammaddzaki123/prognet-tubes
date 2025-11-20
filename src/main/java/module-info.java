module prognet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires com.google.gson;

    opens prognet to javafx.fxml;
    exports prognet;
    exports prognet.client;
    exports prognet.common;
}