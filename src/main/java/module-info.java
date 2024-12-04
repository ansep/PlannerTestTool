module it.uniroma1.plannertests {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.cli;
    requires org.json;

    opens it.uniroma1.plannertests to javafx.fxml;

    exports it.uniroma1.plannertests;

    exports it.uniroma1.plannertests.controller;

    opens it.uniroma1.plannertests.controller to javafx.fxml;
}
