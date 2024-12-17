package com.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.utils.ConcreteHouse;

public class ExcelExporter {
    private static ExcelExporter instance;
    private static final String XLSX_FILENAME = "houses.xlsx";

    private ExcelExporter() {
    }

    public static synchronized ExcelExporter getInstance() {
        if (instance == null) {
            instance = new ExcelExporter();
        }
        return instance;
    }

    public void exportToExcel(List<House> houses) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Houses");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Type", "Area", "Floors", "Price"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (House house : houses) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(house.getId());
                row.createCell(1).setCellValue(house.getType());
                row.createCell(2).setCellValue(house.getArea());
                row.createCell(3).setCellValue(house.getFloors());
                row.createCell(4).setCellValue(house.getPrice());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(XLSX_FILENAME)) {
                workbook.write(fos);
            }
        } catch (IOException e) {
        }
    }

    public List<House> importFromExcel(String filename) {
        List<House> houses = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filename)) {
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    int id = (int) row.getCell(0).getNumericCellValue();
                    String type = row.getCell(1).getStringCellValue();
                    int area = (int) row.getCell(2).getNumericCellValue();
                    int floors = (int) row.getCell(3).getNumericCellValue();
                    double price = row.getCell(4).getNumericCellValue();
                    houses.add(new ConcreteHouse(id, type, area, floors, price));
                }
            }
        } catch (IOException e) {
        }
        return houses;
    }
}
