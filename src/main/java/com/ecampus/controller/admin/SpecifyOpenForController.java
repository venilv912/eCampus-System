package com.ecampus.controller.admin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecampus.model.*;
import com.ecampus.repository.*;
import com.ecampus.service.SpecifyOpenForService;

@Controller
@RequestMapping("/admin/openfor")
public class SpecifyOpenForController {

    @Autowired
    private TermsRepository TermRepo;

    @Autowired
    private BatchesRepository BatchRepo;

    @Autowired
    private SchemeDetailsRepository SchemeDetailsRepo;

    @Autowired
    private SpecifyOpenForService OpenForService;
    
    @GetMapping
    public String bchAndprg(Model model){

        // Finding the batches for the new Term to display in the dropdown
        Long currTermId = TermRepo.findMaxTrmid();
        List<String> programs = SchemeDetailsRepo.findProgramsByTrm(currTermId);

        List<String> batches = BatchRepo.findBatchesByTrmId(currTermId);

        model.addAttribute("programs", programs);
        model.addAttribute("batches", batches);
        model.addAttribute("currTermId", currTermId);
        return "admin/openfor";
    }

    @GetMapping("/getBatches")
    @ResponseBody
    public List<String> getBatches(@RequestParam String prgname) {
        Long currTermId = TermRepo.findMaxTrmid();

        // Finding the batches for the selected program from the dropdown to display in the batches dropdown
        return BatchRepo.findBatchBySplnTrm(prgname, currTermId);
        //return SchemeDetailsRepo.findProgramsByBatch(bchname); 
    }

    @PostMapping("/load")
    public String loadData(@RequestParam String batch, 
                           @RequestParam String program, 
                           @RequestParam Long termId, 
                           Model model) {
        
        prepareCourseData(batch, program, termId, model);

        
        model.addAttribute("selectedBatch", batch);
        model.addAttribute("selectedProgram", program);
        model.addAttribute("selectedTermId", termId);

        return "admin/openfor :: coursesSection"; 
    }

    @PostMapping("/chooseElective")
    public String chooseElective(@RequestParam String batch,
                                 @RequestParam String program,
                                 @RequestParam Long termId,
                                 @RequestParam String electiveCode,
                                 @RequestParam String electiveName,
                                 Model model) {

        Long SchemeId = OpenForService.getSchemeIdFromSplName(program);
        Long SplId = OpenForService.getSplIdFromSplName(program);
        Long BchId = OpenForService.getBchIdFromBchSchSpl(batch, SchemeId, SplId);

        List<TermCourseAvailableFor> courses = OpenForService.getCrsForBch(termId,BchId);
        
        List<TermCourseAvailableFor> coursesNULL = courses.stream()
                .filter(c -> c.getTcaelectivetype() == null)
                .collect(Collectors.toList());

        model.addAttribute("courses", coursesNULL);
        model.addAttribute("batch", batch);
        model.addAttribute("program", program);
        model.addAttribute("termId", termId);
        model.addAttribute("electiveCode", electiveCode);
        model.addAttribute("electiveName", electiveName);
        
        return "admin/chooseElective";
    }

    @PostMapping("/saveElective")
    public String saveElective(@RequestParam String batch,
                               @RequestParam String program,
                               @RequestParam Long termId,
                               @RequestParam String electiveCode,
                               @RequestParam(value = "selectedTcaIds", required = false) List<Long> selectedTcaIds,
                               RedirectAttributes redirectAttributes) {
        
        if (selectedTcaIds != null) {
            OpenForService.updateElectiveType(selectedTcaIds, electiveCode);
        }

        redirectAttributes.addFlashAttribute("batch", batch);
        redirectAttributes.addFlashAttribute("program", program);
        redirectAttributes.addFlashAttribute("termId", termId);
        
        return "redirect:/admin/openfor/reload";
    }

    @GetMapping("/reload")
    public String reloadPage(@ModelAttribute("batch") String batch,
                             @ModelAttribute("program") String program,
                             @ModelAttribute("termId") Long termId,
                             Model model) {
        // Re-setup initial dropdowns
        Long currTermId = TermRepo.findMaxTrmid();
        List<String> programs = SchemeDetailsRepo.findProgramsByTrm(currTermId);
        model.addAttribute("programs", programs);
        model.addAttribute("currTermId", currTermId);

        // Pre-set the view with data
        prepareCourseData(batch, program, termId, model);

        // IMPORTANT: Populate these so the "Add Course" links work after reload
        model.addAttribute("selectedBatch", batch);
        model.addAttribute("selectedProgram", program);
        model.addAttribute("selectedTermId", termId);

        // For the Javascript window.onload
        model.addAttribute("preSelectedBatch", batch);
        model.addAttribute("preSelectedProgram", program);
        
        return "admin/openfor";
    }

