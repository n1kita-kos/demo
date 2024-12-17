package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.utils.ConcreteHouse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class FileManager {

    // Чтение данных из TXT файла
    public static List<House> readFromTxt(String filename) {
        List<House> houses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                String type = parts[1].trim();
                int area = Integer.parseInt(parts[2].trim());
                int floors = Integer.parseInt(parts[3].trim());
                double price = Double.parseDouble(parts[4].trim());
                houses.add(new ConcreteHouse(id, type, area, floors, price));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return houses;
    }

    // Запись в TXT файл
    public static void writeToTxt(String filename, List<House> houses) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (House house : houses) {
                writer.write(house.getId() + "," + house.getType() + "," + house.getArea() + "," +
                        house.getFloors() + "," + house.getPrice() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Чтение из XML файла
    public static List<House> readFromXML(String filename) {
        List<House> houses = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filename));

            NodeList houseNodes = document.getElementsByTagName("house");

            for (int i = 0; i < houseNodes.getLength(); i++) {
                Node node = houseNodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    String type = element.getElementsByTagName("type").item(0).getTextContent();
                    int area = Integer.parseInt(element.getElementsByTagName("area").item(0).getTextContent());
                    int floors = Integer.parseInt(element.getElementsByTagName("floors").item(0).getTextContent());
                    double price = Double.parseDouble(element.getElementsByTagName("price").item(0).getTextContent());

                    houses.add(new ConcreteHouse(id, type, area, floors, price));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return houses;
    }

    // Запись в XML файл
    public static void writeToXML(String filename, List<House> houses) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement("houses");
            document.appendChild(root);

            for (House house : houses) {
                Element houseElement = document.createElement("house");

                Element id = document.createElement("id");
                id.appendChild(document.createTextNode(String.valueOf(house.getId())));
                houseElement.appendChild(id);

                Element type = document.createElement("type");
                type.appendChild(document.createTextNode(house.getType()));
                houseElement.appendChild(type);

                Element area = document.createElement("area");
                area.appendChild(document.createTextNode(String.valueOf(house.getArea())));
                houseElement.appendChild(area);

                Element floors = document.createElement("floors");
                floors.appendChild(document.createTextNode(String.valueOf(house.getFloors())));
                houseElement.appendChild(floors);

                Element price = document.createElement("price");
                price.appendChild(document.createTextNode(String.valueOf(house.getPrice())));
                houseElement.appendChild(price);

                root.appendChild(houseElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(filename));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(domSource, streamResult);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Чтение из JSON файла
    public static List<House> readFromJSON(String filename) {
        List<House> houses = new ArrayList<>();
        try (FileReader reader = new FileReader(filename)) {
            Gson gson = new Gson();
            Type houseListType = new TypeToken<ArrayList<ConcreteHouse>>() {}.getType();
            houses = gson.fromJson(reader, houseListType);
        } catch (Exception e) {
        }
        return houses;
    }

    // Запись в JSON файл
    public static void writeToJSON(String filename, List<House> houses) {
        try (FileWriter writer = new FileWriter(filename)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(houses, writer);
        } catch (Exception e) {
        }
    }

    // Шифрование и запись данных
    public static void writeEncryptedToTxt(String filename, List<House> houses, SecretKey secretKey) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            StringBuilder data = new StringBuilder();
            
            // Собираем все данные о домах в строку
            for (House house : houses) {
                String houseData = house.getId() + "," + house.getType() + "," + house.getArea() + "," +
                                   house.getFloors() + "," + house.getPrice() + "\n";
                data.append(houseData);
            }

            // Шифруем собранные данные
            String encryptedData = EncryptionUtils.encrypt(data.toString(), secretKey);

            // Записываем зашифрованные данные в файл
            writer.write(encryptedData);
        } catch (IOException e) {
        }
    }

    // Чтение и расшифровка данных
    public static List<House> readDecryptedFromTxt(String filename, SecretKey secretKey) {
        List<House> houses = new ArrayList<>();
    
        if (secretKey == null) {
            return houses; // Возврат пустого списка
        }
    
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Чтение зашифрованного текста из файла
            StringBuilder encryptedData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                encryptedData.append(line);
            }
    
            if (encryptedData.length() == 0) {
                return houses; // Возврат пустого списка
            }
    
            // Дешифровка данных
            String decryptedData = EncryptionUtils.decrypt(encryptedData.toString(), secretKey);
    
            // Восстановление объектов House из дешифрованных данных
            String[] houseLines = decryptedData.split("\n");
            for (String houseLine : houseLines) {
                String[] parts = houseLine.split(",");
                if (parts.length < 5) {
                    continue; // Пропустите эту строку, если данных недостаточно
                }
    
                try {
                    int id = Integer.parseInt(parts[0].trim());
                    String type = parts[1].trim();
                    int area = Integer.parseInt(parts[2].trim());
                    int floors = Integer.parseInt(parts[3].trim());
                    double price = Double.parseDouble(parts[4].trim());
    
                    houses.add(new ConcreteHouse(id, type, area, floors, price));
                } catch (NumberFormatException e) {
                }
            }
    
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    
        return houses;
    }
    
    // Сохранение данных и создание ZIP архива
    public static void saveDataWithEncryptionAndZip(List<House> houses) {
        String encryptedFileName = "houses.txt";

        try (FileOutputStream fos = new FileOutputStream("houses.zip");
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            File fileToZip = new File(encryptedFileName);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.closeEntry();
            fis.close();

            
        } catch (IOException e) {

        }
    }
    SecretKey generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // Размер ключа AES - 128 бит
            return keyGen.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
