package com.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.example.utils.ConcreteHouse;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    private static final String ALGORITHM = "AES"; 
    private static final String KEY_FILE = "secretKey.key"; 
    static SecretKey key;
    private static  HouseCollection houseCollection = new HouseList();
    private static  HouseCollection houseCollection2 = new HouseList();
    private static  HouseCollection houseCollection3 = new HouseList();
    private static final TableView<House> tableView = new TableView<>();
    private static final ObservableList<House> tableData = FXCollections.observableArrayList();
    private static final TextArea outputArea = new TextArea();
    private static final VBox inputArea = new VBox(10); // Область ввода
    private static final Map<String, TextField> inputFields = new HashMap<>();
    private static ExcelExporter e=ExcelExporter.getInstance();
    Set<House> houseSet = new HashSet<>();
    String txtFilename = "houses.txt";
    String xmlFilename = "houses.xml";
    String jsonFilename = "houses.json";
    String encryptedFilename = "encrypted.txt";

    private static final ListView<String> listView = new ListView<>();


    // Загрузка ключа из файла
    public static SecretKey loadKeyFromFile() throws IOException {
        byte[] keyBytes = new byte[32]; 
        FileInputStream fis = new FileInputStream(KEY_FILE);
        fis.read(keyBytes);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("House Management System");

        // Создаем основное окно
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Область кнопок
        VBox buttonBox = createButtonPanel();
        VBox outputAndTableBox = new VBox(10); // Контейнер для outputArea и таблицы
        outputAndTableBox.setPadding(new Insets(10));

        // Настройка outputArea
        outputArea.setPrefHeight(150); // Устанавливаем высоту для outputArea
        outputArea.setEditable(false);

        // Добавляем outputArea и таблицу в VBox
        outputAndTableBox.getChildren().addAll(outputArea, tableView,listView);
        createTableView();
        createListView();

        // Область для вывода результатов
        outputArea.setPrefHeight(200);
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        House basicHouse = new ConcreteHouse(1, "fkuyuui", 100, 4, 200000);

        House discountedHouse = new DiscountDecorator(basicHouse, 0.2);
        outputArea.appendText("Original House Price: " + basicHouse.getPrice()+"\n");
        outputArea.appendText("Discounted House Price: " + discountedHouse.getPrice()+"\n");
        outputArea.appendText(discountedHouse.toString()+"\n");
        try {
            key = loadKeyFromFile();
            outputArea.appendText("Ключ загружен из файла.");
        } catch (IOException e) {
            outputArea.appendText("Ключ не найден, генерируем новый.");    
            key = EncryptionUtils.generateKey();
            outputArea.appendText("Новый ключ сгенерирован и сохранен.");
        }
        

        houseSet.addAll(FileManager.readFromTxt(txtFilename));
        houseSet.addAll(FileManager.readFromXML(xmlFilename));
        houseSet.addAll(FileManager.readFromJSON(jsonFilename));
        houseCollection.getAllHouses().addAll(houseSet);
        houseSet.clear();
        // Область ввода
        inputArea.setPadding(new Insets(10));
        createInputFields(); // Создаем поля ввода

        root.setLeft(buttonBox);
        root.setCenter(outputAndTableBox);
        root.setBottom(inputArea);

        // Создаем сцену
        Scene scene = new Scene(root, 1000, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void createListView() {
        listView.setPrefHeight(200); // Устанавливаем высоту списка
        listView.setPlaceholder(new Label("Список домов пуст")); // Сообщение, если список пуст
    }
    // Создаем панель с кнопками
    private VBox createButtonPanel() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        String[] buttonLabels = {
                "Добавить дом", "Показать все дома", "Сохранить и зашифровать данные", "Прочитать и расшифровать данные",
                "Сохранить данные и создать архив (ZIP)", "Удалить дом", "Обновить дом", "Сортировать по цене",
                "Сортировать по площади", "Сохранить дома в файл", "Прочитать дома из файла",
                "Записать в таблицу", "Чтение из таблицы", "Выход"
        };

        for (String label : buttonLabels) {
            Button button = new Button(label);
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(e -> handleButtonClick(label));
            vbox.getChildren().add(button);
        }

        return vbox;
    }

    // Обрабатываем нажатие на кнопки
    private void handleButtonClick(String action) {
        outputArea.clear();
        tableData.clear();
        listView.getItems().clear();
        switch (action) {
            case "Добавить дом":
                addHouse();
                inputFields.get("id").clear();
                inputFields.get("type").clear();
                inputFields.get("area").clear();
                inputFields.get("floors").clear();
                inputFields.get("price").clear();
                break;
            case "Показать все дома":
                printAllHouses();
                break;
            case "Сохранить и зашифровать данные":
                saveAndEncryptData();
                break;
            case "Прочитать и расшифровать данные":
                readAndDecryptData();
                break;
            case "Сохранить данные и создать архив (ZIP)":
                saveDataAndCreateZip();
                break;
            case "Удалить дом":
                removeHouse();
                inputFields.get("id").clear();
                inputFields.get("type").clear();
                inputFields.get("area").clear();
                inputFields.get("floors").clear();
                inputFields.get("price").clear();
                break;
            case "Обновить дом":
                updateHouse();
                inputFields.get("id").clear();
                inputFields.get("type").clear();
                inputFields.get("area").clear();
                inputFields.get("floors").clear();
                inputFields.get("price").clear();
                break;
            case "Сортировать по цене":
                sortHousesByPrice();
                break;
            case "Сортировать по площади":
                sortHousesByArea();
                break;
            case "Сохранить дома в файл":
                saveToFile(txtFilename, xmlFilename, jsonFilename);
                break;
            case "Прочитать дома из файла":
                readFromFile(txtFilename, xmlFilename, jsonFilename);
                break;
            case "Записать в таблицу":
                List<House> housesEXL = houseCollection.getAllHouses();
                e.exportToExcel(housesEXL);
                outputArea.appendText("Данные успешно экспортированы в файл houses.xlsx\n");
                break;
            case "Чтение из таблицы":
                List<House> houses34= e.importFromExcel("houses.xlsx");
                Set<House> h2=houseSet;
                h2.addAll(houses34);
                houseCollection.getAllHouses().clear();
                houseCollection.getAllHouses().addAll(h2);
                outputArea.appendText("Данные успешно импортированы из файла houses.xlsx\n");
                h2.clear();
                printAllHouses();
                break;
            case "Выход":
                Alert exitAlert = new Alert(AlertType.CONFIRMATION);
                exitAlert.setTitle("Подтверждение выхода");
                exitAlert.setHeaderText("Вы точно хотите выйти?");
                exitAlert.setContentText("Выберите 'Да' для выхода или 'Нет' для продолжения работы.");
        
                // Добавляем кнопки Да и Нет
                ButtonType yesButton = new ButtonType("Да");
                ButtonType noButton = new ButtonType("Нет");
        
                exitAlert.getButtonTypes().setAll(yesButton, noButton);
        
                // Показываем диалоговое окно и ждём ответа пользователя
                Optional<ButtonType> result = exitAlert.showAndWait();
        
                // Проверяем, что нажал пользователь
                if (result.isPresent() && result.get() == yesButton) {
                    try {
                        saveKeyToFile(key);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    System.exit(0);
                }else{
                    exitAlert.hide();
                }
                
                break;
            
        }
    }
    private static void saveAndEncryptData() {
        FileManager.writeEncryptedToTxt("encrypted.txt", houseCollection.getAllHouses(), key);
        outputArea.appendText("Данные успешно зашифрованы и записаны в файл encrypted.txt\n");
        outputArea.appendText("Данные успешно зашифрованы и сохранены.\n");
    }
    private static void readAndDecryptData() {
        List<House> decryptedHouses = FileManager.readDecryptedFromTxt("encrypted.txt", key);
        outputArea.appendText("Данные успешно расшифрованы и прочитаны из файла encrypted.txt\n");
        outputArea.appendText("Дешифрованные данные:\n");
        houseCollection2.getAllHouses().addAll(decryptedHouses);
        printAllHouses2();
    }
    private static void printAllHouses2() {
        List<House> houses = houseCollection2.getAllHouses();
        if (houses.isEmpty()) {
            outputArea.appendText("Дома отсутствуют.\n");
        } else {
            updateTableData(houses);
        }
    }
    private static void saveDataAndCreateZip() {
        FileManager.saveDataWithEncryptionAndZip(houseCollection.getAllHouses());
        outputArea.appendText("Данные заархивированы в ZIP файл.\n");
    }
    // Добавление дома с использованием ввода
    private void addHouse() {
        try {
            int id = Integer.parseInt(inputFields.get("id").getText());
            String type = inputFields.get("type").getText();
            int area = Integer.parseInt(inputFields.get("area").getText());
            int floors = Integer.parseInt(inputFields.get("floors").getText());
            double price = Double.parseDouble(inputFields.get("price").getText());

            House newHouse = new ConcreteHouse(id, type, area, floors, price);
            houseCollection.addHouse(newHouse);
            outputArea.appendText("Дом успешно добавлен: " + newHouse + "\n");

        } catch (Exception e) {
            outputArea.appendText("Ошибка ввода данных. Проверьте корректность введенных значений.\n");
        }
    }

    // Удаление дома
    private void removeHouse() {
        try {
            int id = Integer.parseInt(inputFields.get("id").getText());
            houseCollection.removeHouse(id);
            outputArea.appendText("Дом с ID " + id + " удален.\n");
        } catch (Exception e) {
            outputArea.appendText("Ошибка при удалении дома.\n");
        }
    }
    private static void updateHouse() {
        outputArea.appendText("Введите id дома для обновления:\n");
        int id = Integer.parseInt(inputFields.get("id").getText());
        outputArea.appendText("Введите новый тип дома:\n");
        outputArea.appendText("Введите новую площадь дома:\n");
        outputArea.appendText("Введите новое количество этажей:\n");
        outputArea.appendText("Введите новую цену дома:\n");
        House house = houseCollection.getHouseById(id);
        String type = inputFields.get("type").getText();
        int area = Integer.parseInt(inputFields.get("area").getText());
        int floors = Integer.parseInt(inputFields.get("floors").getText());
        double price = Double.parseDouble(inputFields.get("price").getText());

        if (house != null) {
            house.setType(type);
            house.setArea(area);
            house.setFloors(floors);
            house.setPrice(price);
        } else {
            outputArea.appendText("Дом с таким id не найден.\n");
        }
    }
    // Сортировка по цене
    private static void sortHousesByPrice() {
        List<House> houses = houseCollection.getAllHouses();
        houses.sort(Comparator.comparingDouble(House::getPrice));
        outputArea.appendText("Дома отсортированы по цене:\n");
        printAllHouses();
    }
    
    // Сортировка по площади
    private static void sortHousesByArea() {
        List<House> houses = houseCollection.getAllHouses();
        houses.sort(Comparator.comparingInt(House::getArea));
        outputArea.appendText("Дома отсортированы по площади:\n");
        printAllHouses();
    }
    private static void saveToFile(String txtFilename, String xmlFilename, String jsonFilename) {
        List<House> houses = houseCollection.getAllHouses();
        // FileManager.writeToTxt(txtFilename, houses);
        Thread txtWriter = new Thread(() -> {
            FileManager.writeToTxt(txtFilename, houses);
            Platform.runLater(() -> outputArea.appendText("Данные успешно записаны в TXT файл.\n"));
        });
        Thread jsonandxmlWriter = new Thread(() -> {
        FileManager.writeToJSON(jsonFilename, houses);
        Platform.runLater(() -> outputArea.appendText("Данные успешно записаны в JSON файл.\n"));
        FileManager.writeToXML(xmlFilename, houses);
        Platform.runLater(() -> outputArea.appendText("Данные успешнозаписаны в XML файл.\n"));
        });

        jsonandxmlWriter.start();
        txtWriter.start();
        try {
            jsonandxmlWriter.join();
            txtWriter.join();
            Platform.runLater(()->outputArea.appendText("Данные успешно сохранены в файлы.\n"));
        } catch (InterruptedException e) {
            Platform.runLater(() -> outputArea.appendText("Ошибка параллельного чтения данных.\n"));
        }
        // outputArea.appendText("Данные успешно записаны в TXT файл.\n");
        // FileManager.writeToXML(xmlFilename, houses);
        // outputArea.appendText("Данные успешно записаны в XML файл.\n");
        // FileManager.writeToJSON(jsonFilename, houses);
        // outputArea.appendText("Данные успешно записаны в JSON файл.\n");
        // outputArea.appendText("Данные успешно сохранены в файлы.\n");
    }
    private static void readFromFile(String txtFilename, String xmlFilename, String jsonFilename) {
        Set<House> houseSet2 = new HashSet<>();
        Thread jsonandxmlReader = new Thread(() -> {
            List<House> housesFromJson = FileManager.readFromJSON(jsonFilename);
            synchronized (houseSet2) {
                houseSet2.addAll(housesFromJson);
            }
            Platform.runLater(() -> outputArea.appendText("Данные успешно прочитаны из JSON файла.\n"));
            List<House> housesFromXml = FileManager.readFromXML(xmlFilename);
            synchronized (houseSet2) {
                houseSet2.addAll(housesFromXml);
            }
            Platform.runLater(() -> outputArea.appendText("Данные успешно прочитаны из Xml файла.\n"));
        });
        Thread txtReader = new Thread(()->{
            List<House> housesFromTxt = FileManager.readFromTxt(txtFilename);
            synchronized (houseSet2) {
                houseSet2.addAll(housesFromTxt);
            }
            Platform.runLater(() -> outputArea.appendText("Данные успешно прочитаны из Txt файла.\n"));
        });
        jsonandxmlReader.start();
        txtReader.start();
        // houseSet.addAll(FileManager.readFromTxt(txtFilename));
        // houseSet.addAll(FileManager.readFromXML(xmlFilename));
        // houseSet.addAll(FileManager.readFromJSON(jsonFilename));
        houseCollection.getAllHouses().clear();
        try {
            jsonandxmlReader.join();
            txtReader.join();
            houseCollection.getAllHouses().addAll(houseSet2);
            Platform.runLater(()->outputArea.appendText("Данные успешно прочитаны из файлов.\n"));
        } catch (InterruptedException e) {
            Platform.runLater(() -> outputArea.appendText("Ошибка параллельного чтения данных.\n"));
        }
        houseSet2.clear();

        // outputArea.appendText("Данные успешно прочитаны из файлов.\n");
        printAllHouses();
    }
    // Печать всех домов
    private static void printAllHouses() {
        List<House> houses = houseCollection.getAllHouses();
        if (houses.isEmpty()) {
            outputArea.appendText("Дома отсутствуют.\n");
        } else {
            updateTableData(houses);
        }
    }
    private static void printAllHouses3() {
        List<House> houses = houseCollection3.getAllHouses();
        if (houses.isEmpty()) {
            outputArea.appendText("Дома отсутствуют.\n");
        } else {
            updateTableData(houses);
        }
    }
    // Создание полей ввода
    private void createInputFields() {
        String[] labels = {"id", "type", "area", "floors", "price"};

        for (String label : labels) {
            HBox fieldBox = new HBox(5);
            Label fieldLabel = new Label(label + ":");
            TextField textField = new TextField();
            inputFields.put(label, textField);
            fieldBox.getChildren().addAll(fieldLabel, textField);
            inputArea.getChildren().add(fieldBox);
        }
    }
    private void createTableView() {
        // Колонка ID
        TableColumn<House, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
    
        // Колонка Type
        TableColumn<House, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
    
        // Колонка Area
        TableColumn<House, String> areaColumn = new TableColumn<>("Area");
        areaColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getArea())));
    
        // Колонка Floors
        TableColumn<House, String> floorsColumn = new TableColumn<>("Floors");
        floorsColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getFloors())));
    
        // Колонка Price
        TableColumn<House, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPrice())));
    
        // Добавляем колонки в таблицу
        tableView.getColumns().addAll(idColumn, typeColumn, areaColumn, floorsColumn, priceColumn);
        tableView.setItems(tableData); // Привязываем данные
    }
    private static void updateTableData(List<House> houses) {
        tableData.clear();
        tableData.addAll(houses);
        listView.getItems().clear();
        for (House house : houses) {
            listView.getItems().add(house.toString());
        }
    }
    public static void saveKeyToFile(SecretKey secretKey) throws IOException {
        FileOutputStream fos = new FileOutputStream(KEY_FILE);
        fos.write(secretKey.getEncoded());
    }
    
}
