package com.joseja.glassdoorscraper;

import java.io.*;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelManager {

    String pathToFile;

    public ExcelManager(String fileName) throws FileNotFoundException, IOException {
        pathToFile = fileName + ".xlsx";
        boolean fileExists = new File(pathToFile).isFile();
        if (!fileExists) {
            //Blank workbook
            XSSFWorkbook workbook = new XSSFWorkbook();
            //Create a blank sheet
            XSSFSheet sheet = workbook.createSheet("Compañias");
            XSSFRow row = sheet.createRow(0);  // Create info row.
            XSSFCell cell1 = row.createCell(0);
            cell1.setCellValue("Compañía");
            XSSFCell cell2 = row.createCell(1);
            cell2.setCellValue("Reviews");
            XSSFCell cell3 = row.createCell(2);
            cell2.setCellValue("Overall");
            XSSFCell cell4 = row.createCell(3);
            cell3.setCellValue("Culture & Values");
            XSSFCell cell5 = row.createCell(4);
            cell4.setCellValue("Work/Life Balance");
            XSSFCell cell6 = row.createCell(5);
            cell5.setCellValue("Senior Management");
            XSSFCell cell7 = row.createCell(6);
            cell6.setCellValue("Comp & Benefits");
            XSSFCell cell8 = row.createCell(7);
            cell7.setCellValue("Career Opportunities");

            try (FileOutputStream out = new FileOutputStream(new File(pathToFile))) {
                workbook.write(out);
            }
            System.out.println("Fichero de datos creado satisfactoriamente");
        } else {
            System.out.println("El fichero de datos ya existe");
        }
    }

    public int getLastRowIndex() throws FileNotFoundException, IOException {
        FileInputStream file = new FileInputStream(new File(pathToFile));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet spreadsheet = workbook.getSheetAt(0);
        return (spreadsheet.getLastRowNum() + 1);
    }

    public boolean write(TreeMap<String, ArrayList<Float>> companies) throws FileNotFoundException, IOException {
        FileInputStream fileIn = new FileInputStream(new File(pathToFile));
        XSSFWorkbook workbook = new XSSFWorkbook(fileIn);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int lastIndex = getLastRowIndex();
        int numberOfCompanies = companies.size();
        for (int i = 0; i < numberOfCompanies; i++) {
            XSSFRow row = sheet.createRow(lastIndex);  // Create info row.
            Entry entry = companies.pollFirstEntry();
            ArrayList<Float> entryValue = (ArrayList<Float>)entry.getValue();
            XSSFCell Companycell = row.createCell(0);
            Companycell.setCellValue((String)entry.getKey());
            for (int j = 1; j < entryValue.size() + 1; j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(entryValue.get(j - 1));
            }
            lastIndex++;
        }
        try (FileOutputStream fileOut = new FileOutputStream(pathToFile)) {
            workbook.write(fileOut);
        }
        System.out.println("Fichero de datos actualizado satisfactoriamente");
        return true;
    }
}
