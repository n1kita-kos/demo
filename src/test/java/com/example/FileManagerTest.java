package com.example;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.example.utils.ConcreteHouse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FileManagerTest {

    @Test
    public void testWriteToFile() throws IOException {
        Path tempFile = Files.createTempFile("houses2", ".txt");

        List<House> houseList = new ArrayList<>();
        houseList.add(new ConcreteHouse(1, "Кирпичный", 120, 2, 3000000));
        houseList.add(new ConcreteHouse(2, "Деревянный", 80, 1, 1500000));

        FileManager.writeToTxt(tempFile.toString(), houseList);

        List<String> fileContent = Files.readAllLines(tempFile);

        assertEquals(2, fileContent.size());
        assertTrue(fileContent.get(0).contains("Кирпичный"));
        assertTrue(fileContent.get(1).contains("Деревянный"));

        Files.deleteIfExists(tempFile);
    }

    @Test
    public void testWriteToJsonFile() throws IOException {
    Path tempFile = Files.createTempFile("houses2", ".json");

    List<House> houseList = new ArrayList<>();
    houseList.add(new ConcreteHouse(1, "Кирпичный", 120, 2, 3000000));
    houseList.add(new ConcreteHouse(2, "Деревянный", 80, 1, 1500000));

    FileManager.writeToJSON(tempFile.toString(), houseList);

    String fileContent = Files.readString(tempFile);
    System.out.println("Содержимое JSON файла: \n" + fileContent); 

    Gson gson = new Gson();
    List<House> parsedHouses = gson.fromJson(fileContent, new TypeToken<List<ConcreteHouse>>(){}.getType());

    assertEquals(2, parsedHouses.size());
    assertEquals("Кирпичный", parsedHouses.get(0).getType());
    assertEquals("Деревянный", parsedHouses.get(1).getType());

    Files.deleteIfExists(tempFile);
}


    @Test
    public void testWriteToXmlFile() throws IOException {
        Path tempFile = Files.createTempFile("houses2", ".xml");

        List<House> houseList = new ArrayList<>();
        houseList.add(new ConcreteHouse(1, "Кирпичный", 120, 2, 3000000));
        houseList.add(new ConcreteHouse(2, "Деревянный", 80, 1, 1500000));

        FileManager.writeToXML(tempFile.toString(), houseList);

        String fileContent = Files.readString(tempFile);

        assertTrue(fileContent.contains("<type>Кирпичный</type>"));
        assertTrue(fileContent.contains("<type>Деревянный</type>"));

        Files.deleteIfExists(tempFile);
    }
}