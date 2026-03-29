package com.ecampus.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;

@Service
public class ExportToExcel {
    
    public void save(List<Object[]> studentdetails,
                    List<Object[]> coursedata,
                    List<Object[]> openfor,
                    List<Object[]> studentRequirements, 
                    List<Object[]> coursePreferences, 
                    List<Object[]> slotPreferences,
                    List<Object[]> instRequirements,
                    OutputStream outputStream) {
        
        try (Workbook workbook = new XSSFWorkbook()) {

            // --- SHEET 1: STUDENT DETAILS ---
            Sheet sheet1 = workbook.createSheet("StudentData");
            Row header1 = sheet1.createRow(0);
            header1.createCell(0).setCellValue("StudentID");
            header1.createCell(1).setCellValue("Name");
            header1.createCell(2).setCellValue("Program");
            header1.createCell(3).setCellValue("Semester");

            int rowIdx = 1;
            for (Object[] rowData : studentdetails) {
                Row row = sheet1.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowData[0] != null ? rowData[0].toString() : "");
                row.createCell(1).setCellValue(rowData[1] != null ? rowData[1].toString() : "");
                row.createCell(2).setCellValue(rowData[2] != null ? rowData[2].toString() : "");
                row.createCell(3).setCellValue(rowData[3] != null ? rowData[3].toString() : "");
            }

            // --- SHEET 2: COURSE DATA ---
            Sheet sheet2 = workbook.createSheet("CourseData");
            Row header2 = sheet2.createRow(0);
            header2.createCell(0).setCellValue("CourseID");
            header2.createCell(1).setCellValue("CourseName");
            header2.createCell(2).setCellValue("Credits");
            header2.createCell(3).setCellValue("Slot");

            rowIdx = 1;
            for (Object[] rowData : coursedata) {
                Row row = sheet2.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowData[0] != null ? rowData[0].toString() : "");
                row.createCell(1).setCellValue(rowData[1] != null ? rowData[1].toString() : "");
                row.createCell(2).setCellValue(rowData[2] != null ? rowData[2].toString() : "");
                row.createCell(3).setCellValue(rowData[3] != null ? rowData[3].toString() : "");
            }

            // --- SHEET 3: OPEN FOR ---
            Sheet sheet3 = workbook.createSheet("OpenFor");
            Row header3 = sheet3.createRow(0);
            header3.createCell(0).setCellValue("CourseID");
            header3.createCell(1).setCellValue("Program");
            header3.createCell(2).setCellValue("Semester");
            header3.createCell(3).setCellValue("Category");
            header3.createCell(4).setCellValue("Seats");

            rowIdx = 1;
            for (Object[] rowData : openfor) {
                Row row = sheet3.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowData[0] != null ? rowData[0].toString() : "");
                row.createCell(1).setCellValue(rowData[1] != null ? rowData[1].toString() : "");
                row.createCell(2).setCellValue(rowData[2] != null ? rowData[2].toString() : "");
                row.createCell(3).setCellValue(rowData[3] != null ? rowData[3].toString() : "");
                row.createCell(4).setCellValue(rowData[4] != null ? rowData[4].toString() : "");
            }
            
            // --- SHEET 4: STUDENT REQUIREMENTS ---
            Sheet sheet4 = workbook.createSheet("StudentRequirements");
            Row header4 = sheet4.createRow(0);
            header4.createCell(0).setCellValue("StudentID");
            header4.createCell(1).setCellValue("Category");
            header4.createCell(2).setCellValue("Count");

            rowIdx = 1;
            for (Object[] rowData : studentRequirements) {
                Row row = sheet4.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowData[0] != null ? rowData[0].toString() : "");
                row.createCell(1).setCellValue(rowData[1] != null ? rowData[1].toString() : "");
                row.createCell(2).setCellValue(rowData[2] != null ? rowData[2].toString() : "");
            }

            // --- SHEET 5: COURSE PREFERENCES ---
            Sheet sheet5 = workbook.createSheet("CoursePreferences");
            Row header5 = sheet5.createRow(0);
            header5.createCell(0).setCellValue("StudentID");
            header5.createCell(1).setCellValue("Slot");
            header5.createCell(2).setCellValue("CourseID");
            header5.createCell(3).setCellValue("PreferenceIndex");

            rowIdx = 1;
            for (Object[] rowData : coursePreferences) {
                Row row = sheet5.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowData[0] != null ? rowData[0].toString() : "");
                row.createCell(1).setCellValue(rowData[1] != null ? rowData[1].toString() : "");
                row.createCell(2).setCellValue(rowData[2] != null ? rowData[2].toString() : "");
                row.createCell(3).setCellValue(rowData[3] != null ? rowData[3].toString() : "");
            }

            // --- SHEET 6: SLOT PREFERENCES ---
            Sheet sheet6 = workbook.createSheet("SlotPreferences");
            Row header6 = sheet6.createRow(0);
            header6.createCell(0).setCellValue("StudentID");
            header6.createCell(1).setCellValue("SlotNo");
            header6.createCell(2).setCellValue("PreferenceIndex");

            rowIdx = 1;
            for (Object[] rowData : slotPreferences) {
                Row row = sheet6.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowData[0] != null ? rowData[0].toString() : "");
                row.createCell(1).setCellValue(rowData[1] != null ? rowData[1].toString() : "");
                row.createCell(2).setCellValue(rowData[2] != null ? rowData[2].toString() : "");
            }

            // --- SHEET 7: INSTITUTE REQUIREMENTS ---
            Sheet sheet7 = workbook.createSheet("InstituteRequirements");
            Row header7 = sheet7.createRow(0);
            header7.createCell(0).setCellValue("Program");
            header7.createCell(1).setCellValue("Semester");
            header7.createCell(2).setCellValue("Category");
            header7.createCell(3).setCellValue("Count");

            rowIdx = 1;
            for (Object[] rowData : instRequirements) {
                Row row = sheet7.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowData[0] != null ? rowData[0].toString() : "");
                row.createCell(1).setCellValue(rowData[1] != null ? rowData[1].toString() : "");
                row.createCell(2).setCellValue(rowData[2] != null ? rowData[2].toString() : "");
                row.createCell(3).setCellValue(rowData[3] != null ? rowData[3].toString() : "");
            }

            // Write directly to the browser's output stream
            workbook.write(outputStream);
            System.out.println("Excel file streamed to browser successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
