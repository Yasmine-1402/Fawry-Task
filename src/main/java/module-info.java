// File: src/main/java/module-info.java

module com.example.fawry {
    // 1. You need to declare that your module requires the JavaFX modules it uses.
    requires javafx.controls;
    requires javafx.fxml;

    // 2. You must OPEN your UI package to the JavaFX framework.
    //    This is the direct fix for your error.
    //    It allows javafx.graphics to create your Application instance
    //    and javafx.fxml to inject into your FXML controllers.
    opens com.example.fawry.ui to javafx.graphics, javafx.fxml;

    // 3. You should EXPORT your model packages so JavaFX components (like TableView)
    //    can understand your data types (like Product, Customer).
    exports com.example.fawry.model;
    exports com.example.fawry.cart;
    exports com.example.fawry.interfaces;
}