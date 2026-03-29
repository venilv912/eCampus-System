package com.ecampus.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.ecampus.repository.*;
import com.ecampus.service.*;

@Controller
@RequestMapping("/admin/electiveRegistrationDownload")
public class ElectiveRegistrationDownloadController {

    @Autowired
    private TermsRepository TermRepo;

    @Autowired
    private BatchesRepository BatchRepo;

    @Autowired
    private SchemeDetailsRepository SchemeDetailsRepo;

    @Autowired
    private StudentsRepository studRepo;

    @Autowired
    private TermCoursesRepository termCoursesRepo;

    @Autowired
    private TermCourseAvailableForRepository termCourseAvailableForRepo;

    @Autowired
    private SchemeCoursesRepository schemeCoursesRepo;

    @Autowired
    private StudentCourseRequirementsRepository studCrsReqRepo;

    @Autowired
    private CoursePreferencesRepository crsPrefRepo;

    @Autowired
    private SlotPreferencesRepository slotPrefRepo;

    @Autowired
    private ExportToExcel exportToExcel;

    @GetMapping
    public String chooseBchAndPrg(Model model) {

        Long currTermId = TermRepo.findMaxTrmid();
        List<String> programs = SchemeDetailsRepo.findMainProgramsByTrm(currTermId);

        List<String> batches = BatchRepo.findBatchesByTrmId(currTermId);

        model.addAttribute("programs", programs);
        model.addAttribute("batches", batches);
        model.addAttribute("currTermId", currTermId);

        return "admin/electiveRegistrationDownload";

    }

    @GetMapping("/getBatches")
    @ResponseBody
    public List<String> getBatches(@RequestParam String prgname) {
        Long currTermId = TermRepo.findMaxTrmid();

        // Finding the batches for the selected program from the dropdown to display in the batches dropdown
        return BatchRepo.findBatchBySplnTrm(prgname, currTermId);
        //return SchemeDetailsRepo.findProgramsByBatch(bchname); 
    }
    
    @GetMapping("/giveExcel")
    public void giveExcel( @RequestParam String batch, 
                           @RequestParam String program, 
                           @RequestParam Long termId,
                           HttpServletResponse response) throws IOException {

        List<Object[]> studentdetails = studRepo.findStudentRegistrationDetails(batch,program,termId);
        List<Object[]> coursedata = termCoursesRepo.findElectiveCoursesByTerm(termId);
        List<Object[]> openfor = termCourseAvailableForRepo.findAvailableElectivesDetails(termId,batch,program);
        List<Object[]> instrequirements = schemeCoursesRepo.findRequirementsForPrgInSem(batch,program,termId);

        List<Object[]> studentRequirements = studCrsReqRepo.getStudReqForElectiveRegistration();
        List<Object[]> coursePreferences = crsPrefRepo.getCrsPrefForElectiveRegistration();
        List<Object[]> slotPreferences = slotPrefRepo.getSlotPrefForElectiveRegistration();

        // 1. Set the content type for Excel
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        
        // 2. Set the header to trigger the download dialog
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Elective_Registration.xlsx";
        response.setHeader(headerKey, headerValue);

        // 3. Pass the response output stream to the service
        exportToExcel.save(studentdetails, coursedata, openfor, studentRequirements, coursePreferences, slotPreferences, instrequirements, response.getOutputStream());

    }

}
