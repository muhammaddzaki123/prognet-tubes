module prognet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires com.google.gson;

    opens prognet to javafx.fxml;
    opens prognet.controller to javafx.fxml;

    exports prognet;
    exports prognet.network.client;
    exports prognet.common;
}