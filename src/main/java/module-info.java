module com.example {
    requires java.base;
    requires java.logging; // Для Gson
    requires java.xml;      // Для Gson
    requires java.desktop;  // Для работы с графическим интерфейсом и Apache POI
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    opens com.example to com.google.gson;
    requires org.apache.poi.ooxml;

    exports com.example;
    exports com.example.utils;
}