    @PostMapping("/reload")
    public String reloadPost(String batch,
                             String program,
                             Long termId,
                             RedirectAttributes redirectAttributes) {
                                
        redirectAttributes.addFlashAttribute("batch", batch);
        redirectAttributes.addFlashAttribute("program", program);
        redirectAttributes.addFlashAttribute("termId", termId);

        return "redirect:/admin/openfor/reload";
    }

    @PostMapping("/updateSeats")
    public String updateSeats(@RequestParam Long tcaId,
                              @RequestParam Long seats,
                              @RequestParam String batch,
                              @RequestParam String program,
                              @RequestParam Long termId,
                              Model model) {
        
        // Call service to update seats
        OpenForService.updateTcaSeats(tcaId, seats);
        
        // Standard fragment refresh logic
        prepareCourseData(batch, program, termId, model);
        model.addAttribute("selectedBatch", batch);
        model.addAttribute("selectedProgram", program);
        model.addAttribute("selectedTermId", termId);

        return "admin/openfor :: coursesSection";
    }

    @PostMapping("/remove")
    public String removeCourse(@RequestParam Long tcaId,
                               @RequestParam String batch,
                               @RequestParam String program,
                               @RequestParam Long termId,
                               Model model) {
        
        System.out.println("Removing now:" + tcaId + " " + batch + " " + program + " " + termId);

        // Set the elective type to null for this specific record
        OpenForService.updateElectiveType(List.of(tcaId), null);
        
        // Prepare the updated data for the fragment
        prepareCourseData(batch, program, termId, model);
        
        // Provide the variables needed for the "Add Course" links in the fragment
        model.addAttribute("selectedBatch", batch);
        model.addAttribute("selectedProgram", program);
        model.addAttribute("selectedTermId", termId);

        return "admin/openfor :: coursesSection";
    }

    private void prepareCourseData(String batch, String program, Long termId, Model model) {
        Long SchemeId = OpenForService.getSchemeIdFromSplName(program);
        Long SplId = OpenForService.getSplIdFromSplName(program);
        Long BchId = OpenForService.getBchIdFromBchSchSpl(batch, SchemeId, SplId);

        List<TermCourseAvailableFor> courses = OpenForService.getCrsForBch(termId,BchId);

        if(courses.isEmpty()){
            OpenForService.addTCAfromTCR(termId,BchId);
            //courses = OpenForService.getCrsForBch(termId,BchId);
        }

        Long semNo = OpenForService.getSemNoByBchTrm(batch,termId);

        // List of Code + Name like {('ICTE','ICT Elective'), ('TE','Technical Elective'), ('SE','Science Electives'), ...}
        List<Object[]> electiveType = OpenForService.getTypeElectBySchSplSem(SchemeId, SplId, semNo);
        electiveType.sort(Comparator.comparing(o -> ((String) o[0])));

        // Group the courses by their Code i.e. ICTE, TE, ...(Handling nulls by mapping them to a "NULL" string)
        Map<String, List<TermCourseAvailableFor>> groupedByCode = courses.stream()
            .collect(Collectors.groupingBy(
                c -> c.getTcaelectivetype() == null ? "NULL" : c.getTcaelectivetype()
            ));

        // Create the final map that links the full ElectiveType (Code + Name) to the List
        // We use LinkedHashMap to preserve the order found in electiveType
        Map<Object[], List<TermCourseAvailableFor>> finalCoursesMap = new LinkedHashMap<>();

        for (Object[] type : electiveType) {
            String code = (String) type[0];
            // Get the list from our grouping, or an empty list if no courses match
            List<TermCourseAvailableFor> matches = groupedByCode.getOrDefault(code, new ArrayList<>());
            finalCoursesMap.put(type, matches);
        }

        // Add the NULL entries as a separate category
        List<TermCourseAvailableFor> coursesNULL = groupedByCode.getOrDefault("NULL", new ArrayList<>());


        model.addAttribute("electiveGroupedMap", finalCoursesMap);
        model.addAttribute("coursesNULL", coursesNULL);
    }

}
