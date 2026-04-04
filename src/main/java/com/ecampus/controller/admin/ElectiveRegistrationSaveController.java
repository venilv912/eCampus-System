package com.ecampus.controller.admin;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecampus.model.*;
import com.ecampus.repository.*;

import org.apache.poi.ss.usermodel.*;

@Controller
@RequestMapping("/admin/electiveRegistrationSave")
public class ElectiveRegistrationSaveController {

    @Autowired
    private StudentsRepository studRepo;

    @Autowired
    private TermsRepository termRepo;

    @Autowired
    private SemestersRepository semRepo;

    @Autowired
    private StudentRegistrationsRepository stdRegRepo;

    @Autowired
    private TermCoursesRepository termCrsRepo;

    @Autowired
    private TermCourseAvailableForRepository termCrsAvailableForRepo;

    @Autowired
    private StudentRegistrationCoursesRepository studRegCrsRepo;

    @GetMapping
    public String showUploadPage() {
        return "admin/electiveRegistrationSave";
    }
    
    @PostMapping("/process")
    public String processExcel(@RequestParam("file") MultipartFile file, RedirectAttributes ra) {
        if (file.isEmpty()) {
            ra.addFlashAttribute("error", "Please select a file to upload.");
            return "redirect:/admin/electiveRegistrationSave";
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);

            DataFormatter formatter = new DataFormatter();
            
            // Example: Iterate through rows and print data to console
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                Cell cell1 = row.getCell(0);
                Cell cell2 = row.getCell(4);

                // Use the formatter to get the text exactly as it looks in Excel
                String stdinstid = formatter.formatCellValue(cell1);
                String crscode = formatter.formatCellValue(cell2);
                
                Long stdid = studRepo.findStdid(stdinstid);
                Long bchid = studRepo.findBatchIdByStudentId(stdinstid);
                Long trmid = termRepo.findMaxTrmid();
                Long strid = semRepo.findSemByBchAndTrm(bchid,trmid);
                StudentRegistrations stdReg = stdRegRepo.findByStudentIdAndSemesterId(stdid, strid);

                List<Object[]> tcrctp = termCrsRepo.findForElectiveRegSave(bchid, trmid, crscode);
                Object[] value = tcrctp.get(0);
                Long tcrid = ((Number) value[0]).longValue();
                Long ctpid = ((Number) value[1]).longValue();

                termCrsAvailableForRepo.incrementBookedCount(tcrid,bchid);

                Long srcid = studRegCrsRepo.findMaxSrcId();
                StudentRegistrationCourses src = new StudentRegistrationCourses();
                src.setSrcid(srcid+1);
                src.setSrcsrgid(stdReg.getSrgid());
                src.setSrctcrid(tcrid);
                src.setSrctype("REGULARADD");
                src.setSrcscrid(null);
                src.setSrcstatus("ACTIVE");
                src.setSrcfield1(null);
                src.setSrcfield2(null);
                src.setSrcfield3(null);
                src.setSrccreatedat(LocalDateTime.now());
                src.setSrccreatedby(3809L);
                src.setSrclastupdatedat(null);
                src.setSrclastupdatedby(null);
                src.setSrcrowstate(1L);
                src.setOrigCtpid(ctpid);
                src.setCurrCtpid(ctpid);
                studRegCrsRepo.save(src);
            }

            ra.addFlashAttribute("success", "File uploaded and processed successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error processing Excel file: " + e.getMessage());
        }

        return "redirect:/admin/electiveRegistrationSave";
    }

}
