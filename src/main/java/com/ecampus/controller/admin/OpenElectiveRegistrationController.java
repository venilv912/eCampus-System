package com.ecampus.controller.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecampus.repository.BatchesRepository;
import com.ecampus.repository.TermsRepository;
import com.ecampus.service.OpenRegistrationService;

@Controller
@RequestMapping("/admin/electiveregistrationopen")
public class OpenElectiveRegistrationController {

    @Autowired
    private TermsRepository TermRepo;

    @Autowired
    private BatchesRepository BatchRepo;

    @Autowired
    private OpenRegistrationService OpenRegistrationService;
    
    @GetMapping
    public String openregistrationfor(Model model){

        Long currTermId = TermRepo.findMaxTrmid();
        List<String> batches = BatchRepo.findBatchesByTrmId(currTermId);

        List<Object[]> bchSpl = OpenRegistrationService.getBatchSpl(batches,currTermId, "Elective");

        Map<String, List<Object[]>> ugPgMap = 
            bchSpl.stream()
                .collect(Collectors.groupingBy(row -> {
                    Long schemeId = ((Number) row[2]).longValue();
                    return OpenRegistrationService.getUGPG(schemeId);
                }));

        model.addAttribute("ugPgMap", ugPgMap);

        return "admin/electiveOpenFor";
    }

    @PostMapping("/saveelectiveregistrationdate")
    public String saveRegistrationDate(
            @RequestParam("startDate") String startDateStr,
            @RequestParam("endDate") String endDateStr,
            @RequestParam(value = "selectedBchIds", required = false) List<Long> bchIds,
            RedirectAttributes redirectAttributes) {

        if (bchIds == null || bchIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select at least one batch.");
            return "redirect:/admin/electiveregistrationopen";
        }
        else if(startDateStr.isEmpty() || endDateStr.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "Please select both dates.");
            return "redirect:/admin/electiveregistrationopen";
        }
        
        LocalDateTime startDate = LocalDateTime.parse(startDateStr);
        LocalDateTime endDate = LocalDateTime.parse(endDateStr);

        if (startDate.isAfter(endDate) || startDate.isEqual(endDate)) {
            redirectAttributes.addFlashAttribute("error", "Start Date should be before End Date.");
            return "redirect:/admin/electiveregistrationopen";
        }
        else if(endDate.isBefore(LocalDateTime.now())){
            redirectAttributes.addFlashAttribute("error", "End Date should be after current date and time.");
            return "redirect:/admin/electiveregistrationopen";
        }

        // Logic: Convert Strings to LocalDateTime and save to your Registration table
        System.out.println("Start: " + startDateStr);
        System.out.println("End: " + endDateStr);
        System.out.println("Selected Batches: " + bchIds);

        Long currTermId = TermRepo.findMaxTrmid();
        OpenRegistrationService.saveRegistrationDate(startDate,endDate,currTermId,bchIds,"Elective");
        
        redirectAttributes.addFlashAttribute("success", "Elective Registration dates saved successfully!");
        return "redirect:/admin/electiveregistrationopen";
    }
}
